// backend/domain/stats.go
package domain

type Stats struct {
	LikeCount     int `json:"like_count"`
	CommentCount  int `json:"comment_count"`
	FavoriteCount int `json:"favorite_count"`
	ShareCount    int `json:"share_count"`
}
