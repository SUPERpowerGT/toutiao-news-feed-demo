package api

import (
	"database/sql"
	"encoding/json"
	"net/http"
	"strconv"
	"strings"
)

type NewsDetailHandler struct {
	db *sql.DB
}

type newsDetailResponse struct {
	ID          int64         `json:"id"`
	Title       string        `json:"title"`
	ContentHTML string        `json:"content_html"`
	ContentJSON string        `json:"content_json"`
	NewsType    string        `json:"news_type"`
	Media       []detailMedia `json:"media"`
	Author      detailAuthor  `json:"author"`
	Stats       detailStats   `json:"stats"`
	PublishTime int64         `json:"publish_time"`
}

type detailMedia struct {
	MediaType string  `json:"media_type"`
	URL       *string `json:"url"`
	CoverURL  *string `json:"cover_url"`
	Duration  *int    `json:"duration"`
	Width     *int    `json:"width"`
	Height    *int    `json:"height"`
}

type detailAuthor struct {
	ID            int64   `json:"id"`
	Name          string  `json:"name"`
	AvatarURL     *string `json:"avatar_url"`
	Certification *string `json:"certification"`
}

type detailStats struct {
	LikeCount     int `json:"like_count"`
	CommentCount  int `json:"comment_count"`
	FavoriteCount int `json:"favorite_count"`
	ShareCount    int `json:"share_count"`
}

func NewNewsDetailHandler(db *sql.DB) *NewsDetailHandler {
	return &NewsDetailHandler{db: db}
}

func (h *NewsDetailHandler) RegisterRoutes(mux *http.ServeMux) {
	mux.HandleFunc("/api/v1/news/", h.getDetail)
}

func (h *NewsDetailHandler) getDetail(w http.ResponseWriter, r *http.Request) {
	if r.Method != http.MethodGet {
		writeBadRequest(w, "unsupported method")
		return
	}

	idText := strings.TrimPrefix(r.URL.Path, "/api/v1/news/")
	if idText == "" || strings.Contains(idText, "/") {
		writeBadRequest(w, "invalid news id")
		return
	}
	id, err := strconv.ParseInt(idText, 10, 64)
	if err != nil || id <= 0 {
		writeBadRequest(w, "invalid news id")
		return
	}

	var detail newsDetailResponse
	var contentJSON []byte
	err = h.db.QueryRowContext(r.Context(), `
        SELECT n.id, n.title, COALESCE(nc.content_html, ''),
               COALESCE(nc.content_json::text, '{}'), n.news_type,
               a.id, a.name, a.avatar_url, a.certification,
               COALESCE(s.like_count, 0), COALESCE(s.comment_count, 0),
               COALESCE(s.favorite_count, 0), COALESCE(s.share_count, 0),
               FLOOR(EXTRACT(EPOCH FROM n.publish_time))::bigint
        FROM news n
        JOIN author a ON a.id = n.author_id
        LEFT JOIN news_content nc ON nc.news_id = n.id
        LEFT JOIN stats s ON s.news_id = n.id
        WHERE n.id = $1
    `, id).Scan(
		&detail.ID, &detail.Title, &detail.ContentHTML, &contentJSON, &detail.NewsType,
		&detail.Author.ID, &detail.Author.Name, &detail.Author.AvatarURL, &detail.Author.Certification,
		&detail.Stats.LikeCount, &detail.Stats.CommentCount,
		&detail.Stats.FavoriteCount, &detail.Stats.ShareCount, &detail.PublishTime,
	)
	if err == sql.ErrNoRows {
		writeJSON(w, http.StatusNotFound, map[string]any{"message": "news not found"})
		return
	}
	if err != nil {
		http.Error(w, "query news detail failed", http.StatusInternalServerError)
		return
	}
	detail.ContentJSON = string(contentJSON)

	rows, err := h.db.QueryContext(r.Context(), `
        SELECT media_type, url, cover_url, duration, width, height
        FROM media WHERE news_id = $1 ORDER BY order_index
    `, id)
	if err != nil {
		http.Error(w, "query news media failed", http.StatusInternalServerError)
		return
	}
	defer rows.Close()
	detail.Media = make([]detailMedia, 0)
	for rows.Next() {
		var media detailMedia
		if err := rows.Scan(&media.MediaType, &media.URL, &media.CoverURL, &media.Duration, &media.Width, &media.Height); err != nil {
			http.Error(w, "scan news media failed", http.StatusInternalServerError)
			return
		}
		detail.Media = append(detail.Media, media)
	}
	if err := rows.Err(); err != nil {
		http.Error(w, "read news media failed", http.StatusInternalServerError)
		return
	}

	w.Header().Set("Content-Type", "application/json")
	if err := json.NewEncoder(w).Encode(detail); err != nil {
		http.Error(w, "encode news detail failed", http.StatusInternalServerError)
	}
}
