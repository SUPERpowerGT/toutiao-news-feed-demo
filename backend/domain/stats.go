package domain

import "time"

type Stats struct {
	NewsID        int64     `json:"news_id"`
	LikeCount     int       `json:"like_count"`
	CommentCount  int       `json:"comment_count"`
	FavoriteCount int       `json:"favorite_count"`
	ShareCount    int       `json:"share_count"`
	PlayCount     int       `json:"play_count"`
	Version       int       `json:"version"`
	UpdatedAt     time.Time `json:"updated_at"`
}
