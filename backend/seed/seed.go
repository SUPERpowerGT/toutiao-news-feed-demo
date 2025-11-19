package seed

import (
	"database/sql"
	"fmt"
)

func InsertSeedData(db *sql.DB) error {
	// ---------- 1. 插入 author ----------
	authorSQL := `
        INSERT INTO author (name, avatar_url, description, certification)
        VALUES 
        ('Alice', 'https://example.com/avatar1.png', 'Tech Reporter', 'Verified'),
        ('Bob', 'https://example.com/avatar2.png', 'Finance Writer', 'Pro');
    `
	_, err := db.Exec(authorSQL)
	if err != nil {
		return fmt.Errorf("insert authors error: %w", err)
	}

	// ---------- 2. 插入 news ----------
	newsSQL := `
        INSERT INTO news (title, summary, news_type, author_id, source, category, publish_time)
        VALUES 
        ('Breaking News 1', 'Summary for news 1', 'text', 1, 'BBC', 'World', NOW() - INTERVAL '1 minute'),
        ('Breaking News 2', 'Summary for news 2', 'image', 2, 'CNN', 'Tech', NOW() - INTERVAL '2 minute'),
        ('Breaking News 3', 'Summary for news 3', 'text', 1, 'NYTimes', 'Finance', NOW() - INTERVAL '3 minute');
    `
	_, err = db.Exec(newsSQL)
	if err != nil {
		return fmt.Errorf("insert news error: %w", err)
	}

	// ---------- 3. 插入 feed_item ----------
	// seq_id 会自增，越大的越新
	feedSQL := `
        INSERT INTO feed_item (news_id, display_type, weight, scene, model_id, publish_time)
        VALUES
        (1, 'text', 0.8, 'home', 'model_x', NOW() - INTERVAL '10 second'),
        (2, 'image', 0.9, 'home', 'model_x', NOW() - INTERVAL '20 second'),
        (3, 'text', 1.0, 'home', 'model_x', NOW() - INTERVAL '30 second');
    `
	_, err = db.Exec(feedSQL)
	if err != nil {
		return fmt.Errorf("insert feed_item error: %w", err)
	}

	return nil
}
