package com.dk.study.core.mq.rabbitmq.basic.routing;

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
public class ConsumerRoutingListener {

    @RabbitListener(
            bindings = {
                    @QueueBinding(
                            value = @Queue(value ="testRouting1",durable = "false" ),
                            exchange = @Exchange(
                                    value = "directTest",
                                    type = ExchangeTypes.DIRECT,
                                    durable = "false"
                            ),
                            key = {"rout.info","rout.error"}
                    )
            }
    )
    public void testRoutingListener1(String message){
        log.info("message1收到 = " + message);
    }


    @RabbitListener(
            bindings = {
                    @QueueBinding(
                            value = @Queue(value = "testRouting2",durable = "false"),
                            exchange = @Exchange(
                                    value = "directTest",
                                    type = ExchangeTypes.DIRECT,
                                    durable = "false"
                            ),
                            key = {"rout.error"}
                    )
            }
    )
    public void testRoutingListener2(String message){
        log.info("message2收到 = " + message);
    }
}
