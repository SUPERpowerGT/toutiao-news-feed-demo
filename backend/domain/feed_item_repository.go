package domain

import "context"

type FeedItemRepository interface {
	ListFeed(ctx context.Context, cursor *int64, limit int) ([]FeedItem, *int64, error)
}
