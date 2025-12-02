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

//////////////////////////////////////////
// 首页首次加载 Top5 + Normal15
//////////////////////////////////////////

// 🎯 修正: 函数签名增加 int64 (最新的发布时间) 返回值
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
		// 🎯 修正: 错误返回时，增加 latestPublishTime 的默认值 0
		return nil, nil, 0, err
	}
	defer rows.Close()

	topItems := []domain.FeedItem{}
	for rows.Next() {
		item, err := scanFeedItem(rows)
		if err != nil {
			// 🎯 修正: 错误返回时，增加 latestPublishTime 的默认值 0
			return nil, nil, 0, err
		}

		var loadErr error
		// 🎯 修复: 从 item.NewsID 改为 item.ID
		item.Media, loadErr = r.loadMedia(ctx, item.ID)
		if loadErr != nil {
			// 强烈建议使用 log 包打印到控制台，而不是直接返回
			fmt.Printf("Error loading media for NewsID %d: %v\n", item.ID, loadErr)
			// 生产环境中，通常会忽略这个子查询的错误，但为了调试，我们先打出来
			// 如果不希望一个 media 错误导致整个 feed 列表崩溃，就继续，否则返回主错误
		}
		topItems = append(topItems, item)
	}

	// Normal15
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
		// 🎯 修正: 错误返回时，增加 latestPublishTime 的默认值 0
		return nil, nil, 0, err
	}
	defer rows2.Close()

	normalItems := []domain.FeedItem{}
	for rows2.Next() {
		item, err := scanFeedItem(rows2)
		if err != nil {
			// 🎯 修正: 错误返回时，增加 latestPublishTime 的默认值 0
			return nil, nil, 0, err
		}

		// 🎯 修复: 从 item.NewsID 改为 item.ID
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

	// 🎯 新增: 计算 latestPublishTime
	var latestPublishTime int64 = 0
	if len(items) > 0 {
		// 列表的第一项就是最新的，因为是 DESC 排序
		latestPublishTime = items[0].PublishTime
	}

	// 🎯 修正: 返回 latestPublishTime
	return items, nextCursor, latestPublishTime, nil
}

//////////////////////////////////////////
// 加载更多
//////////////////////////////////////////

// 🎯 修正: 函数签名增加 int64 (最新的发布时间) 返回值
func (r *FeedItemRepositoryPG) ListFeed(ctx context.Context, cursor *int64, limit int) ([]domain.FeedItem, *int64, int64, error) {
	if cursor == nil {
		// 🎯 修正: 错误返回时，增加 latestPublishTime 的默认值 0
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
		// 🎯 修正: 错误返回时，增加 latestPublishTime 的默认值 0
		return nil, nil, 0, err
	}
	defer rows.Close()

	items := []domain.FeedItem{}
	for rows.Next() {
		item, err := scanFeedItem(rows)
		if err != nil {
			// 🎯 修正: 错误返回时，增加 latestPublishTime 的默认值 0
			return nil, nil, 0, err
		}

		// 🎯 修复: 从 item.NewsID 改为 item.ID
		item.Media, _ = r.loadMedia(ctx, item.ID)
		items = append(items, item)
	}

	var nextCursor *int64
	if len(items) > 0 {
		last := items[len(items)-1]
		nextCursor = &last.PublishTime
	}

	// 🎯 新增: 计算 latestPublishTime
	var latestPublishTime int64 = 0
	if len(items) > 0 {
		// 列表的第一项就是最新的，因为是 DESC 排序
		latestPublishTime = items[0].PublishTime
	}

	// 🎯 修正: 返回 latestPublishTime
	return items, nextCursor, latestPublishTime, nil
}

//////////////////////////////////////////
// 下拉刷新
//////////////////////////////////////////

func (r *FeedItemRepositoryPG) ListNewer(ctx context.Context, refreshTime int64) ([]domain.FeedItem, error) {

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
WHERE f.publish_time > to_timestamp($1)
ORDER BY f.publish_time DESC;
`

	rows, err := r.db.QueryContext(ctx, sqlStr, refreshTime)
	if err != nil {
		return nil, err
	}
	defer rows.Close()

	items := []domain.FeedItem{}
	for rows.Next() {
		item, err := scanFeedItem(rows)
		if err != nil {
			return nil, err
		}
		// 🎯 修复: 从 item.NewsID 改为 item.ID
		item.Media, _ = r.loadMedia(ctx, item.ID)
		items = append(items, item)
	}

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

	// 🎯 调试行：打印结果长度
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
		// 🚨 修正：item.NewsID 已移除，SQL 结果中的 n.id AS news_id 扫描到 item.ID
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

	// 🎯 修正：手动将 ID 赋值给 NewsID 字段的逻辑已删除，因为 domain.FeedItem 中不再需要 NewsID

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
