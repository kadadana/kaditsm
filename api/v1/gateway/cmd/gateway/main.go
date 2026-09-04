package main

import (
	"context"
	"errors"
	"log"
	"net/http"
	"os"
	"os/signal"
	"syscall"
	"time"

	"gateway/internal/blacklist"
	"gateway/internal/config"
	"gateway/internal/httpapi"
	"gateway/internal/jwks"
	"gateway/internal/mq"
	"gateway/internal/proxy"
)

func main() {
	cfg, err := config.Load()
	if err != nil {
		log.Fatalf("Config initialization failed: %v", err)
	}

	ctx, cancel := context.WithCancel(context.Background())
	defer cancel()

	km, err := jwks.NewKeyManager(cfg.JwksURL)
	if err != nil {
		log.Fatalf("Failed to initialize JWKS manager: %v", err)
	}

	blService, err := blacklist.NewBlacklistService(cfg.RedisAddr)
	if err != nil {
		log.Fatalf("Failed to connect Redis: %v", err)
	}

	mqConsumer, err := mq.NewConsumer(mq.ConsumerConfig{
		AmqpURL:      cfg.RabbitMQURL,
		ExchangeName: cfg.RabbitMQExchange,
		RoutingKey:   cfg.RabbitMQRoutingKey,
		QueueName:    cfg.RabbitMQQueue,
	}, blService)
	if err != nil {
		log.Printf("Warning: RabbitMQ connection failed: %v (running without live revocation sync)", err)
	} else {
		defer mqConsumer.Close()
		if err := mqConsumer.Start(ctx); err != nil {
			log.Printf("MQ consumer error: %v", err)
		}
	}

	router := proxy.NewRouter()

	if err := router.AddRoute("/api/v1/auth", cfg.AuthServiceURL, false); err != nil {
		log.Fatalf("Failed to register auth route: %v", err)
	}

	if err := router.AddRoute("/.well-known", cfg.AuthServiceURL, false); err != nil {
		log.Fatalf("Failed to register jwks route: %v", err)
	}

	//add other services here

	gatewayHandler := httpapi.NewGatewayHandler(km, blService, router)

	server := &http.Server{
		Addr:         ":" + cfg.Port,
		Handler:      gatewayHandler,
		ReadTimeout:  15 * time.Second,
		WriteTimeout: 15 * time.Second,
	}

	go func() {
		log.Printf("API Gateway listening on port :%s", cfg.Port)
		if err := server.ListenAndServe(); err != nil && !errors.Is(err, http.ErrServerClosed) {
			log.Fatalf("HTTP server error: %v", err)
		}
	}()

	stopChan := make(chan os.Signal, 1)
	signal.Notify(stopChan, os.Interrupt, syscall.SIGTERM)
	<-stopChan

	log.Println("Shutting down Gateway gracefully...")

	shutdownCtx, shutdownCancel := context.WithTimeout(context.Background(), 5*time.Second)
	defer shutdownCancel()

	if err := server.Shutdown(shutdownCtx); err != nil {
		log.Printf("Forced shutdown error: %v", err)
	}

	log.Println("Gateway stopped successfully.")
}
