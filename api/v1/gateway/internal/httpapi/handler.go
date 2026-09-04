package httpapi

import (
	"context"
	"net/http"
	"strings"

	"gateway/internal/jwks"
	"gateway/internal/proxy"
)

type BlacklistChecker interface {
	IsBlacklisted(ctx context.Context, jti string) (bool, error)
}

type GatewayHandler struct {
	keyManager  *jwks.KeyManager
	blacklist   BlacklistChecker
	router      *proxy.Router
	publicPaths []string
}

func NewGatewayHandler(km *jwks.KeyManager, bl BlacklistChecker, router *proxy.Router) *GatewayHandler {
	return &GatewayHandler{
		keyManager: km,
		blacklist:  bl,
		router:     router,
		publicPaths: []string{
			"/api/v1/auth/sessions",
			"/api/v1/auth/identities",
			"/api/v1/auth/password-reset-tokens",
			"/api/v1/auth/.well-known",
			"/health",
		},
	}
}

func (h *GatewayHandler) ServeHTTP(w http.ResponseWriter, r *http.Request) {
	if r.URL.Path == "/health" {
		w.Header().Set("Content-Type", "application/json")
		w.WriteHeader(http.StatusOK)
		w.Write([]byte(`{"status":"UP"}`))
		return
	}

	route, found := h.router.Match(r.URL.Path)
	if !found {
		http.Error(w, `{"error":"route not found"}`, http.StatusNotFound)
		return
	}

	if h.isPublic(r.URL.Path) {
		route.Forward(w, r, "")
		return
	}

	authHeader := r.Header.Get("Authorization")
	if authHeader == "" || !strings.HasPrefix(authHeader, "Bearer ") {
		http.Error(w, `{"error":"missing or invalid authorization header"}`, http.StatusUnauthorized)
		return
	}

	tokenStr := strings.TrimPrefix(authHeader, "Bearer ")

	claims, err := h.keyManager.ValidateToken(tokenStr)
	if err != nil {
		http.Error(w, `{"error":"unauthorized: invalid token"}`, http.StatusUnauthorized)
		return
	}

	isRevoked, err := h.blacklist.IsBlacklisted(r.Context(), claims.ID)
	if err != nil {
		http.Error(w, `{"error":"internal authentication check error"}`, http.StatusInternalServerError)
		return
	}
	if isRevoked {
		http.Error(w, `{"error":"unauthorized: token revoked"}`, http.StatusUnauthorized)
		return
	}

	route.Forward(w, r, claims.Subject)
}

func (h *GatewayHandler) isPublic(path string) bool {
	for _, p := range h.publicPaths {
		if strings.HasPrefix(path, p) {
			return true
		}
	}
	return false
}
