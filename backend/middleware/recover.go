package middleware

import (
	"log"
	"net/http"
	"runtime/debug" // 🎯 新增导入
)

func Recover(next http.Handler) http.Handler {
	return http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
		defer func() {
			if rec := recover(); rec != nil {
				// 1. 打印 panic 的原因和完整的堆栈跟踪
				// debug.Stack() 返回的是一个字节切片，需要转换成字符串打印
				log.Printf("PANIC: %v\nSTACK TRACE:\n%s", rec, debug.Stack())

				// 2. 返回 500 错误给客户端
				w.WriteHeader(http.StatusInternalServerError)
				w.Write([]byte("Internal Server Error"))
			}
		}()

		next.ServeHTTP(w, r)
	})
}
