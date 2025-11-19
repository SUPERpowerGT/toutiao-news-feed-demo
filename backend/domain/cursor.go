package domain

import "time"

type NewsCursor struct {
	PublishTime time.Time
	ID          int64
}
