package infrastructure

import (
	"context"
	"database/sql"
	"fmt"
	"strings"

	"toutiao-backend/domain"
)

type FeedItemRepositoryPG struct {
	db *sql.DB
}

func NewFeedItemRepositoryPG(db *sql.DB) *FeedItemRepositoryPG {
	return &FeedItemRepositoryPG{db: db}
}

// 首页首次加载 Top5 + Normal15
func (r *FeedItemRepositoryPG) ListInitial(ctx context.Context) ([]domain.FeedItem, *int64, int64, error) {

	sqlTop := `
SELECT
	n.id AS news_id,
	n.title,
	n.summary,
	f.content_type,
	f.category,
	f.sub_category,
	f.tags,
	f.city,
	f.is_official_media,
	f.is_top_official,
	f.source,
	EXTRACT(EPOCH FROM f.publish_time)::bigint AS publish_time,
	f.weight,

	a.id AS author_id,
	a.name AS author_name,
	a.avatar_url AS author_avatar,
	a.certification AS author_cert,

	s.like_count,
	s.comment_count,
	s.favorite_count,
	s.share_count
FROM feed_item f
JOIN news n ON n.id = f.news_id
LEFT JOIN author a ON a.id = n.author_id
LEFT JOIN stats s ON s.news_id = n.id
WHERE f.is_top_official = TRUE
ORDER BY f.publish_time DESC
LIMIT 5;
`

	rows, err := r.db.QueryContext(ctx, sqlTop)
	if err != nil {
		//修正: 错误返回时，增加 latestPublishTime 的默认值 0
		return nil, nil, 0, err
	}
	defer rows.Close()

	topItems := []domain.FeedItem{}
	for rows.Next() {
		item, err := scanFeedItem(rows)
		if err != nil {
			return nil, nil, 0, err
		}

		var loadErr error
		item.Media, loadErr = r.loadMedia(ctx, item.ID)
		if loadErr != nil {
			fmt.Printf("Error loading media for NewsID %d: %v\n", item.ID, loadErr)
		}
		topItems = append(topItems, item)
	}

	sqlNormal := `
SELECT
	n.id AS news_id,
	n.title,
	n.summary,
	f.content_type,
	f.category,
	f.sub_category,
	f.tags,
	f.city,
	f.is_official_media,
	f.is_top_official,
	f.source,
	EXTRACT(EPOCH FROM f.publish_time)::bigint AS publish_time,
	f.weight,

	a.id AS author_id,
	a.name AS author_name,
	a.avatar_url AS author_avatar,
	a.certification AS author_cert,

	s.like_count,
	s.comment_count,
	s.favorite_count,
	s.share_count
FROM feed_item f
JOIN news n ON n.id = f.news_id
LEFT JOIN author a ON a.id = n.author_id
LEFT JOIN stats s ON s.news_id = n.id
WHERE f.is_top_official = FALSE
ORDER BY f.publish_time DESC, f.weight DESC
LIMIT 15;
`

	rows2, err := r.db.QueryContext(ctx, sqlNormal)
	if err != nil {
		return nil, nil, 0, err
	}
	defer rows2.Close()

	normalItems := []domain.FeedItem{}
	for rows2.Next() {
		item, err := scanFeedItem(rows2)
		if err != nil {
			return nil, nil, 0, err
		}

		item.Media, _ = r.loadMedia(ctx, item.ID)
		normalItems = append(normalItems, item)
	}

	// 合并
	items := append(topItems, normalItems...)

	var nextCursor *int64
	if len(normalItems) > 0 {
		last := normalItems[len(normalItems)-1]
		nextCursor = &last.PublishTime
	}

	var latestPublishTime int64 = 0
	if len(items) > 0 {
		// 列表的第一项就是最新的，因为是 DESC 排序
		latestPublishTime = items[0].PublishTime
	}
	return items, nextCursor, latestPublishTime, nil
}

//////////////////////////////////////////
// 加载更多
//////////////////////////////////////////

func (r *FeedItemRepositoryPG) ListFeed(ctx context.Context, cursor *int64, limit int) ([]domain.FeedItem, *int64, int64, error) {
	if cursor == nil {
		return nil, nil, 0, fmt.Errorf("cursor is required")
	}

	sqlStr := `
SELECT
	n.id AS news_id,
	n.title,
	n.summary,
	f.content_type,
	f.category,
	f.sub_category,
	f.tags,
	f.city,
	f.is_official_media,
	f.is_top_official,
	f.source,
	EXTRACT(EPOCH FROM f.publish_time)::bigint AS publish_time,
	f.weight,

	a.id AS author_id,
	a.name AS author_name,
	a.avatar_url AS author_avatar,
	a.certification AS author_cert,

	s.like_count,
	s.comment_count,
	s.favorite_count,
	s.share_count
FROM feed_item f
JOIN news n ON n.id = f.news_id
LEFT JOIN author a ON a.id = n.author_id
LEFT JOIN stats s ON s.news_id = n.id
WHERE f.publish_time < to_timestamp($1)
ORDER BY f.publish_time DESC, f.weight DESC
LIMIT $2;
`

	rows, err := r.db.QueryContext(ctx, sqlStr, *cursor, limit)
	if err != nil {
		return nil, nil, 0, err
	}
	defer rows.Close()

	items := []domain.FeedItem{}
	for rows.Next() {
		item, err := scanFeedItem(rows)
		if err != nil {
			return nil, nil, 0, err
		}

		item.Media, _ = r.loadMedia(ctx, item.ID)
		items = append(items, item)
	}

	var nextCursor *int64
	if len(items) > 0 {
		last := items[len(items)-1]
		nextCursor = &last.PublishTime
	}

	var latestPublishTime int64 = 0
	if len(items) > 0 {
		// 列表的第一项就是最新的，因为是 DESC 排序
		latestPublishTime = items[0].PublishTime
	}

	return items, nextCursor, latestPublishTime, nil
}

