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
	if r.Method != http.MethodGet {
		w.Header().Set("Allow", http.MethodGet)
		writeJSON(w, http.StatusMethodNotAllowed, map[string]any{
			"code":    http.StatusMethodNotAllowed,
			"message": "method not allowed",
		})
		return
	}

	q := r.URL.Query()
	scene := q.Get("scene")
	if scene == "" {
		scene = "recommend"
	}
	if !isSupportedScene(scene) {
		writeBadRequest(w, "unsupported scene")
		return
	}

	// ① cursor
	var cursor *int64
	if v := q.Get("cursor"); v != "" {
		n, err := strconv.ParseInt(v, 10, 64)
		if err != nil || n <= 0 {
			writeBadRequest(w, "cursor must be a positive integer")
			return
		}
		cursor = &n
	}

	// ② refresh_time
	var refreshTime *int64
	if v := q.Get("refresh_time"); v != "" {
		n, err := strconv.ParseInt(v, 10, 64)
		if err != nil || n < 0 {
			writeBadRequest(w, "refresh_time must be a non-negative integer")
			return
		}
		refreshTime = &n
	}
	if cursor != nil && refreshTime != nil {
		writeBadRequest(w, "cursor and refresh_time cannot be used together")
		return
	}

	// ③ limit (默认 15)
	limit := 15
	if v := q.Get("limit"); v != "" {
		n, err := strconv.Atoi(v)
		if err != nil || n <= 0 || n > 100 {
			writeBadRequest(w, "limit must be between 1 and 100")
			return
		}
		limit = n
	}

	// 调用服务层
	resp, err := h.service.GetFeed(r.Context(), scene, cursor, refreshTime, limit)

	//不要再使用 json.NewEncoder(w).Encode() —— 它会触发 chunked!!!
	if err != nil {
		log.Printf("ERROR: GetFeed failed for %s. Reason: %v", r.URL.String(), err)
		writeJSON(w, http.StatusInternalServerError, map[string]any{
			"code":    500,
			"message": "internal server error",
		})
		return
	}

	// 正常返回
	writeJSON(w, http.StatusOK, map[string]any{
		"code":    0,
		"message": "success",
		"data":    resp,
	})

	log.Printf("API: Feed Request for %s completed successfully.", r.URL.String())
}

func isSupportedScene(scene string) bool {
	switch scene {
	case "recommend", "following", "hot", "video", "shenzhen", "featured", "image", "war", "tech", "sports", "finance":
		return true
	default:
		return false
	}
}

func writeBadRequest(w http.ResponseWriter, message string) {
	writeJSON(w, http.StatusBadRequest, map[string]any{
		"code":    http.StatusBadRequest,
		"message": message,
	})
}

func writeJSON(w http.ResponseWriter, status int, body map[string]any) {
	b, err := json.Marshal(body)
	if err != nil {
		http.Error(w, "internal server error", http.StatusInternalServerError)
		return
	}

	w.Header().Set("Content-Type", "application/json; charset=utf-8")
	w.Header().Set("Content-Length", strconv.Itoa(len(b)))
	w.Header().Set("X-Content-Type-Options", "nosniff")
	w.Header().Set("X-Frame-Options", "DENY")
	w.Header().Set("Content-Security-Policy", "default-src 'none'; frame-ancestors 'none'")
	w.Header().Set("Referrer-Policy", "no-referrer")
	w.WriteHeader(status)
	_, _ = w.Write(b)
}
