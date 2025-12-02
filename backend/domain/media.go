// backend/domain/media.go
package domain

type Media struct {
	MediaType string `json:"media_type"` // image / video
	URL       string `json:"url,omitempty"`
	CoverURL  string `json:"cover_url,omitempty"`
	Duration  int    `json:"duration,omitempty"`
	Width     int    `json:"width,omitempty"`
	Height    int    `json:"height,omitempty"`
}
