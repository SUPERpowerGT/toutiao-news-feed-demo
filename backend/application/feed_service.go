package application

import (
	"context"
	"fmt"
	"math"
	"strings"
	"time"
	"toutiao-backend/domain"
)

type FeedService struct {
	repo domain.FeedItemRepository
}

func NewFeedService(r domain.FeedItemRepository) *FeedService {
	return &FeedService{repo: r}
}

/*
三种模式统一入口：
- cursor == nil && refreshTime == nil → 首次加载
- cursor != nil → 加载更多
- cursor == nil && refreshTime != nil → 下拉刷新
*/
func (s *FeedService) GetFeed(
	ctx context.Context,
	scene string,
	cursor *int64,
	refreshTime *int64,
	limit int,
) (*FeedResponse, error) {
	scene = normalizeScene(scene)

	//首次加载
	if cursor == nil && refreshTime == nil {
		topItems, normalItems, next, latestTime, err := s.repo.ListInitial(ctx, scene)
		if err != nil {
			return nil, err
		}

		applyRecommendationReasons(scene, topItems)
		applyRecommendationReasons(scene, normalItems)

		items := mergeForScene(scene, topItems, normalItems)
		return &FeedResponse{
			Scene:             scene,
			TopItems:          topItems,
			Items:             items,
			NextCursor:        next,
			HasMore:           next != nil,
			LatestPublishTime: latestTime,
		}, nil
	}

	//加载更多
	if cursor != nil {
		items, next, latestTime, err := s.repo.ListFeed(ctx, scene, cursor, limit)
		if err != nil {
			return nil, err
		}
		applyRecommendationReasons(scene, items)
		return &FeedResponse{
			Scene:             scene,
			TopItems:          []domain.FeedItem{},
			Items:             items,
			NextCursor:        next,
			HasMore:           next != nil,
			LatestPublishTime: latestTime,
		}, nil
	}

	//下拉刷新
	if refreshTime != nil {
		topItems, normalItems, err := s.repo.ListNewer(ctx, scene, *refreshTime)
		if err != nil {
			return nil, err
		}

		applyRecommendationReasons(scene, topItems)
		applyRecommendationReasons(scene, normalItems)

		items := mergeForScene(scene, topItems, normalItems)

		//逻辑优化: 下拉刷新返回的列表中的第一项就是最新的时间戳。
		var latestTime int64 = 0
		if len(items) > 0 {
			latestTime = items[0].PublishTime
		}

		return &FeedResponse{
			Scene:             scene,
			TopItems:          topItems,
			Items:             items,
			NextCursor:        nil,
			HasMore:           false,
			LatestPublishTime: latestTime,
		}, nil
	}

	return nil, fmt.Errorf("invalid feed parameters")
}

// ---------------- DTO ------------------

type FeedResponse struct {
	Scene             string            `json:"scene"`
	TopItems          []domain.FeedItem `json:"top_items"`
	Items             []domain.FeedItem `json:"items"`
	NextCursor        *int64            `json:"next_cursor"`
	HasMore           bool              `json:"has_more"`
	LatestPublishTime int64             `json:"latest_publish_time"`
}

func normalizeScene(scene string) string {
	switch scene {
	case "following", "hot", "video", "shenzhen", "featured", "image", "war", "tech", "sports", "finance":
		return scene
	default:
		return "recommend"
	}
}

func mergeForScene(scene string, topItems, normalItems []domain.FeedItem) []domain.FeedItem {
	if scene == "recommend" {
		return append(append([]domain.FeedItem{}, topItems...), normalItems...)
	}
	return normalItems
}

func applyRecommendationReasons(scene string, items []domain.FeedItem) {
	for i := range items {
		items[i].RecommendScore = calculateRecommendationScore(scene, items[i])
		items[i].Reason = buildRecommendationReason(scene, items[i])
	}
}

func buildRecommendationReason(scene string, item domain.FeedItem) string {
	switch {
	case item.IsTopOfficial:
		return "权威发布"
	case item.IsOfficial:
		return "官方媒体推荐"
	case item.Stats.CommentCount >= 120 || item.Stats.LikeCount >= 800:
		return "热门讨论"
	case item.PublishTime > 0 && isFreshItem(item.PublishTime):
		return "最新更新"
	case scene == "recommend" && strings.EqualFold(item.ContentType, "video"):
		return "视频内容优先展示"
	case scene == "video":
		return "视频频道精选"
	case scene == "following":
		return "来自你的关注"
	case scene == "hot":
		return "全站热榜内容"
	case scene == "shenzhen":
		return "深圳本地热点"
	case scene == "featured":
		return "编辑精选内容"
	case scene == "image":
		return "高清图片内容"
	case scene == "war":
		return "抗战历史专题"
	case scene == "tech":
		return "科技频道精选"
	case scene == "sports":
		return "体育频道精选"
	case scene == "finance":
		return "财经频道精选"
	case item.Category != "":
		return item.Category + "内容持续热读"
	default:
		return "为你推荐"
	}
}

func isFreshItem(publishTime int64) bool {
	const freshWindowSeconds = 30 * 60
	return publishTime >= time.Now().Unix()-freshWindowSeconds
}

func calculateRecommendationScore(scene string, item domain.FeedItem) float64 {
	now := time.Now().Unix()
	ageSeconds := float64(maxInt64(0, now-item.PublishTime))

	weightScore := clampFloat64(item.Weight, 0, 1)
	freshnessScore := 1 / (1 + ageSeconds/3600.0)

	engagementRaw := float64(item.Stats.LikeCount)*0.45 +
		float64(item.Stats.CommentCount)*0.35 +
		float64(item.Stats.ShareCount)*0.12 +
		float64(item.Stats.FavoriteCount)*0.08
	engagementScore := math.Min(1.0, math.Log1p(engagementRaw)/6.0)

	officialBoost := 0.0
	if item.IsTopOfficial {
		officialBoost = 0.18
	} else if item.IsOfficial {
		officialBoost = 0.10
	}

	sceneBoost := 0.0
	switch scene {
	case "recommend":
		if strings.EqualFold(item.ContentType, "video") {
			sceneBoost = 0.05
		}
	case "video":
		if strings.EqualFold(item.ContentType, "video") {
			sceneBoost = 0.10
		}
	case "image":
		if strings.EqualFold(item.ContentType, "image") {
			sceneBoost = 0.10
		}
	case "shenzhen":
		if item.City == "深圳" {
			sceneBoost = 0.10
		}
	case "tech":
		if item.Category == "科技" {
			sceneBoost = 0.10
		}
	case "sports":
		if item.Category == "体育" {
			sceneBoost = 0.10
		}
	case "finance":
		if item.Category == "财经" {
			sceneBoost = 0.10
		}
	case "following", "hot", "featured", "war":
		if item.Category == sceneCategory(scene) {
			sceneBoost = 0.10
		}
	}

	score := weightScore*0.40 + freshnessScore*0.30 + engagementScore*0.20 + officialBoost + sceneBoost
	return roundTo(score, 3)
}

func sceneCategory(scene string) string {
	switch scene {
	case "following":
		return "关注"
	case "hot":
		return "热榜"
	case "featured":
		return "精选"
	case "war":
		return "抗战"
	default:
		return ""
	}
}

func clampFloat64(v, min, max float64) float64 {
	if v < min {
		return min
	}
	if v > max {
		return max
	}
	return v
}

func roundTo(v float64, decimals int) float64 {
	pow := math.Pow10(decimals)
	return math.Round(v*pow) / pow
}

func maxInt64(a, b int64) int64 {
	if a > b {
		return a
	}
	return b
}
