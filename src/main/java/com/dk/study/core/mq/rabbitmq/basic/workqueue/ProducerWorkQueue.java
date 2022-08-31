package com.dk.study.core.mq.rabbitmq.basic.workqueue;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author dk
 * @date 2022/08/26 10:32
 **/
@Slf4j
@Component
public class ProducerWorkQueue {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void testWorkQueue(){
        String queueName = "workQueue";
        String message = "hello,work.queue__";
        for (int i = 1; i <= 20; i++) {
            rabbitTemplate.convertAndSend(queueName,message + i);
            log.info("发送的消息内容:{}",message + i);
        }
    }
}
