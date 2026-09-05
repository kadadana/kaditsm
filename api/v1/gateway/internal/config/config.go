package config

import (
	"encoding/json"
	"fmt"
	"net/url"
	"os"
)

type Config struct {
	Port               string
	JwksURL            string
	RedisAddr          string
	RabbitMQHost       string
	RabbitMQPort       string
	RabbitMQUser       string
	RabbitMQPass       string
	RabbitMQExchange   string
	RabbitMQRoutingKey string
	RabbitMQQueue      string
	RoutesFilePath     string
}

func Load() (*Config, error) {
	cfg := &Config{
		Port:               getEnv("PORT", "8081"),
		JwksURL:            getEnv("JWKS_URL", ""),
		RedisAddr:          getEnv("REDIS_ADDR", "gateway-redis:6379"),
		RabbitMQHost:       getEnv("RABBITMQ_HOST", "kaditsm-rabbitmq"),
		RabbitMQPort:       getEnv("RABBITMQ_PORT", "5672"),
		RabbitMQUser:       getEnv("RABBITMQ_USER", "guest"),
		RabbitMQPass:       getEnv("RABBITMQ_PASS", "guest"),
		RabbitMQExchange:   getEnv("RABBITMQ_EXCHANGE", "auth.token.exchange"),
		RabbitMQRoutingKey: getEnv("RABBITMQ_ROUTING_KEY", "auth.token.blacklisted"),
		RabbitMQQueue:      getEnv("RABBITMQ_QUEUE", "gateway.token.blacklist.queue"),
		RoutesFilePath:     getEnv("ROUTES_FILE_PATH", "routes.json"),
	}

	return cfg, nil
}

func (c *Config) RabbitMQURL() string {
	escapedUser := url.QueryEscape(c.RabbitMQUser)
	escapedPass := url.QueryEscape(c.RabbitMQPass)
	return fmt.Sprintf("amqp://%s:%s@%s:%s/", escapedUser, escapedPass, c.RabbitMQHost, c.RabbitMQPort)
}

func getEnv(key, fallback string) string {
	if val := os.Getenv(key); val != "" {
		return val
	}
	return fallback
}

type RouteConfig struct {
	PathPrefix string `json:"path_prefix"`
	TargetURL  string `json:"target_url"`
	StripPath  bool   `json:"strip_path"`
}

func LoadRoutes(filePath string) ([]RouteConfig, error) {
	data, err := os.ReadFile(filePath)
	if err != nil {
		return nil, fmt.Errorf("failed to read route file: %w", err)
	}

	var routes []RouteConfig
	if err := json.Unmarshal(data, &routes); err != nil {
		return nil, fmt.Errorf("failed to parse routes json: %w", err)
	}

	return routes, nil
}
