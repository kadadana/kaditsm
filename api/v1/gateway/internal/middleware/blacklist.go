package middleware

import (
	"context"
	"net/http"
)

type BlacklistChecker interface {
	IsBlacklisted(ctx context.Context, jti string) (bool, error)
}

func Blacklist(bl BlacklistChecker) func(http.Handler) http.Handler {
	return func(next http.Handler) http.Handler {
		return http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
			jti, ok := GetTokenJTI(r.Context())
			if !ok || jti == "" {
				next.ServeHTTP(w, r)
				return
			}

			isRevoked, err := bl.IsBlacklisted(r.Context(), jti)
			if err != nil {
				http.Error(w, `{"error":"internal authentication check error"}`, http.StatusInternalServerError)
				return
			}
			if isRevoked {
				http.Error(w, `{"error":"unauthorized: token revoked"}`, http.StatusUnauthorized)
				return
			}

			next.ServeHTTP(w, r)
		})
	}
}
