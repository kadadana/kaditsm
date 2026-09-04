package blacklist

import (
	"context"
	"errors"
	"fmt"
	"time"

	"github.com/redis/go-redis/v9"
)

type BlacklistService struct {
	client *redis.Client
}

func NewBlacklistService(redisAddr string) (*BlacklistService, error) {
	client := redis.NewClient(&redis.Options{
		Addr: redisAddr,
		DB:   0,
	})

	ctx, cancel := context.WithTimeout(context.Background(), 3*time.Second)
	defer cancel()

	if err := client.Ping(ctx).Err(); err != nil {
		return nil, fmt.Errorf("redis connection failed: %w", err)
	}

	return &BlacklistService{client: client}, nil
}

// AddToBlacklist MQ consumer tarafından tetiklenir, jti'yi expire süresi kadar Redis'e yazar
func (b *BlacklistService) AddToBlacklist(ctx context.Context, jti string, ttl time.Duration) error {
	key := fmt.Sprintf("blacklist:%s", jti)
	err := b.client.Set(ctx, key, "revoked", ttl).Err()
	if err != nil {
		return fmt.Errorf("failed to write blacklist key to redis: %w", err)
	}
	return nil
}

// IsBlacklisted Gateway handler tarafından her gelen istekte kontrol edilir
func (b *BlacklistService) IsBlacklisted(ctx context.Context, jti string) (bool, error) {
	key := fmt.Sprintf("blacklist:%s", jti)
	val, err := b.client.Get(ctx, key).Result()

	if errors.Is(err, redis.Nil) {
		return false, nil
	} else if err != nil {
		return false, fmt.Errorf("redis lookup failed: %w", err)
	}

	return val == "revoked", nil
}
