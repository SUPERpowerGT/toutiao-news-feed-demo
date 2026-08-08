package api

import (
	"context"
	"encoding/json"
	"net/http"
	"net/http/httptest"
	"testing"

	"toutiao-backend/application"
	"toutiao-backend/domain"
)

type handlerRepository struct {
	lastScene string
	lastLimit int
}

func (r *handlerRepository) ListInitial(_ context.Context, scene string) ([]domain.FeedItem, []domain.FeedItem, *int64, int64, error) {
	r.lastScene = scene
	return []domain.FeedItem{}, []domain.FeedItem{}, nil, 0, nil
}

func (r *handlerRepository) ListFeed(_ context.Context, scene string, _ *int64, limit int) ([]domain.FeedItem, *int64, int64, error) {
	r.lastScene = scene
	r.lastLimit = limit
	return []domain.FeedItem{}, nil, 0, nil
}

func (r *handlerRepository) ListNewer(_ context.Context, scene string, _ int64) ([]domain.FeedItem, []domain.FeedItem, error) {
	r.lastScene = scene
	return []domain.FeedItem{}, []domain.FeedItem{}, nil
}

func newFeedTestHandler(repo domain.FeedItemRepository) http.Handler {
	mux := http.NewServeMux()
	NewFeedHandler(application.NewFeedService(repo)).RegisterRoutes(mux)
	return mux
}

func TestFeedHandlerAcceptsValidRequest(t *testing.T) {
	repo := &handlerRepository{}
	recorder := httptest.NewRecorder()
	request := httptest.NewRequest(http.MethodGet, "/api/v1/feed?scene=tech&cursor=100&limit=7", nil)

	newFeedTestHandler(repo).ServeHTTP(recorder, request)

	if recorder.Code != http.StatusOK {
		t.Fatalf("status=%d body=%s", recorder.Code, recorder.Body.String())
	}
	if repo.lastScene != "tech" || repo.lastLimit != 7 {
		t.Fatalf("request values not forwarded: scene=%q limit=%d", repo.lastScene, repo.lastLimit)
	}
	if recorder.Header().Get("X-Content-Type-Options") != "nosniff" {
		t.Fatal("missing nosniff response header")
	}
	if recorder.Header().Get("X-Frame-Options") != "DENY" {
		t.Fatal("missing frame protection response header")
	}
}

func TestFeedHandlerRejectsInvalidInput(t *testing.T) {
	tests := []struct {
		name   string
		method string
		target string
		status int
	}{
		{name: "method", method: http.MethodPost, target: "/api/v1/feed", status: http.StatusMethodNotAllowed},
		{name: "scene", method: http.MethodGet, target: "/api/v1/feed?scene=unknown", status: http.StatusBadRequest},
		{name: "cursor format", method: http.MethodGet, target: "/api/v1/feed?cursor=nope", status: http.StatusBadRequest},
		{name: "negative cursor", method: http.MethodGet, target: "/api/v1/feed?cursor=-1", status: http.StatusBadRequest},
		{name: "refresh format", method: http.MethodGet, target: "/api/v1/feed?refresh_time=nope", status: http.StatusBadRequest},
		{name: "conflicting modes", method: http.MethodGet, target: "/api/v1/feed?cursor=1&refresh_time=2", status: http.StatusBadRequest},
		{name: "limit zero", method: http.MethodGet, target: "/api/v1/feed?limit=0", status: http.StatusBadRequest},
		{name: "limit too high", method: http.MethodGet, target: "/api/v1/feed?limit=101", status: http.StatusBadRequest},
	}

	for _, test := range tests {
		t.Run(test.name, func(t *testing.T) {
			recorder := httptest.NewRecorder()
			request := httptest.NewRequest(test.method, test.target, nil)
			newFeedTestHandler(&handlerRepository{}).ServeHTTP(recorder, request)

			if recorder.Code != test.status {
				t.Fatalf("status=%d want=%d body=%s", recorder.Code, test.status, recorder.Body.String())
			}
			var body map[string]any
			if err := json.Unmarshal(recorder.Body.Bytes(), &body); err != nil {
				t.Fatalf("response is not valid JSON: %v", err)
			}
		})
	}
}
