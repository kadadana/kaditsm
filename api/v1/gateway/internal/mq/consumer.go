package mq

import (
	"context"
	"encoding/json"
	"fmt"
	"log"
	"time"

	"gateway/internal/blacklist"

	amqp "github.com/rabbitmq/amqp091-go"
)

type TokenBlacklistedPayload struct {
	TokenJti      string    `json:"tokenJti"`
	IdentityId    string    `json:"identityId"`
	BlacklistedAt time.Time `json:"blacklistedAt"`
	ExpiresAt     time.Time `json:"expiresAt"`
}

type BlacklistConsumer struct {
	conn             *amqp.Connection
	channel          *amqp.Channel
	queueName        string
	exchangeName     string
	routingKey       string
	blacklistService *blacklist.BlacklistService
}

func NewBlacklistConsumer(
	amqpURL, exchange, routingKey, queue string,
	bs *blacklist.BlacklistService,
) (*BlacklistConsumer, error) {
	conn, err := amqp.Dial(amqpURL)
	if err != nil {
		return nil, fmt.Errorf("failed to connect to rabbitmq: %w", err)
	}

	ch, err := conn.Channel()
	if err != nil {
		conn.Close()
		return nil, fmt.Errorf("failed to open rabbitmq channel: %w", err)
	}

	err = ch.ExchangeDeclare(
		exchange,
		"topic",
		true,
		false,
		false,
		false,
		nil,
	)
	if err != nil {
		ch.Close()
		conn.Close()
		return nil, fmt.Errorf("failed to declare exchange: %w", err)
	}

	q, err := ch.QueueDeclare(
		queue,
		true,
		false,
		false,
		false,
		nil,
	)
	if err != nil {
		ch.Close()
		conn.Close()
		return nil, fmt.Errorf("failed to declare queue: %w", err)
	}

	err = ch.QueueBind(
		q.Name,
		routingKey,
		exchange,
		false,
		nil,
	)
	if err != nil {
		ch.Close()
		conn.Close()
		return nil, fmt.Errorf("failed to bind queue to exchange: %w", err)
	}

	return &BlacklistConsumer{
		conn:             conn,
		channel:          ch,
		queueName:        q.Name,
		exchangeName:     exchange,
		routingKey:       routingKey,
		blacklistService: bs,
	}, nil
}

func (c *BlacklistConsumer) Start(ctx context.Context) error {
	msgs, err := c.channel.Consume(
		c.queueName,
		"gateway-blacklist-consumer",
		false,
		false,
		false,
		false,
		nil,
	)
	if err != nil {
		return fmt.Errorf("failed to register a consumer: %w", err)
	}

	log.Printf("[RabbitMQ] Listening on queue '%s' (exchange: '%s', routingKey: '%s')", c.queueName, c.exchangeName, c.routingKey)

	go func() {
		for {
			select {
			case <-ctx.Done():
				log.Println("[RabbitMQ] Consumer worker stopped.")
				return
			case d, ok := <-msgs:
				if !ok {
					log.Println("[RabbitMQ] Message delivery channel closed.")
					return
				}

				log.Printf("[RabbitMQ] Received message: %s", string(d.Body))

				var payload TokenBlacklistedPayload
				if err := json.Unmarshal(d.Body, &payload); err != nil {
					log.Printf("[RabbitMQ] Error parsing payload: %v. Rejecting message.", err)
					_ = d.Nack(false, false)
					continue
				}

				ttl := time.Until(payload.ExpiresAt)
				if ttl <= 0 {
					ttl = 24 * time.Hour
				}

				if err := c.blacklistService.AddToBlacklist(ctx, payload.TokenJti, ttl); err != nil {
					log.Printf("[RabbitMQ] Failed to write to Redis: %v. Requeuing...", err)
					_ = d.Nack(false, true)
					continue
				}

				log.Printf("[RabbitMQ] JTI successfully blacklisted in Redis: %s (TTL: %v, IdentityId: %s)", payload.TokenJti, ttl, payload.IdentityId)
			}
		}
	}()

	return nil
}

func (c *BlacklistConsumer) Close() {
	if c.channel != nil {
		_ = c.channel.Close()
	}
	if c.conn != nil {
		_ = c.conn.Close()
	}
}
