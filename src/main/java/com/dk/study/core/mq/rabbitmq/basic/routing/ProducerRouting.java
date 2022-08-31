package com.dk.study.core.mq.rabbitmq.basic.routing;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author dk
 * @date 2022/08/26 10:02
 **/
@Component
public class ProducerRouting {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void testRouting1() {
        rabbitTemplate.convertAndSend("directTest","rout.info","发送info的key的路由消息");
    }

    public void testRouting2(){
        rabbitTemplate.convertAndSend("directTest","rout.error","发送error的key的路由消息");
    }

}
