package com.dk.study.core.mq.rabbitmq.features.listener;

import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Date;

import static com.dk.study.core.mq.rabbitmq.RabbitMqConfig.*;

/**
 * @author dk
 * @date 2022/08/26 10:11
 **/
@Slf4j
@Component
public class ConsumerFeaturesListener {

    @RabbitListener(queues = NORMAL_QUEUE)
    public void testFeaturesListener(Message message, Channel channel) throws Exception {
        String msg = new String(message.getBody(), StandardCharsets.UTF_8);
        try{
            log.info("处理业务---->message1:{}", msg);
            int i = 1/0;
            channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
        } catch (Exception e){
            log.info("拒绝消息");
            channel.basicNack(message.getMessageProperties().getDeliveryTag(),false,false);
        }
    }

    @RabbitListener(queues = FEATURES_DELAYED_QUEUE)
    public void testDelayedListener(Message message, Channel channel) throws IOException {
        channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
        log.info("消费时间：{}；消息内容：{}",new Date() + "",new String(message.getBody(),StandardCharsets.UTF_8));
    }

    @RabbitListener(queues = FEATURES_MESSAGE_QUEUE)
    public void testMessageListener(Message message, Channel channel) throws IOException {
        String msg = new String(message.getBody(), StandardCharsets.UTF_8);
        try{
            log.info("处理业务---->message1:{}", msg);
            /**
             * 确认
             * deliveryTag：表示消息投递序号，每次消费消息或者消息重新投递后，deliveryTag都会增加
             * multiple：是否批量确认，值为 true 则会一次性 ack所有小于当前消息 deliveryTag 的消息。
             */
            channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
        } catch (Exception e){
            log.info("拒绝消息");
            /**
             * 拒绝(可批量)
             * deliveryTag：表示消息投递序号，每次消费消息或者消息重新投递后，deliveryTag都会增加
             * multiple:是否批量确认，值为 true 则会一次性 ack所有小于当前消息 deliveryTag 的消息。
             * requeue:是否重新放入队列
             */
            channel.basicNack(message.getMessageProperties().getDeliveryTag(),false,false);
            /**
             * 拒绝单条
             * deliveryTag：表示消息投递序号，每次消费消息或者消息重新投递后，deliveryTag都会增加
             * requeue:是否重新放入队列
             */
            channel.basicReject(message.getMessageProperties().getDeliveryTag(),false);
        }
        log.info("消费时间：{}；消息内容：{}",new Date() + "",msg);
    }
}
