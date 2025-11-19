package domain

import "time"

type Media struct {
	ID         int64     `json:"id"`
	NewsID     int64     `json:"news_id"`
	GroupID    int64     `json:"group_id"`
	MediaType  string    `json:"media_type"`
	URL        string    `json:"url"`
	CoverURL   string    `json:"cover_url"`
	Duration   int       `json:"duration"`
	Width      int       `json:"width"`
	Height     int       `json:"height"`
	OrderIndex int       `json:"order_index"`
	CreatedAt  time.Time `json:"created_at"`
}
