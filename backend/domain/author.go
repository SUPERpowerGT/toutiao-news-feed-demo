package domain

import "time"

type Author struct {
	ID            int64     `json:"id"`
	Name          string    `json:"name"`
	AvatarURL     string    `json:"avatar_url"`
	Description   string    `json:"description"`
	Certification string    `json:"certification"`
	CreatedAt     time.Time `json:"created_at"`
}
