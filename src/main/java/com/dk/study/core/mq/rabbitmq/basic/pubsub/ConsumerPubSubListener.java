package com.dk.study.core.mq.rabbitmq.basic.pubsub;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * @author dk
 * @date 2022/08/26 10:11
 **/
@Component
@Slf4j
public class ConsumerPubSubListener {

    @RabbitListener(bindings = {
            @QueueBinding(
                    value = @Queue(value = "testPubSub1",durable = "false"),
                    exchange = @Exchange(value = "fanoutName",type = ExchangeTypes.FANOUT,durable = "false")
                )
    })
    public void testPubSubListener1(String message) {
        log.info("message1:{}", message);
    }

    @RabbitListener(bindings = {
            @QueueBinding(
                    value = @Queue(value = "testPubSub2",durable = "false"),
                    exchange = @Exchange(value = "fanoutName",type = ExchangeTypes.FANOUT,durable = "false")
            )
    })
    public void testPubSubListener2(String message) {
        log.info("message2:{}", message);
    }

    @RabbitListener(bindings = {
            @QueueBinding(
                    value = @Queue(value = "testPubSub3",durable = "false"),
                    exchange = @Exchange(value = "fanoutName",type = ExchangeTypes.FANOUT,durable = "false")
            )
    })
    public void testPubSubListener3(String message) {
        log.info("message3:{}", message);
    }
}