//////////////////////////////////////////
// 下拉刷新
//////////////////////////////////////////

// func (r *FeedItemRepositoryPG) ListNewer(ctx context.Context, refreshTime int64) ([]domain.FeedItem, error) {

// 	sqlStr := `
// SELECT
// 	n.id AS news_id,
// 	n.title,
// 	n.summary,
// 	f.content_type,
// 	f.category,
// 	f.sub_category,
// 	f.tags,
// 	f.city,
// 	f.is_official_media,
// 	f.is_top_official,
// 	f.source,
// 	EXTRACT(EPOCH FROM f.publish_time)::bigint AS publish_time,
// 	f.weight,

// 	a.id AS author_id,
// 	a.name AS author_name,
// 	a.avatar_url AS author_avatar,
// 	a.certification AS author_cert,

// 	s.like_count,
// 	s.comment_count,
// 	s.favorite_count,
// 	s.share_count
// FROM feed_item f
// JOIN news n ON n.id = f.news_id
// LEFT JOIN author a ON a.id = n.author_id
// LEFT JOIN stats s ON s.news_id = n.id
// WHERE f.publish_time > to_timestamp($1)
// ORDER BY f.publish_time DESC;
// `

// 	rows, err := r.db.QueryContext(ctx, sqlStr, refreshTime)
// 	if err != nil {
// 		return nil, err
// 	}
// 	defer rows.Close()

// 	items := []domain.FeedItem{}
// 	for rows.Next() {
// 		item, err := scanFeedItem(rows)
// 		if err != nil {
// 			return nil, err
// 		}
// 		item.Media, _ = r.loadMedia(ctx, item.ID)
// 		items = append(items, item)
// 	}

// 	return items, nil
// }

// infrastructure/feed_item_repository_pg.go

//////////////////////////////////////////
// 下拉刷新 (Top5 + Normal15 模式，带时间过滤)
//////////////////////////////////////////

func (r *FeedItemRepositoryPG) ListNewer(ctx context.Context, refreshTime int64) ([]domain.FeedItem, error) {

	// 1. 获取 Top 官方内容（最新的 5 条，且发布时间要比 refreshTime 新）
	sqlTop := `
SELECT
    n.id AS news_id,
    n.title,
    n.summary,
    f.content_type,
    f.category,
    f.sub_category,
    f.tags,
    f.city,
    f.is_official_media,
    f.is_top_official,
    f.source,
    EXTRACT(EPOCH FROM f.publish_time)::bigint AS publish_time,
    f.weight,

    a.id AS author_id,
    a.name AS author_name,
    a.avatar_url AS author_avatar,
    a.certification AS author_cert,

    s.like_count,
    s.comment_count,
    s.favorite_count,
    s.share_count
FROM feed_item f
JOIN news n ON n.id = f.news_id
LEFT JOIN author a ON a.id = n.author_id
LEFT JOIN stats s ON s.news_id = n.id
WHERE f.is_top_official = TRUE
  AND f.publish_time > to_timestamp($1)
ORDER BY f.publish_time DESC
LIMIT 5;
`
	rowsTop, err := r.db.QueryContext(ctx, sqlTop, refreshTime) // 传入 refreshTime
	if err != nil {
		// 确保返回的错误清晰地包含是哪个 SQL 失败了
		return nil, fmt.Errorf("query sqlTop failed: %w", err)
	}
	defer rowsTop.Close()

	topItems := []domain.FeedItem{}
	for rowsTop.Next() {
		item, err := scanFeedItem(rowsTop)
		if err != nil {
			return nil, fmt.Errorf("scan top feed item failed: %w", err)
		}
		item.Media, _ = r.loadMedia(ctx, item.ID)
		topItems = append(topItems, item)
	}

	// 2. 获取 普通内容（最新的 15 条，且发布时间要比 refreshTime 新）
	sqlNormal := `
SELECT
    n.id AS news_id,
    n.title,
    n.summary,
    f.content_type,
    f.category,
    f.sub_category,
    f.tags,
    f.city,
    f.is_official_media,
    f.is_top_official,
    f.source,
    EXTRACT(EPOCH FROM f.publish_time)::bigint AS publish_time,
    f.weight,

    a.id AS author_id,
    a.name AS author_name,
    a.avatar_url AS author_avatar,
    a.certification AS author_cert,

    s.like_count,
    s.comment_count,
    s.favorite_count,
    s.share_count
FROM feed_item f
JOIN news n ON n.id = f.news_id
LEFT JOIN author a ON a.id = n.author_id
LEFT JOIN stats s ON s.news_id = n.id
WHERE f.is_top_official = FALSE
  AND f.publish_time > to_timestamp($1)
ORDER BY f.publish_time DESC, f.weight DESC
LIMIT 15;
`
	rowsNormal, err := r.db.QueryContext(ctx, sqlNormal, refreshTime) // 传入 refreshTime
	if err != nil {
		return nil, fmt.Errorf("query sqlNormal failed: %w", err)
	}
	defer rowsNormal.Close()

	normalItems := []domain.FeedItem{}
	for rowsNormal.Next() {
		item, err := scanFeedItem(rowsNormal)
		if err != nil {
			return nil, fmt.Errorf("scan normal feed item failed: %w", err)
		}
		item.Media, _ = r.loadMedia(ctx, item.ID)
		normalItems = append(normalItems, item)
	}

	// 3. 合并并返回
	items := append(topItems, normalItems...)

	return items, nil
}

