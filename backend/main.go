package main

import (
	"log"
	"net/http"
	"path/filepath"
	"runtime"

	"toutiao-backend/api"
	"toutiao-backend/application"
	"toutiao-backend/infrastructure"
	"toutiao-backend/middleware"
	"toutiao-backend/seed"

	"github.com/joho/godotenv"
)

func loadEnv() {
	_, file, _, _ := runtime.Caller(0)
	baseDir := filepath.Dir(filepath.Dir(file))
	_ = godotenv.Load(filepath.Join(baseDir, ".env.dev"))
}

func main() {
	loadEnv()

	// ---------- DB 初始化 ----------
	cfg := infrastructure.LoadDBConfigFromEnv()
	db, err := infrastructure.NewDB(cfg)
	if err != nil {
		log.Fatalf("connect db failed: %v", err)
	}
	defer db.Close()

	// ---------- 依赖注入 ----------
	feedRepo := infrastructure.NewPGFeedItemRepository(db)
	feedService := application.NewFeedService(feedRepo)
	feedHandler := api.NewFeedHandler(feedService)

	// ---------- 路由 ----------
	mux := http.NewServeMux()

	mux.HandleFunc("/health", func(w http.ResponseWriter, r *http.Request) {
		if err := db.Ping(); err != nil {
			http.Error(w, "db not ok", http.StatusInternalServerError)
			return
		}
		w.Write([]byte("ok"))
	})

	mux.HandleFunc("/seed", func(w http.ResponseWriter, r *http.Request) {
		if err := seed.InsertSeedData(db); err != nil {
			http.Error(w, "seed error: "+err.Error(), http.StatusInternalServerError)
			return
		}
		w.Write([]byte("seed ok"))
	})

	feedHandler.RegisterRoutes(mux)

	// ---------- 中间件 ----------
	var h http.Handler = mux
	h = middleware.Recover(h)
	h = middleware.Logging(h)

	log.Println("server listening on :8080")

	// ---------- 启动服务 ----------
	if err := http.ListenAndServe(":8080", h); err != nil {
		log.Fatal(err)
	}
}
