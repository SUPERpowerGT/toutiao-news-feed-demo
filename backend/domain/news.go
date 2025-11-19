package domain

import "time"

type News struct {
	ID          int64     `json:"id"`
	Title       string    `json:"title"`
	Summary     string    `json:"summary"`
	NewsType    string    `json:"news_type"`
	AuthorID    int64     `json:"author_id"`
	Source      string    `json:"source"`
	Category    string    `json:"category"`
	PublishTime time.Time `json:"publish_time"`
	Status      int       `json:"status"`
	CreatedAt   time.Time `json:"created_at"`
	UpdatedAt   time.Time `json:"updated_at"`
}
