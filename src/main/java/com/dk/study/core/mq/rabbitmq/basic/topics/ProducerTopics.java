package com.dk.study.core.mq.rabbitmq.basic.topics;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author dk
 * @date 2022/08/26 10:02
 **/
@Component
public class ProducerTopics {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void testRouting1() {
        rabbitTemplate.convertAndSend("TopicExchange","topic.info.rabbit","发送info的key的路由消息");
    }

    public void testRouting2(){
        rabbitTemplate.convertAndSend("TopicExchange","topic.error.rabbit","发送error的key的路由消息");
    }

    public void testRouting3(){
        rabbitTemplate.convertAndSend("TopicExchange","topic.warn.mq","发送warn的key的路由消息");
    }

    public void testRouting4(){
        rabbitTemplate.convertAndSend("TopicExchange","topic.debug.mq","发送debug的key的路由消息");
    }

}
