// backend/domain/author.go
package domain

type Author struct {
	ID            int64  `json:"id"`
	Name          string `json:"name"`
	AvatarURL     string `json:"avatar_url,omitempty"`
	Certification string `json:"certification,omitempty"` // red_v / yellow_v / null
}
