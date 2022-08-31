package com.dk.study.core.mq.rabbitmq.basic.workqueue;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * @author dk
 * @date 2022/08/26 10:40
 **/
@Slf4j
@Component
public class ConsumerWorkQueueListener {

    @RabbitListener(queuesToDeclare = @Queue(value = "workQueue",durable = "false"))
    public void testWorkQueueListener1(String message) throws InterruptedException {
        Thread.sleep(1000);
        log.info("message1   :{}",message);
    }

    @RabbitListener(queuesToDeclare = @Queue(value = "workQueue",durable = "false"))
    public void testWorkQueueListener2(String message) throws InterruptedException {
        Thread.sleep(10000);
        log.info("message2   :{}",message);
    }

}
