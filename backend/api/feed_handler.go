package api

import (
	"encoding/json"
	"net/http"
	"strconv"

	"toutiao-backend/application"
)

type FeedHandler struct {
	service *application.FeedService
}

func NewFeedHandler(s *application.FeedService) *FeedHandler {
	return &FeedHandler{service: s}
}

func (h *FeedHandler) RegisterRoutes(mux *http.ServeMux) {
	// GET /api/v1/feed?cursor=xxx&limit=20
	mux.HandleFunc("/api/v1/feed", h.handleGetFeed)
}

func (h *FeedHandler) handleGetFeed(w http.ResponseWriter, r *http.Request) {
	q := r.URL.Query()

	cursor := q.Get("cursor")

	// limit
	limit := 20
	if v := q.Get("limit"); v != "" {
		if n, err := strconv.Atoi(v); err == nil && n > 0 && n <= 100 {
			limit = n
		}
	}

	// 调用 FeedService
	res, err := h.service.GetFeed(r.Context(), cursor, limit)
	if err != nil {
		w.WriteHeader(http.StatusInternalServerError)
		_ = json.NewEncoder(w).Encode(map[string]any{
			"code":    500,
			"message": err.Error(),
		})
		return
	}

	// 正确返回
	w.Header().Set("Content-Type", "application/json; charset=utf-8")
	_ = json.NewEncoder(w).Encode(map[string]any{
		"code":    0,
		"message": "success",
		"data":    res,
	})
}
