package com.metainnotech.authservice.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String EMAIL_EXCHANGE = "auth.email.exchange";
    public static final String EMAIL_QUEUE = "auth.email.queue";
    public static final String EMAIL_ROUTING_KEY = "auth.email.verify";

    @Bean
    Exchange emailExchange() {
        return ExchangeBuilder.topicExchange(EMAIL_EXCHANGE).durable(true).build();
    }

    @Bean
    Queue emailQueue() {
        return QueueBuilder.durable(EMAIL_QUEUE).build();
    }

    @Bean
    Binding emailBinding(Queue emailQueue, Exchange emailExchange) {
        return BindingBuilder.bind(emailQueue).to(emailExchange).with(EMAIL_ROUTING_KEY).noargs();
    }
}
