package config

import (
	"os"

	"github.com/joho/godotenv"
)

type Config struct {
	Port               string
	JwksURL            string
	RedisAddr          string
	RabbitMQURL        string
	AuthServiceURL     string
	RabbitMQExchange   string
	RabbitMQRoutingKey string
	RabbitMQQueue      string
}

func Load() (*Config, error) {
	_ = godotenv.Load()

	cfg := &Config{
		Port:               getEnv("PORT", "8081"),
		JwksURL:            getEnv("JWKS_URL", "http://localhost:8081/.well-known/jwks.json"),
		RedisAddr:          getEnv("REDIS_ADDR", "localhost:6379"),
		RabbitMQURL:        getEnv("RABBITMQ_URL", "amqp://guest:guest@localhost:5672/"),
		AuthServiceURL:     getEnv("AUTH_SERVICE_URL", "http://localhost:8080"),
		RabbitMQExchange:   getEnv("RABBITMQ_EXCHANGE", "auth.token.exchange"),
		RabbitMQRoutingKey: getEnv("RABBITMQ_ROUTING_KEY", "auth.token.blacklisted"),
		RabbitMQQueue:      getEnv("RABBITMQ_QUEUE", "gateway.token.blacklist.queue"),
	}

	return cfg, nil
}

func getEnv(key, fallback string) string {
	if val := os.Getenv(key); val != "" {
		return val
	}
	return fallback
}
