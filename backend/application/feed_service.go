package application

import (
	"context"
	"fmt"
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
	cursor *int64,
	refreshTime *int64,
	limit int,
) (*FeedResponse, error) {

	// ① 首次加载
	if cursor == nil && refreshTime == nil {
		// 🎯 修复: 接收最新的发布时间
		items, next, latestTime, err := s.repo.ListInitial(ctx)
		if err != nil {
			return nil, err
		}
		return &FeedResponse{
			Items:             items,
			NextCursor:        next,
			HasMore:           next != nil,
			LatestPublishTime: latestTime, // 🎯 赋值
		}, nil
	}

	// ② 加载更多
	if cursor != nil {
		// 🎯 修复: 接收最新的发布时间
		items, next, latestTime, err := s.repo.ListFeed(ctx, cursor, limit)
		if err != nil {
			return nil, err
		}
		return &FeedResponse{
			Items:             items,
			NextCursor:        next,
			HasMore:           next != nil,
			LatestPublishTime: latestTime, // 🎯 赋值
		}, nil
	}

	// ③ 下拉刷新
	if refreshTime != nil {
		items, err := s.repo.ListNewer(ctx, *refreshTime)
		if err != nil {
			return nil, err
		}

		// 🎯 逻辑优化: 下拉刷新返回的列表中的第一项就是最新的时间戳。
		var latestTime int64 = 0
		if len(items) > 0 {
			latestTime = items[0].PublishTime
		}

		return &FeedResponse{
			Items:             items,
			NextCursor:        nil,
			HasMore:           false,
			LatestPublishTime: latestTime, // 🎯 赋值
		}, nil
	}

	return nil, fmt.Errorf("invalid feed parameters")
}

// ---------------- DTO ------------------

type FeedResponse struct {
	Items             []domain.FeedItem `json:"items"`
	NextCursor        *int64            `json:"next_cursor"`
	HasMore           bool              `json:"has_more"`
	LatestPublishTime int64             `json:"latest_publish_time"`
}
