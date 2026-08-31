package com.kaditsm.auth.adapter.out.messaging;

import com.kaditsm.auth.domain.port.out.TokenEventPublisherPort;
import com.kaditsm.auth.domain.event.TokenBlacklistedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class RabbitMqTokenPublisherAdapter implements TokenEventPublisherPort {

    private static final Logger log = LoggerFactory.getLogger(RabbitMqTokenPublisherAdapter.class);

    private final RabbitTemplate rabbitTemplate;
    private final String exchangeName;
    private final String routingKey;

    public RabbitMqTokenPublisherAdapter(
            RabbitTemplate rabbitTemplate,
            @Value("${rabbitmq.exchange.token-revocation:auth.token.exchange}") String exchangeName,
            @Value("${rabbitmq.routingkey.blacklisted-token:auth.token.blacklisted}") String routingKey) {
        this.rabbitTemplate = rabbitTemplate;
        this.exchangeName = exchangeName;
        this.routingKey = routingKey;
    }

    @Override
    public void publishTokenBlacklisted(TokenBlacklistedEvent event) {
        try {
            rabbitTemplate.convertAndSend(exchangeName, routingKey, event);
            log.info("TokenBlacklistedEvent published. Exchange: '{}', RoutingKey: '{}', UserId: {}",
                    exchangeName, routingKey, event.identityId());
        } catch (Exception e) {
            log.error("Failed to publish TokenBlacklistedEvent for UserId: {}", event.identityId(), e);
        }
    }
}