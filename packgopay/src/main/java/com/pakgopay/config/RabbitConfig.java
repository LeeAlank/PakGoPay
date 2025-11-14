package com.pakgopay.config;

import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

//@Configuration
public class RabbitConfig {

    //@Bean
    public ConnectionFactory connectionFactory() {
        CachingConnectionFactory factory = new CachingConnectionFactory("localhost");
        factory.setHost("8.148.79.164");
        factory.setPort(5672);
        factory.setUsername("zf");
        factory.setPassword("zf@qwe.123");
        return factory;
    }

    //@Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        return new RabbitTemplate(connectionFactory);
    }
}
