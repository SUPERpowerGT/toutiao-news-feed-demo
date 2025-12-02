// backend/domain/feed_item.go
package domain

type FeedItem struct {
	ID int64 `json:"id"` // news_id

	Title       string `json:"title"`
	Summary     string `json:"summary,omitempty"`
	ContentType string `json:"content_type"`

	Media  []Media `json:"media"`
	Author Author  `json:"author"`
	Stats  Stats   `json:"stats"`

	PublishTime int64    `json:"publish_time"`
	Category    string   `json:"category"`
	SubCategory string   `json:"sub_category"`
	Tags        []string `json:"tags"`
	City        string   `json:"city"`

	IsOfficial    bool    `json:"is_official_media"`
	IsTopOfficial bool    `json:"is_top_official"`
	Source        string  `json:"source"`
	Weight        float64 `json:"weight"`
}
