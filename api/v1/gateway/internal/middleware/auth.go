package middleware

import (
	"net/http"
	"strings"

	"gateway/internal/jwks"
)

// Public rotaların listesi
var defaultPublicPaths = []string{
	"/api/v1/auth/sessions",
	"/api/v1/auth/identities",
	"/api/v1/auth/password-reset-tokens",
	"/api/v1/auth/.well-known",
	"/health",
}

func isPublic(path string, method string) bool {
	for _, p := range defaultPublicPaths {
		if strings.HasPrefix(path, p) {
			if path == "/api/v1/auth/identities" && method != http.MethodPost {
				return false
			}
			return true
		}
	}
	return false
}

func Auth(km *jwks.KeyManager) func(http.Handler) http.Handler {
	return func(next http.Handler) http.Handler {
		return http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
			if isPublic(r.URL.Path, r.Method) {
				next.ServeHTTP(w, r)
				return
			}

			authHeader := r.Header.Get("Authorization")
			if authHeader == "" || !strings.HasPrefix(authHeader, "Bearer ") {
				http.Error(w, `{"error":"missing or invalid authorization header"}`, http.StatusUnauthorized)
				return
			}

			tokenStr := strings.TrimPrefix(authHeader, "Bearer ")

			claims, err := km.ValidateToken(tokenStr)
			if err != nil {
				http.Error(w, `{"error":"unauthorized: invalid token"}`, http.StatusUnauthorized)
				return
			}

			ctx := SetIdentityID(r.Context(), claims.Subject)
			ctx = SetTokenJTI(ctx, claims.ID)

			next.ServeHTTP(w, r.WithContext(ctx))
		})
	}
}
