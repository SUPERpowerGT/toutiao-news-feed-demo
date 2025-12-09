package api

import (
	"encoding/json"
	"log"
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
	mux.HandleFunc("/api/v1/feed", h.handleFeed)
}

/*
GET /api/v1/feed?cursor=xxx&refresh_time=xxx&limit=20
*/
func (h *FeedHandler) handleFeed(w http.ResponseWriter, r *http.Request) {
	log.Printf("API: Feed Request received from %s for URL %s", r.RemoteAddr, r.URL.String())

	q := r.URL.Query()

	// ① cursor
	var cursor *int64
	if v := q.Get("cursor"); v != "" {
		if n, err := strconv.ParseInt(v, 10, 64); err == nil {
			cursor = &n
		}
	}

	// ② refresh_time
	var refreshTime *int64
	if v := q.Get("refresh_time"); v != "" {
		if n, err := strconv.ParseInt(v, 10, 64); err == nil {
			refreshTime = &n
		}
	}

	// ③ limit (默认 15)
	limit := 15
	if v := q.Get("limit"); v != "" {
		if n, err := strconv.Atoi(v); err == nil && n > 0 && n <= 100 {
			limit = n
		}
	}

	// 调用服务层
	resp, err := h.service.GetFeed(r.Context(), cursor, refreshTime, limit)

	//统一包装响应函数 —— 永远不再 chunked
	writeJSON := func(status int, body map[string]any) {
		b, _ := json.Marshal(body)

		w.Header().Set("Content-Type", "application/json; charset=utf-8")
		w.Header().Set("Content-Length", strconv.Itoa(len(b)))
		w.WriteHeader(status)
		w.Write(b)
	}

	//不要再使用 json.NewEncoder(w).Encode() —— 它会触发 chunked!!!
	if err != nil {
		log.Printf("ERROR: GetFeed failed for %s. Reason: %v", r.URL.String(), err)
		writeJSON(http.StatusInternalServerError, map[string]any{
			"code":    500,
			"message": err.Error(),
		})
		return
	}

	// 正常返回
	writeJSON(http.StatusOK, map[string]any{
		"code":    0,
		"message": "success",
		"data":    resp,
	})

	log.Printf("API: Feed Request for %s completed successfully.", r.URL.String())
}
