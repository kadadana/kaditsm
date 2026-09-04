package proxy

import (
	"fmt"
	"net/http"
	"net/http/httputil"
	"net/url"
	"sort"
	"strings"
	"sync"
)

type Route struct {
	Prefix    string
	TargetURL *url.URL
	StripPath bool
	Proxy     *httputil.ReverseProxy
}

func NewRoute(prefix string, target string, stripPath bool) (*Route, error) {
	parsedURL, err := url.Parse(target)
	if err != nil {
		return nil, fmt.Errorf("invalid target url: %w", err)
	}

	proxy := httputil.NewSingleHostReverseProxy(parsedURL)

	proxy.ErrorHandler = func(w http.ResponseWriter, r *http.Request, err error) {
		http.Error(w, `{"error":"bad gateway: downstream service unavailable"}`, http.StatusBadGateway)
	}

	return &Route{
		Prefix:    prefix,
		TargetURL: parsedURL,
		StripPath: stripPath,
		Proxy:     proxy,
	}, nil
}

func (r *Route) Forward(w http.ResponseWriter, req *http.Request) {
	if r.StripPath {
		req.URL.Path = strings.TrimPrefix(req.URL.Path, r.Prefix)
		if !strings.HasPrefix(req.URL.Path, "/") {
			req.URL.Path = "/" + req.URL.Path
		}
	}

	req.Host = r.TargetURL.Host
	r.Proxy.ServeHTTP(w, req)
}

type Router struct {
	routes []*Route
	mu     sync.RWMutex
}

func NewRouter() *Router {
	return &Router{
		routes: make([]*Route, 0),
	}
}

func (rt *Router) AddRoute(prefix string, target string, stripPath bool) error {
	rt.mu.Lock()
	defer rt.mu.Unlock()

	route, err := NewRoute(prefix, target, stripPath)
	if err != nil {
		return fmt.Errorf("failed to add route for %s: %w", prefix, err)
	}

	rt.routes = append(rt.routes, route)

	sort.Slice(rt.routes, func(i, j int) bool {
		return len(rt.routes[i].Prefix) > len(rt.routes[j].Prefix)
	})

	return nil
}

func (rt *Router) Match(path string) (*Route, bool) {
	rt.mu.RLock()
	defer rt.mu.RUnlock()

	for _, route := range rt.routes {
		if strings.HasPrefix(path, route.Prefix) {
			return route, true
		}
	}
	return nil, false
}
