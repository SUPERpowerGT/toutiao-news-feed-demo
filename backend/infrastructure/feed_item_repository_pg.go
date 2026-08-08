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
func (r *FeedItemRepositoryPG) ListInitial(ctx context.Context, scene string) ([]domain.FeedItem, []domain.FeedItem, *int64, int64, error) {
	scene = normalizeScene(scene)

	if scene != "recommend" {
		items, next, latestTime, err := r.querySceneItems(ctx, scene, nil, nil, 15)
		if err != nil {
			return nil, nil, nil, 0, err
		}
		return []domain.FeedItem{}, items, next, latestTime, nil
	}

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
	FLOOR(EXTRACT(EPOCH FROM f.publish_time))::bigint AS publish_time,
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
		return nil, nil, nil, 0, err
	}
	topItems, err := scanFeedRows(rows)
	rows.Close()
	if err != nil {
		return nil, nil, nil, 0, err
	}
	if err := r.attachMedia(ctx, topItems); err != nil {
		return nil, nil, nil, 0, err
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
	FLOOR(EXTRACT(EPOCH FROM f.publish_time))::bigint AS publish_time,
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
		return nil, nil, nil, 0, err
	}
	normalItems, err := scanFeedRows(rows2)
	rows2.Close()
	if err != nil {
		return nil, nil, nil, 0, err
	}
	if err := r.attachMedia(ctx, normalItems); err != nil {
		return nil, nil, nil, 0, err
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
	return topItems, normalItems, nextCursor, latestPublishTime, nil
}

//////////////////////////////////////////
// 加载更多
//////////////////////////////////////////

