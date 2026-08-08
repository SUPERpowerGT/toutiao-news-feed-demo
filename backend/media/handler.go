package media

import (
	"bytes"
	_ "embed"
	"net/http"
	"time"
)

//go:embed demo-video.mp4
var demoVideo []byte

func RegisterRoutes(mux *http.ServeMux) {
	mux.HandleFunc("/media/demo-video.mp4", func(w http.ResponseWriter, r *http.Request) {
		if r.Method != http.MethodGet && r.Method != http.MethodHead {
			w.Header().Set("Allow", "GET, HEAD")
			http.Error(w, "method not allowed", http.StatusMethodNotAllowed)
			return
		}

		w.Header().Set("Content-Type", "video/mp4")
		w.Header().Set("Cache-Control", "public, max-age=86400")
		http.ServeContent(w, r, "demo-video.mp4", time.Time{}, bytes.NewReader(demoVideo))
	})
}
