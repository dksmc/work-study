package com.dk.study.core.mq.rabbitmq.basic.simple;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * @author dk
 * @date 2022/08/26 10:11
 **/
@Component
@Slf4j
public class ConsumerSimpleListener {

    @RabbitListener(queuesToDeclare = @Queue(value = "simple.queue",durable = "false"))
    public void testSimpleListener(String message) {
        log.info("message:{}", message);
    }
}
