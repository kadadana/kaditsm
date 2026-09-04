package proxy

import (
	"fmt"
	"net/http"
	"net/http/httputil"
	"net/url"
	"strings"
	"time"
)

type Route struct {
	Prefix    string
	TargetURL *url.URL
	Proxy     *httputil.ReverseProxy
	StripPath bool
}

type Router struct {
	routes []Route
}

func NewRouter() *Router {
	return &Router{
		routes: make([]Route, 0),
	}
}

func (r *Router) AddRoute(prefix, targetURLStr string, stripPrefix bool) error {
	target, err := url.Parse(targetURLStr)
	if err != nil {
		return fmt.Errorf("invalid target url %s: %w", targetURLStr, err)
	}

	proxy := httputil.NewSingleHostReverseProxy(target)

	proxy.Transport = &http.Transport{
		ResponseHeaderTimeout: 10 * time.Second,
	}
	proxy.ErrorHandler = func(w http.ResponseWriter, req *http.Request, err error) {
		http.Error(w, "Bad Gateway", http.StatusBadGateway)
	}

	r.routes = append(r.routes, Route{
		Prefix:    prefix,
		TargetURL: target,
		Proxy:     proxy,
		StripPath: stripPrefix,
	})

	return nil
}

func (r *Router) Match(reqPath string) (*Route, bool) {
	for _, route := range r.routes {
		if strings.HasPrefix(reqPath, route.Prefix) {
			return &route, true
		}
	}
	return nil, false
}

func (route *Route) Forward(w http.ResponseWriter, r *http.Request, identityID string) {
	if route.StripPath {
		r.URL.Path = strings.TrimPrefix(r.URL.Path, route.Prefix)
		if !strings.HasPrefix(r.URL.Path, "/") {
			r.URL.Path = "/" + r.URL.Path
		}
	}

	if identityID != "" {
		r.Header.Set("X-User-Id", identityID)
	}

	route.Proxy.ServeHTTP(w, r)
}
