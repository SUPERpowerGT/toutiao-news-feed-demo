package middleware

import (
	"log"
	"net/http"
)

func Recover(next http.Handler) http.Handler {
	return http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
		defer func() {
			if rec := recover(); rec != nil {
				log.Printf("panic: %v", rec)
				w.WriteHeader(http.StatusInternalServerError)
				w.Write([]byte("internal server error"))
			}
		}()

		next.ServeHTTP(w, r)
	})
}
