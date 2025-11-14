package com.pakgopay.mq;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class TestReceiver {

    @RabbitListener(queuesToDeclare = @Queue("test"))
    public void reveiveMessage(String message) throws JsonProcessingException {
        //System.out.println("I get a test message: " + new ObjectMapper().readValue(message, TestMessage.class).getContent());
        System.out.println(new Date() + "You get it" + message);
    }
}
