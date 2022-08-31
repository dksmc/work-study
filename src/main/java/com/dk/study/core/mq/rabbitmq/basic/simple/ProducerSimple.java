package com.dk.study.core.mq.rabbitmq.basic.simple;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author dk
 * @date 2022/08/26 10:02
 **/
@Component
public class ProducerSimple {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void testSimple() {
        String queueName = "simple.queue";
        String message = "hello,simple.queue";
        rabbitTemplate.convertAndSend(queueName, message);
    }

}
