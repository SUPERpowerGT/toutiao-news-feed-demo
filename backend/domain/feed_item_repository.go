package domain

import "context"

// package domain

type FeedItemRepository interface {

	//首页首次加载（5 条官方 + 15 条普通）
	ListInitial(ctx context.Context) ([]FeedItem, *int64, int64, error)

	//上滑加载更多（cursor-based）
	ListFeed(ctx context.Context, cursor *int64, limit int) ([]FeedItem, *int64, int64, error)

	//下拉刷新（返回比 refreshTime 更新的内容）
	ListNewer(ctx context.Context, refreshTime int64) ([]FeedItem, error)
}
