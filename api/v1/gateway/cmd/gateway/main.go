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
	"gateway/internal/mq"
	"gateway/internal/proxy"
)

func main() {
	cfg, err := config.Load()
	if err != nil {
		log.Fatalf("Failed to load configuration: %v", err)
	}

	keyManager, err := jwks.NewKeyManager(cfg.JwksURL)
	if err != nil {
		log.Printf("Warning: Failed to fetch initial JWKS: %v (will retry on incoming requests)", err)
	}

	blacklistService, err := blacklist.NewBlacklistService(cfg.RedisAddr)
	if err != nil {
		log.Fatalf("Failed to connect to Redis blacklist service: %v", err)
	}
	ctx, cancel := context.WithCancel(context.Background())
	defer cancel()

	consumer, err := mq.NewBlacklistConsumer(
		cfg.RabbitMQURL(),
		cfg.RabbitMQExchange,
		cfg.RabbitMQRoutingKey,
		cfg.RabbitMQQueue,
		blacklistService,
	)
	if err != nil {
		log.Printf("Warning: RabbitMQ consumer connection failed: %v", err)
	} else {
		defer consumer.Close()
		if err := consumer.Start(ctx); err != nil {
			log.Printf("Failed to start RabbitMQ consumer: %v", err)
		}
	}

	routes, err := config.LoadRoutes("routes.json")
	if err != nil {
		log.Fatalf("Failed to load routes: %v", err)
	}

	router := proxy.NewRouter()
	for _, r := range routes {
		if err := router.AddRoute(r.PathPrefix, r.TargetURL, r.StripPath); err != nil {
			log.Fatalf("Failed to register route %s: %v", r.PathPrefix, err)
		}
		log.Printf("[Route] Registered %s -> %s (StripPath: %t)", r.PathPrefix, r.TargetURL, r.StripPath)
	}

	gatewayHandler := proxy.NewGatewayHandler(router)

	var handler http.Handler = gatewayHandler
	handler = middleware.Blacklist(blacklistService)(handler)
	handler = middleware.Auth(keyManager)(handler)

	mux := http.NewServeMux()
	mux.HandleFunc("/health", func(w http.ResponseWriter, r *http.Request) {
		w.Header().Set("Content-Type", "application/json")
		w.WriteHeader(http.StatusOK)
		w.Write([]byte(`{"status":"UP"}`))
	})
	mux.Handle("/", handler)

	server := &http.Server{
		Addr:         fmt.Sprintf(":%s", cfg.Port),
		Handler:      mux,
		ReadTimeout:  15 * time.Second,
		WriteTimeout: 15 * time.Second,
		IdleTimeout:  60 * time.Second,
	}

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
	cancel()

	shutdownCtx, cancel := context.WithTimeout(context.Background(), 10*time.Second)
	defer cancel()

	if err := server.Shutdown(shutdownCtx); err != nil {
		log.Fatalf("Gateway forced to shutdown: %v", err)
	}

	log.Println("API Gateway stopped.")
}
