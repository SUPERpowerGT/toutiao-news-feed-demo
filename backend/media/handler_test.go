package media

import (
	"net/http"
	"net/http/httptest"
	"testing"
)

func TestDemoVideoSupportsFullAndRangeRequests(t *testing.T) {
	mux := http.NewServeMux()
	RegisterRoutes(mux)

	full := httptest.NewRecorder()
	mux.ServeHTTP(full, httptest.NewRequest(http.MethodGet, "/media/demo-video.mp4", nil))
	if full.Code != http.StatusOK {
		t.Fatalf("full request status = %d, want %d", full.Code, http.StatusOK)
	}
	if got := full.Header().Get("Content-Type"); got != "video/mp4" {
		t.Fatalf("Content-Type = %q, want video/mp4", got)
	}
	if full.Body.Len() < 100_000 {
		t.Fatalf("video body too small: %d bytes", full.Body.Len())
	}

	partialRequest := httptest.NewRequest(http.MethodGet, "/media/demo-video.mp4", nil)
	partialRequest.Header.Set("Range", "bytes=0-99")
	partial := httptest.NewRecorder()
	mux.ServeHTTP(partial, partialRequest)
	if partial.Code != http.StatusPartialContent {
		t.Fatalf("range request status = %d, want %d", partial.Code, http.StatusPartialContent)
	}
	if partial.Body.Len() != 100 {
		t.Fatalf("range body length = %d, want 100", partial.Body.Len())
	}
}