//////////////////////////////////////////
// 加载媒体
//////////////////////////////////////////

func (r *FeedItemRepositoryPG) loadMedia(ctx context.Context, newsID int64) ([]domain.Media, error) {

	// 1. 重新引入 SQL 字符串定义，解决 undefined: sqlStr 错误
	sqlStr := `
SELECT media_type, url, cover_url, duration, width, height
FROM media
WHERE news_id = $1
ORDER BY id ASC;
`

	// 2. 执行查询
	rows, err := r.db.QueryContext(ctx, sqlStr, newsID)
	if err != nil {
		// 建议添加日志输出查询错误
		// log.Printf("loadMedia QueryContext error for newsID %d: %v", newsID, err)
		return nil, err
	}
	defer rows.Close()

	result := []domain.Media{}

	// 3. 扫描结果，使用 sql.Null 类型解决 NULL 值问题
	for rows.Next() {
		var m domain.Media

		// 声明临时的 sql.Null 类型变量
		var duration sql.NullInt64
		var width sql.NullInt64
		var height sql.NullInt64
		var coverURL sql.NullString

		// 扫描时使用 sql.Null 类型变量
		err := rows.Scan(
			&m.MediaType,
			&m.URL,
			&coverURL,
			&duration,
			&width,
			&height,
		)

		if err != nil {
			// 建议打印扫描错误，这通常是类型不匹配导致的
			// log.Printf("loadMedia rows.Scan error: %v", err)
			return nil, err
		}

		// 检查 Null 类型变量是否有效，并赋值给 domain.Media 的字段
		if coverURL.Valid {
			m.CoverURL = coverURL.String
		}
		if duration.Valid {
			m.Duration = int(duration.Int64)
		}
		if width.Valid {
			m.Width = int(width.Int64)
		}
		if height.Valid {
			m.Height = int(height.Int64)
		}

		result = append(result, m)
	}

	// 检查 rows.Err() 以捕获循环结束后的错误
	if err := rows.Err(); err != nil {
		return nil, err
	}
	fmt.Printf("NewsID %d: successfully loaded %d media items.\n", newsID, len(result))

	return result, nil
}

//////////////////////////////////////////
// scanFeedItem（100% 对齐 SQL 顺序）
//////////////////////////////////////////

func scanFeedItem(row *sql.Rows) (domain.FeedItem, error) {

	var item domain.FeedItem
	var rawTags sql.NullString
	var authorCert sql.NullString

	err := row.Scan(
		&item.ID,
		&item.Title,
		&item.Summary,
		&item.ContentType,
		&item.Category,
		&item.SubCategory,
		&rawTags,
		&item.City,
		&item.IsOfficial,
		&item.IsTopOfficial,
		&item.Source,
		&item.PublishTime,
		&item.Weight,

		&item.Author.ID,
		&item.Author.Name,
		&item.Author.AvatarURL,
		&authorCert,

		&item.Stats.LikeCount,
		&item.Stats.CommentCount,
		&item.Stats.FavoriteCount,
		&item.Stats.ShareCount,
	)

	if err != nil {
		return item, err
	}

	if rawTags.Valid {
		item.Tags = parsePgArray(rawTags.String)
	}

	if authorCert.Valid {
		item.Author.Certification = authorCert.String
	}

	return item, nil
}

func parsePgArray(s string) []string {
	if s == "" || s == "{}" {
		return []string{}
	}

	s = strings.Trim(s, "{}")
	if s == "" {
		return []string{}
	}

	parts := strings.Split(s, ",")
	for i := range parts {
		parts[i] = strings.Trim(parts[i], `"`)
	}

	return parts
}
