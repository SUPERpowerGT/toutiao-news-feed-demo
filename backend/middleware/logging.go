package middleware

import (
	"log"
	"net/http"
	"time"
)

func Logging(next http.Handler) http.Handler {
	return http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
		start := time.Now()

		// 调用下一个 handler
		next.ServeHTTP(w, r)

		// 打印日志
		log.Printf("%s %s %s", r.Method, r.URL.Path, time.Since(start))
	})
}
