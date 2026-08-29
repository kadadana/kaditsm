package com.kaditsm.auth.adapter.out.messaging.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMqConfig {

    @Value("${rabbitmq.exchange.token-revocation:auth.token.exchange}")
    private String exchangeName;

    @Value("${rabbitmq.queue.blacklisted-token:auth.token.blacklisted.queue}")
    private String queueName;

    @Value("${rabbitmq.routingkey.blacklisted-token:auth.token.blacklisted}")
    private String routingKey;

    @Bean
    public ConnectionFactory connectionFactory(
            @Value("${spring.rabbitmq.host}") String host,
            @Value("${spring.rabbitmq.port}") int port,
            @Value("${spring.rabbitmq.username}") String username,
            @Value("${spring.rabbitmq.password}") String password) {
        CachingConnectionFactory factory = new CachingConnectionFactory(host, port);
        factory.setUsername(username);
        factory.setPassword(password);
        return factory;
    }

    @Bean
    public TopicExchange tokenExchange() {
        return new TopicExchange(exchangeName, true, false);
    }

    @Bean
    public Queue tokenBlacklistQueue() {
        return new Queue(queueName, true);
    }

    @Bean
    public Binding tokenBlacklistBinding(Queue tokenBlacklistQueue, TopicExchange tokenExchange) {
        return BindingBuilder.bind(tokenBlacklistQueue)
                .to(tokenExchange)
                .with(routingKey);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new JacksonJsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory, MessageConverter jsonMessageConverter) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jsonMessageConverter);
        return template;
    }
}