package proxy

import (
	"net/http"

	"gateway/internal/middleware"
)

type GatewayHandler struct {
	router *Router
}

func NewGatewayHandler(router *Router) *GatewayHandler {
	return &GatewayHandler{
		router: router,
	}
}

func (h *GatewayHandler) ServeHTTP(w http.ResponseWriter, r *http.Request) {
	route, found := h.router.Match(r.URL.Path)
	if !found {
		http.Error(w, `{"error":"route not found"}`, http.StatusNotFound)
		return
	}

	if identityID, ok := middleware.GetIdentityID(r.Context()); ok && identityID != "" {
		r.Header.Set("X-User-Id", identityID)
	} else {
		r.Header.Del("X-User-Id")
	}

	route.Forward(w, r)
}
