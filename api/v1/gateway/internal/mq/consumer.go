package mq

import (
	"context"
	"encoding/json"
	"fmt"
	"log"
	"time"

	amqp "github.com/rabbitmq/amqp091-go"
)

type Blacklister interface {
	AddToBlacklist(ctx context.Context, jti string, ttl time.Duration) error
}
type TokenBlacklistedEvent struct {
	TokenJti      string    `json:"tokenJti"`
	IdentityID    string    `json:"identityId"`
	BlacklistedAt time.Time `json:"blacklistedAt"`
	ExpiresAt     time.Time `json:"expiresAt"`
}

type ConsumerConfig struct {
	AmqpURL      string
	ExchangeName string
	RoutingKey   string
	QueueName    string
}

type Consumer struct {
	conn      *amqp.Connection
	channel   *amqp.Channel
	blacklist Blacklister
	cfg       ConsumerConfig
}

func NewConsumer(cfg ConsumerConfig, bl Blacklister) (*Consumer, error) {
	conn, err := amqp.Dial(cfg.AmqpURL)
	if err != nil {
		return nil, fmt.Errorf("rabbitmq connection failed: %w", err)
	}

	ch, err := conn.Channel()
	if err != nil {
		conn.Close()
		return nil, fmt.Errorf("failed to open channel: %w", err)
	}

	err = ch.ExchangeDeclare(
		cfg.ExchangeName,
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
		cfg.QueueName,
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
		cfg.RoutingKey,
		cfg.ExchangeName,
		false,
		nil,
	)
	if err != nil {
		ch.Close()
		conn.Close()
		return nil, fmt.Errorf("failed to bind queue to exchange: %w", err)
	}

	return &Consumer{
		conn:      conn,
		channel:   ch,
		blacklist: bl,
		cfg:       cfg,
	}, nil
}

func (c *Consumer) Start(ctx context.Context) error {
	msgs, err := c.channel.Consume(
		c.cfg.QueueName,
		"",
		false,
		false,
		false,
		false,
		nil,
	)
	if err != nil {
		return fmt.Errorf("failed to register consumer: %w", err)
	}

	go func() {
		for {
			select {
			case <-ctx.Done():
				log.Println("MQ consumer stopping...")
				return
			case msg, ok := <-msgs:
				if !ok {
					log.Println("MQ channel closed")
					return
				}
				c.handleMessage(ctx, msg)
			}
		}
	}()

	return nil
}

func (c *Consumer) handleMessage(ctx context.Context, msg amqp.Delivery) {
	var evt TokenBlacklistedEvent
	if err := json.Unmarshal(msg.Body, &evt); err != nil {
		log.Printf("Invalid TokenBlacklistedEvent payload: %v", err)
		_ = msg.Nack(false, false)
		return
	}

	remaining := time.Until(evt.ExpiresAt)
	if remaining <= 0 {
		_ = msg.Ack(false)
		return
	}

	if err := c.blacklist.AddToBlacklist(ctx, evt.TokenJti, remaining); err != nil {
		log.Printf("Failed to blacklist jti %s: %v", evt.TokenJti, err)
		_ = msg.Nack(false, true)
		return
	}

	log.Printf("Token blacklisted successfully: jti=%s, remaining=%v", evt.TokenJti, remaining)
	_ = msg.Ack(false)
}

func (c *Consumer) Close() {
	if c.channel != nil {
		_ = c.channel.Close()
	}
	if c.conn != nil {
		_ = c.conn.Close()
	}
}
