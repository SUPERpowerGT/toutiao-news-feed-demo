package domain

import "time"

type FeedItem struct {
	ID          int64     `json:"id"`
	NewsID      int64     `json:"news_id"`
	DisplayType string    `json:"display_type"`
	Weight      float64   `json:"weight"`
	Scene       string    `json:"scene"`
	ModelID     string    `json:"model_id"`
	PublishTime time.Time `json:"publish_time"`
	SeqID       int64     `json:"seq_id"`
	CreatedAt   time.Time `json:"created_at"`
}
