package middleware

import (
	"log"
	"net/http"
	"time"
)

// StatusRecorder 包装了 http.ResponseWriter 来捕获状态码
type StatusRecorder struct {
	http.ResponseWriter
	Status int
}

func (r *StatusRecorder) WriteHeader(status int) {
	r.Status = status
	r.ResponseWriter.WriteHeader(status)
}

func Logging(next http.Handler) http.Handler {
	return http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
		start := time.Now()

		// 1. 使用 StatusRecorder 包装原始 ResponseWriter
		recorder := &StatusRecorder{
			ResponseWriter: w,
			// 默认状态码为 200，如果 WriteHeader 未被调用，Go 默认返回 200
			Status: http.StatusOK,
		}

		// 2. 调用下一个 handler
		next.ServeHTTP(recorder, r)

		// 3. 打印增强日志：包括方法、路径、状态码和耗时
		log.Printf("%s %s [%d] %s",
			r.Method,
			r.URL.Path,
			recorder.Status, // 🎯 捕获到的状态码
			time.Since(start),
		)
	})
}
