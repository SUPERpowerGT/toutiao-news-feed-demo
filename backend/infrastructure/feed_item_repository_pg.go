package infrastructure

import (
	"context"
	"database/sql"
	"toutiao-backend/domain"
)

type PGFeedItemRepository struct {
	db *sql.DB
}

func NewPGFeedItemRepository(db *sql.DB) *PGFeedItemRepository {
	return &PGFeedItemRepository{db: db}
}

func (r *PGFeedItemRepository) ListFeed(
	ctx context.Context,
	cursor *int64,
	limit int,
) ([]domain.FeedItem, *int64, error) {

	const baseSQL = `
        SELECT id, news_id, display_type, weight, scene, model_id,
               publish_time, seq_id, created_at
        FROM feed_item
    `

	var rows *sql.Rows
	var err error

	if cursor == nil {
		rows, err = r.db.QueryContext(ctx, baseSQL+`
            ORDER BY seq_id DESC
            LIMIT $1
        `, limit)
	} else {
		rows, err = r.db.QueryContext(ctx, baseSQL+`
            WHERE seq_id < $1
            ORDER BY seq_id DESC
            LIMIT $2
        `, *cursor, limit)
	}

	if err != nil {
		return nil, nil, err
	}
	defer rows.Close()

	list := make([]domain.FeedItem, 0)

	for rows.Next() {
		var f domain.FeedItem
		if err := rows.Scan(
			&f.ID,
			&f.NewsID,
			&f.DisplayType,
			&f.Weight,
			&f.Scene,
			&f.ModelID,
			&f.PublishTime,
			&f.SeqID,
			&f.CreatedAt,
		); err != nil {
			return nil, nil, err
		}
		list = append(list, f)
	}

	if len(list) == 0 {
		return list, nil, nil
	}

	nextCursor := list[len(list)-1].SeqID

	return list, &nextCursor, nil
}