func (r *FeedItemRepositoryPG) ListFeed(ctx context.Context, scene string, cursor *int64, limit int) ([]domain.FeedItem, *int64, int64, error) {
	if cursor == nil {
		return nil, nil, 0, fmt.Errorf("cursor is required")
	}

	return r.querySceneItems(ctx, normalizeScene(scene), cursor, nil, limit)
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

func (r *FeedItemRepositoryPG) ListNewer(ctx context.Context, scene string, refreshTime int64) ([]domain.FeedItem, []domain.FeedItem, error) {
	scene = normalizeScene(scene)

	if scene != "recommend" {
		items, _, _, err := r.querySceneItems(ctx, scene, nil, &refreshTime, 15)
		if err != nil {
			return nil, nil, err
		}
		return []domain.FeedItem{}, items, nil
	}

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
    FLOOR(EXTRACT(EPOCH FROM f.publish_time))::bigint AS publish_time,
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
  AND FLOOR(EXTRACT(EPOCH FROM f.publish_time))::bigint > $1
ORDER BY f.publish_time DESC
LIMIT 5;
`
	rowsTop, err := r.db.QueryContext(ctx, sqlTop, refreshTime) // 传入 refreshTime
	if err != nil {
		return nil, nil, fmt.Errorf("query sqlTop failed: %w", err)
	}
	topItems, err := scanFeedRows(rowsTop)
	rowsTop.Close()
	if err != nil {
		return nil, nil, fmt.Errorf("scan top feed item failed: %w", err)
	}
	if err := r.attachMedia(ctx, topItems); err != nil {
		return nil, nil, err
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
    FLOOR(EXTRACT(EPOCH FROM f.publish_time))::bigint AS publish_time,
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
  AND FLOOR(EXTRACT(EPOCH FROM f.publish_time))::bigint > $1
ORDER BY f.publish_time DESC, f.weight DESC
LIMIT 15;
`
	rowsNormal, err := r.db.QueryContext(ctx, sqlNormal, refreshTime) // 传入 refreshTime
	if err != nil {
		return nil, nil, fmt.Errorf("query sqlNormal failed: %w", err)
	}
	normalItems, err := scanFeedRows(rowsNormal)
	rowsNormal.Close()
	if err != nil {
		return nil, nil, fmt.Errorf("scan normal feed item failed: %w", err)
	}
	if err := r.attachMedia(ctx, normalItems); err != nil {
		return nil, nil, err
	}

	return topItems, normalItems, nil
}

func (r *FeedItemRepositoryPG) querySceneItems(
	ctx context.Context,
	scene string,
	cursor *int64,
	refreshTime *int64,
	limit int,
) ([]domain.FeedItem, *int64, int64, error) {
	filterClause, args, err := sceneFilterClause(scene, 1)
	if err != nil {
		return nil, nil, 0, err
	}

	sqlStr := baseFeedSelectSQL() + `
WHERE 1=1` + filterClause

	if cursor != nil {
		sqlStr += fmt.Sprintf(`
  AND FLOOR(EXTRACT(EPOCH FROM f.publish_time))::bigint < $%d`, len(args)+1)
		args = append(args, *cursor)
	}

	if refreshTime != nil {
		sqlStr += fmt.Sprintf(`
  AND FLOOR(EXTRACT(EPOCH FROM f.publish_time))::bigint > $%d`, len(args)+1)
		args = append(args, *refreshTime)
	}

	sqlStr += `
ORDER BY f.publish_time DESC, f.weight DESC`

	if limit > 0 {
		sqlStr += fmt.Sprintf(`
LIMIT $%d`, len(args)+1)
		args = append(args, limit)
	}
	sqlStr += ";"

	rows, err := r.db.QueryContext(ctx, sqlStr, args...)
	if err != nil {
		return nil, nil, 0, err
	}
	items, err := scanFeedRows(rows)
	rows.Close()
	if err != nil {
		return nil, nil, 0, err
	}
	if err := r.attachMedia(ctx, items); err != nil {
		return nil, nil, 0, err
	}

	var nextCursor *int64
	if len(items) > 0 {
		last := items[len(items)-1]
		nextCursor = &last.PublishTime
	}

	var latestPublishTime int64
	if len(items) > 0 {
		latestPublishTime = items[0].PublishTime
	}

	return items, nextCursor, latestPublishTime, nil
}

func baseFeedSelectSQL() string {
	return `
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
	FLOOR(EXTRACT(EPOCH FROM f.publish_time))::bigint AS publish_time,
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
`
}

func sceneFilterClause(scene string, startIndex int) (string, []any, error) {
	switch normalizeScene(scene) {
	case "recommend":
		return "", nil, nil
	case "video":
		return fmt.Sprintf("\n  AND f.content_type = $%d", startIndex), []any{"video"}, nil
	case "image":
		return fmt.Sprintf("\n  AND f.content_type = $%d", startIndex), []any{"image"}, nil
	case "shenzhen":
		return fmt.Sprintf("\n  AND f.city = $%d", startIndex), []any{"深圳"}, nil
	case "tech":
		return fmt.Sprintf("\n  AND f.category = $%d", startIndex), []any{"科技"}, nil
	case "sports":
		return fmt.Sprintf("\n  AND f.category = $%d", startIndex), []any{"体育"}, nil
	case "finance":
		return fmt.Sprintf("\n  AND f.category = $%d", startIndex), []any{"财经"}, nil
	case "following":
		return fmt.Sprintf("\n  AND f.category = $%d", startIndex), []any{"关注"}, nil
	case "hot":
		return fmt.Sprintf("\n  AND f.category = $%d", startIndex), []any{"热榜"}, nil
	case "featured":
		return fmt.Sprintf("\n  AND f.category = $%d", startIndex), []any{"精选"}, nil
	case "war":
		return fmt.Sprintf("\n  AND f.category = $%d", startIndex), []any{"抗战"}, nil
	default:
		return "", nil, fmt.Errorf("unsupported scene: %s", scene)
	}
}

func normalizeScene(scene string) string {
	switch scene {
	case "following", "hot", "video", "shenzhen", "featured", "image", "war", "tech", "sports", "finance":
		return scene
	default:
		return "recommend"
	}
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
	return result, nil
}

func (r *FeedItemRepositoryPG) attachMedia(ctx context.Context, items []domain.FeedItem) error {
	for i := range items {
		media, err := r.loadMedia(ctx, items[i].ID)
		if err != nil {
			return fmt.Errorf("load media for news %d: %w", items[i].ID, err)
		}
		items[i].Media = media
	}
	return nil
}

func scanFeedRows(rows *sql.Rows) ([]domain.FeedItem, error) {
	items := []domain.FeedItem{}
	for rows.Next() {
		item, err := scanFeedItem(rows)
		if err != nil {
			return nil, err
		}
		items = append(items, item)
	}
	if err := rows.Err(); err != nil {
		return nil, err
	}
	return items, nil
}

//////////////////////////////////////////
// scanFeedItem（100% 对齐 SQL 顺序）
//////////////////////////////////////////

func scanFeedItem(row *sql.Rows) (domain.FeedItem, error) {

	var item domain.FeedItem
	var rawTags sql.NullString
	var contentType sql.NullString
	var category sql.NullString
	var subCategory sql.NullString
	var city sql.NullString
	var source sql.NullString
	var isOfficial sql.NullBool
	var isTopOfficial sql.NullBool
	var authorID sql.NullInt64
	var authorName sql.NullString
	var authorAvatar sql.NullString
	var authorCert sql.NullString
	var likeCount sql.NullInt64
	var commentCount sql.NullInt64
	var favoriteCount sql.NullInt64
	var shareCount sql.NullInt64

	err := row.Scan(
		&item.ID,
		&item.Title,
		&item.Summary,
		&contentType,
		&category,
		&subCategory,
		&rawTags,
		&city,
		&isOfficial,
		&isTopOfficial,
		&source,
		&item.PublishTime,
		&item.Weight,

		&authorID,
		&authorName,
		&authorAvatar,
		&authorCert,

		&likeCount,
		&commentCount,
		&favoriteCount,
		&shareCount,
	)

	if err != nil {
		return item, err
	}

	if rawTags.Valid {
		item.Tags = parsePgArray(rawTags.String)
	}

	if contentType.Valid {
		item.ContentType = contentType.String
	}
	if category.Valid {
		item.Category = category.String
	}
	if subCategory.Valid {
		item.SubCategory = subCategory.String
	}
	if city.Valid {
		item.City = city.String
	}
	if source.Valid {
		item.Source = source.String
	}
	if isOfficial.Valid {
		item.IsOfficial = isOfficial.Bool
	}
	if isTopOfficial.Valid {
		item.IsTopOfficial = isTopOfficial.Bool
	}
	if authorID.Valid {
		item.Author.ID = authorID.Int64
	}
	if authorName.Valid {
		item.Author.Name = authorName.String
	}
	if authorAvatar.Valid {
		item.Author.AvatarURL = authorAvatar.String
	}
	if authorCert.Valid {
		item.Author.Certification = authorCert.String
	}
	if likeCount.Valid {
		item.Stats.LikeCount = int(likeCount.Int64)
	}
	if commentCount.Valid {
		item.Stats.CommentCount = int(commentCount.Int64)
	}
	if favoriteCount.Valid {
		item.Stats.FavoriteCount = int(favoriteCount.Int64)
	}
	if shareCount.Valid {
		item.Stats.ShareCount = int(shareCount.Int64)
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
