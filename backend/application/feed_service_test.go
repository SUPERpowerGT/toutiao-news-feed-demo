package application

import (
	"context"
	"errors"
	"testing"
	"time"

	"toutiao-backend/domain"
)

type fakeFeedRepository struct {
	initialTop    []domain.FeedItem
	initialNormal []domain.FeedItem
	initialNext   *int64
	initialLatest int64
	feedItems     []domain.FeedItem
	feedNext      *int64
	feedLatest    int64
	newerTop      []domain.FeedItem
	newerNormal   []domain.FeedItem
	err           error
	lastScene     string
	lastLimit     int
}

func (f *fakeFeedRepository) ListInitial(_ context.Context, scene string) ([]domain.FeedItem, []domain.FeedItem, *int64, int64, error) {
	f.lastScene = scene
	return cloneItems(f.initialTop), cloneItems(f.initialNormal), f.initialNext, f.initialLatest, f.err
}

func (f *fakeFeedRepository) ListFeed(_ context.Context, scene string, _ *int64, limit int) ([]domain.FeedItem, *int64, int64, error) {
	f.lastScene = scene
	f.lastLimit = limit
	return cloneItems(f.feedItems), f.feedNext, f.feedLatest, f.err
}

func (f *fakeFeedRepository) ListNewer(_ context.Context, scene string, _ int64) ([]domain.FeedItem, []domain.FeedItem, error) {
	f.lastScene = scene
	return cloneItems(f.newerTop), cloneItems(f.newerNormal), f.err
}

func cloneItems(items []domain.FeedItem) []domain.FeedItem {
	return append([]domain.FeedItem(nil), items...)
}

func testItem(id int64, options ...func(*domain.FeedItem)) domain.FeedItem {
	item := domain.FeedItem{
		ID:          id,
		Title:       "item",
		ContentType: "text",
		PublishTime: time.Now().Add(-2 * time.Hour).Unix(),
		Category:    "社会",
		Weight:      0.5,
	}
	for _, option := range options {
		option(&item)
	}
	return item
}

func TestGetFeedInitialRecommendMergesAndAnnotatesItems(t *testing.T) {
	next := int64(100)
	repo := &fakeFeedRepository{
		initialTop: []domain.FeedItem{testItem(1, func(item *domain.FeedItem) {
			item.IsTopOfficial = true
		})},
		initialNormal: []domain.FeedItem{testItem(2, func(item *domain.FeedItem) {
			item.Stats.LikeCount = 900
		})},
		initialNext:   &next,
		initialLatest: 200,
	}

	response, err := NewFeedService(repo).GetFeed(context.Background(), "recommend", nil, nil, 15)
	if err != nil {
		t.Fatalf("GetFeed returned error: %v", err)
	}
	if len(response.TopItems) != 1 || len(response.Items) != 2 {
		t.Fatalf("unexpected item counts: top=%d items=%d", len(response.TopItems), len(response.Items))
	}
	if response.Items[0].Reason != "权威发布" || response.Items[1].Reason != "热门讨论" {
		t.Fatalf("unexpected recommendation reasons: %q, %q", response.Items[0].Reason, response.Items[1].Reason)
	}
	if response.Items[0].RecommendScore <= 0 || !response.HasMore || response.LatestPublishTime != 200 {
		t.Fatalf("unexpected response metadata: %+v", response)
	}
}

func TestGetFeedNonRecommendDoesNotMergeTopItems(t *testing.T) {
	repo := &fakeFeedRepository{
		initialTop:    []domain.FeedItem{testItem(1)},
		initialNormal: []domain.FeedItem{testItem(2, func(item *domain.FeedItem) { item.ContentType = "video" })},
	}

	response, err := NewFeedService(repo).GetFeed(context.Background(), "video", nil, nil, 15)
	if err != nil {
		t.Fatalf("GetFeed returned error: %v", err)
	}
	if repo.lastScene != "video" || len(response.Items) != 1 || response.Items[0].ID != 2 {
		t.Fatalf("scene filtering was not preserved: scene=%q response=%+v", repo.lastScene, response)
	}
	if response.Items[0].Reason != "视频频道精选" {
		t.Fatalf("unexpected video recommendation reason: %q", response.Items[0].Reason)
	}
}

func TestGetFeedLoadMoreForwardsLimitAndCursorMetadata(t *testing.T) {
	next := int64(50)
	cursor := int64(100)
	repo := &fakeFeedRepository{
		feedItems:  []domain.FeedItem{testItem(3)},
		feedNext:   &next,
		feedLatest: 90,
	}

	response, err := NewFeedService(repo).GetFeed(context.Background(), "tech", &cursor, nil, 7)
	if err != nil {
		t.Fatalf("GetFeed returned error: %v", err)
	}
	if repo.lastLimit != 7 || response.NextCursor == nil || *response.NextCursor != next || !response.HasMore {
		t.Fatalf("load-more metadata was not preserved: limit=%d response=%+v", repo.lastLimit, response)
	}
}

func TestGetFeedRefreshUsesNewestReturnedPublishTime(t *testing.T) {
	refreshTime := int64(100)
	repo := &fakeFeedRepository{
		newerNormal: []domain.FeedItem{
			testItem(1, func(item *domain.FeedItem) { item.PublishTime = 300 }),
			testItem(2, func(item *domain.FeedItem) { item.PublishTime = 200 }),
		},
	}

	response, err := NewFeedService(repo).GetFeed(context.Background(), "recommend", nil, &refreshTime, 15)
	if err != nil {
		t.Fatalf("GetFeed returned error: %v", err)
	}
	if response.LatestPublishTime != 300 || response.HasMore || response.NextCursor != nil {
		t.Fatalf("unexpected refresh metadata: %+v", response)
	}
}

func TestGetFeedNormalizesUnknownSceneForServiceCallers(t *testing.T) {
	repo := &fakeFeedRepository{}
	response, err := NewFeedService(repo).GetFeed(context.Background(), "unknown", nil, nil, 15)
	if err != nil {
		t.Fatalf("GetFeed returned error: %v", err)
	}
	if repo.lastScene != "recommend" || response.Scene != "recommend" {
		t.Fatalf("scene was not normalized: repo=%q response=%q", repo.lastScene, response.Scene)
	}
}

func TestGetFeedPropagatesRepositoryError(t *testing.T) {
	repo := &fakeFeedRepository{err: errors.New("database unavailable")}
	if _, err := NewFeedService(repo).GetFeed(context.Background(), "recommend", nil, nil, 15); err == nil {
		t.Fatal("expected repository error")
	}
}

func TestRecommendationScoreRewardsMatchingOfficialContent(t *testing.T) {
	base := testItem(1, func(item *domain.FeedItem) {
		item.ContentType = "video"
		item.PublishTime = time.Now().Unix()
	})
	official := base
	official.IsTopOfficial = true

	baseScore := calculateRecommendationScore("video", base)
	officialScore := calculateRecommendationScore("video", official)
	if officialScore <= baseScore {
		t.Fatalf("official score %.3f should exceed base score %.3f", officialScore, baseScore)
	}
	if officialScore < 0 || officialScore > 1.78 {
		t.Fatalf("score %.3f is outside the documented scoring range", officialScore)
	}
}
