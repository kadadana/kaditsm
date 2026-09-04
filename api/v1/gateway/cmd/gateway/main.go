package main

import (
	"context"
	"errors"
	"fmt"
	"log"
	"net/http"
	"os"
	"os/signal"
	"syscall"
	"time"

	"gateway/internal/blacklist"
	"gateway/internal/config"
	"gateway/internal/jwks"
	"gateway/internal/middleware"
	"gateway/internal/proxy"
)

func main() {
	// 1. Konfigürasyonu yükle
	cfg, err := config.Load()
	if err != nil {
		log.Fatalf("Failed to load configuration: %v", err)
	}

	// 2. JWKS Manager başlat
	keyManager, err := jwks.NewKeyManager(cfg.JwksURL)
	if err != nil {
		log.Printf("Warning: Failed to fetch initial JWKS: %v (will retry on incoming requests)", err)
	}

	// 3. Redis Blacklist Servisi
	blacklistService, err := blacklist.NewBlacklistService(cfg.RedisAddr)
	if err != nil {
		log.Fatalf("Failed to connect to Redis blacklist service: %v", err)
	}

	// 4. Reverse Proxy ve Rota Tanımları
	router := proxy.NewRouter()

	// /api/v1/auth altındaki tüm istekler Auth servisine prefix temizlenerek aktarılır:
	// /api/v1/auth/sessions -> http://localhost:8080/sessions
	if err := router.AddRoute("/api/v1/auth", cfg.AuthServiceURL, true); err != nil {
		log.Fatalf("Failed to register auth route: %v", err)
	}

	// 5. Middleware Pipeline
	// İstek Sırası: Recovery (varsa) -> Auth -> Blacklist -> GatewayHandler (Proxy)
	gatewayHandler := proxy.NewGatewayHandler(router)

	var handler http.Handler = gatewayHandler
	handler = middleware.Blacklist(blacklistService)(handler)
	handler = middleware.Auth(keyManager)(handler)

	// Standart Health Endpoint
	mux := http.NewServeMux()
	mux.HandleFunc("/health", func(w http.ResponseWriter, r *http.Request) {
		w.Header().Set("Content-Type", "application/json")
		w.WriteHeader(http.StatusOK)
		w.Write([]byte(`{"status":"UP"}`))
	})
	mux.Handle("/", handler)

	// 6. HTTP Sunucusu
	server := &http.Server{
		Addr:         fmt.Sprintf(":%s", cfg.Port),
		Handler:      mux,
		ReadTimeout:  15 * time.Second,
		WriteTimeout: 15 * time.Second,
		IdleTimeout:  60 * time.Second,
	}

	// Graceful Shutdown Mekanizması
	stopChan := make(chan os.Signal, 1)
	signal.Notify(stopChan, os.Interrupt, syscall.SIGTERM)

	go func() {
		log.Printf("API Gateway running on port %s", cfg.Port)
		if err := server.ListenAndServe(); err != nil && !errors.Is(err, http.ErrServerClosed) {
			log.Fatalf("Gateway server failed: %v", err)
		}
	}()

	<-stopChan
	log.Println("Shutting down API Gateway gracefully...")

	shutdownCtx, cancel := context.WithTimeout(context.Background(), 10*time.Second)
	defer cancel()

	if err := server.Shutdown(shutdownCtx); err != nil {
		log.Fatalf("Gateway forced to shutdown: %v", err)
	}

	log.Println("API Gateway stopped.")
}
