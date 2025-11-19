package application

import (
	"context"
	"encoding/base64"
	"strconv"
	"toutiao-backend/domain"
)

type FeedService struct {
	repo domain.FeedItemRepository
}

func NewFeedService(repo domain.FeedItemRepository) *FeedService {
	return &FeedService{repo: repo}
}

func encodeCursor(id *int64) string {
	if id == nil {
		return ""
	}
	return base64.RawURLEncoding.EncodeToString([]byte(strconv.FormatInt(*id, 10)))
}

func decodeCursor(s string) (*int64, error) {
	if s == "" {
		return nil, nil
	}
	b, err := base64.RawURLEncoding.DecodeString(s)
	if err != nil {
		return nil, err
	}
	v, err := strconv.ParseInt(string(b), 10, 64)
	if err != nil {
		return nil, err
	}
	return &v, nil
}

type FeedResult struct {
	Items      []domain.FeedItem `json:"items"`
	NextCursor string            `json:"next_cursor,omitempty"`
}

func (s *FeedService) GetFeed(ctx context.Context, cursor string, limit int) (*FeedResult, error) {
	cur, err := decodeCursor(cursor)
	if err != nil {
		return nil, err
	}

	items, nextID, err := s.repo.ListFeed(ctx, cur, limit)
	if err != nil {
		return nil, err
	}

	result := &FeedResult{Items: items}

	if nextID != nil {
		result.NextCursor = encodeCursor(nextID)
	}

	return result, nil
}
