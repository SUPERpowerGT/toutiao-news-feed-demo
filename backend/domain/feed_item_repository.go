package domain

import "context"

// package domain

type FeedItemRepository interface {

	// ① 首页首次加载（5 条官方 + 15 条普通）
	// 🎯 修复: 增加 int64 (最新的发布时间) 返回值
	ListInitial(ctx context.Context) ([]FeedItem, *int64, int64, error)

	// ② 上滑加载更多（cursor-based）
	// 🎯 修复: 增加 int64 (最新的发布时间) 返回值
	ListFeed(ctx context.Context, cursor *int64, limit int) ([]FeedItem, *int64, int64, error)

	// ③ 下拉刷新（返回比 refreshTime 更新的内容）
	ListNewer(ctx context.Context, refreshTime int64) ([]FeedItem, error)
}
