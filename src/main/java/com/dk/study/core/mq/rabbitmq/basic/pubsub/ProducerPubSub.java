package com.dk.study.core.mq.rabbitmq.basic.pubsub;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author dk
 * @date 2022/08/26 10:02
 **/
@Component
public class ProducerPubSub {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void testPubSub() {
        String exchangeName = "fanoutName";
        String message = "hello,pubsub__";
        for (int i = 1; i <= 5; i++) {
            rabbitTemplate.convertAndSend(exchangeName,"",message + i);
        }
    }

}
