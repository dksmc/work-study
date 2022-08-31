package com.dk.study.core.mq.rabbitmq;

import com.rabbitmq.client.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author dk
 * @date 2022/08/25 11:21
 **/
@Slf4j
@Configuration
public class RabbitMqConfig implements RabbitTemplate.ConfirmCallback,RabbitTemplate.ReturnsCallback,
        DeliverCallback, CancelCallback, ConsumerShutdownSignalCallback {

    // **************************************************死信队列***************************************************

    public static final String DEAD_LETTER_EXCHANGE = "deadLetterExchange";
    public static final String DEAD_LETTER_QUEUE = "deadLetterQueue";
    public static final String NORMAL_EXCHANGE = "normalExchange";
    public static final String NORMAL_QUEUE = "normalQueue";
    public static final String KEY1 = "key1";
    public static final String KEY2 = "key2";

    @Bean
    public DirectExchange deadLetterExchange(){
        return new DirectExchange(DEAD_LETTER_EXCHANGE,false,false);
    }

    @Bean
    public Queue deadLetterQueue(){
        return QueueBuilder.nonDurable(DEAD_LETTER_QUEUE).build();
    }

    @Bean DirectExchange normalExchange(){
        return new DirectExchange(NORMAL_EXCHANGE,false,false);
    }

    @Bean
    public Queue normalQueue(){
        // 设置队列消息ttl为10秒，设置队列最大长度为5
        return QueueBuilder.nonDurable(NORMAL_QUEUE).ttl(10*1000).maxLength(5)
                // 正常队列绑定死信交换机,参数固定为私信交换名称及私信交换及额与死信队列绑定的routingKey
                .deadLetterExchange(DEAD_LETTER_EXCHANGE).deadLetterRoutingKey(KEY1).build();
    }

    @Bean
    public Binding deadLetterQueueBindingDeadLetterExchange(){
        return BindingBuilder.bind(deadLetterQueue()).to(deadLetterExchange()).with(KEY1);
    }

    @Bean
    public Binding normalQueueBindingNormalExchange(){
        return BindingBuilder.bind(normalQueue()).to(normalExchange()).with(KEY2);
    }



    // **************************************************测试消息的TTL***************************************************

    public static final String FEATURES_TTL_EXCHANGE = "featuresTtlExchange";

    public static final String FEATURES_MESSAGE_TTL_QUEUE = "messageTtlQueue";

    public static final String FEATURES_QUEUE_TTL_QUEUE = "queueTtlQueue";

    public static final String ROUTING_KEY_QUEUE_TTL = "key.queue.ttl";

    public static final String ROUTING_KEY_MESSAGE_TTL = "key.message.ttl";

    @Bean
    public DirectExchange featuresTtlExchange(){
        return new DirectExchange(FEATURES_TTL_EXCHANGE,false,false);
    }

    @Bean
    public Queue messageTtlQueue(){
        return QueueBuilder.nonDurable(FEATURES_MESSAGE_TTL_QUEUE).build();
    }

    @Bean
    public Queue queueTtlQueue(){
        return QueueBuilder.nonDurable(FEATURES_QUEUE_TTL_QUEUE).ttl(10*1000).build();
    }

    @Bean
    public Binding messageTtlQueueBindingExchange(){
        return BindingBuilder.bind(messageTtlQueue()).to(featuresTtlExchange()).with(ROUTING_KEY_MESSAGE_TTL);
    }

    @Bean
    public Binding queueTtlQueueBindingExchange(){
        return BindingBuilder.bind(queueTtlQueue()).to(featuresTtlExchange()).with(ROUTING_KEY_QUEUE_TTL);
    }

    // **************************************************测试延迟队列(基于插件)***************************************************

    public static final String FEATURES_DELAYED_EXCHANGE = "delayedExchange";

    public static final String FEATURES_DELAYED_QUEUE = "delayedQueue";

    public static final String ROUTING_KEY_DELAYED = "key.delayed";

    @Bean
    public CustomExchange delayedExchange(){
        Map<String,Object> args = new HashMap<>(2);
        // 自定义延迟交换机的类型
        args.put("x-delayed-type","direct");
        return new CustomExchange(FEATURES_DELAYED_EXCHANGE,"x-delayed-message",false,false,args);
    }

    @Bean
    public Queue delayedQueue(){
        return QueueBuilder.nonDurable(FEATURES_DELAYED_QUEUE).build();
    }

    @Bean
    public Binding delayedQueueBindingDelayedExchange(){
        return BindingBuilder.bind(delayedQueue()).to(delayedExchange()).with(ROUTING_KEY_DELAYED).noargs();
    }

    // **************************************************测试消息确认***************************************************

    public static final String FEATURES_MESSAGE_EXCHANGE = "messageExchange";

    public static final String FEATURES_MESSAGE_QUEUE = "messageQueue";

    public static final String ROUTING_KEY_MESSAGE = "key.message";

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @PostConstruct
    public void init(){
        rabbitTemplate.setConfirmCallback(this);
        rabbitTemplate.setReturnsCallback(this);
    }

    @Bean
    public DirectExchange messageExchange(){
        return new DirectExchange(FEATURES_MESSAGE_EXCHANGE,false,false);
    }

    @Bean
    public Queue messageQueue(){
        return QueueBuilder.nonDurable(FEATURES_MESSAGE_QUEUE).build();
    }

    @Bean
    public Binding messageQueueBindingMessageExchange(){
        return BindingBuilder.bind(messageQueue()).to(messageExchange()).with(ROUTING_KEY_MESSAGE);
    }

    /**
     * producer-->exchange确认的方法,交换机不管是否收到消息都回调此方法
     *
     * @param correlationData 关联信息
     * @param ack true/false 成功/失败
     * @param cause 失败原因
     */
    @Override
    public void confirm(CorrelationData correlationData, boolean ack, String cause) {
        String id = correlationData != null ? correlationData.getId() : "";
        if(ack){
            log.info("交换机已经收到id为：{}的消息。",id);
        } else {
            log.info("交换机未收到id为：{}的消息,原因是：{}",id,cause);
        }
    }

    /**
     * exchange-->queue
     * @param returned 参数封装实体
     */
    @Override
    public void returnedMessage(ReturnedMessage returned) {
        log.info("消息"+ new String(returned.getMessage().getBody(), StandardCharsets.UTF_8) +"被交换机"+returned.getExchange()+"回退！"
                +"退回原因为："+returned.getReplyText());
    }

    // **************************************************优先级队列***************************************************
    @Bean
    public Queue prioryQueue(){
        return QueueBuilder.nonDurable(FEATURES_MESSAGE_QUEUE).maxPriority(10).build();
    }

    // 惰性队列
    @Bean
    public Queue lazyQueue(){
        return QueueBuilder.nonDurable(FEATURES_MESSAGE_QUEUE).lazy().build();
    }

    @Bean
    public DirectExchange alternateExchange(){
        return ExchangeBuilder.directExchange("EXCHANGE_NAME").durable(false).alternate("ALTERNATE_NAME").build();
    }


    /**
     * {@link CancelCallback}:消费端消费消息时会在取消订阅时调用此回调接口
     *
     * @param consumerTag
     */
    @Override
    public void handle(String consumerTag) {
        log.info("执行CancelCallback回调方法---consumerTag:{}",consumerTag);
    }

    /**
     * {@link ConsumerShutdownSignalCallback}:当消费者通道或底层连接关闭时通知回调接口
     * @param consumerTag
     * @param sig
     */
    @Override
    public void handleShutdownSignal(String consumerTag, ShutdownSignalException sig) {
        log.info("执行ConsumerShutdownSignalCallback回调方法---consumerTag:{}",consumerTag);
    }

    /**
     * {@link DeliverCallback}:消息传递时通知的回调接口。
     *
     * @param consumerTag
     * @param message
     */
    @Override
    public void handle(String consumerTag, Delivery message) {
        log.info("执行DeliverCallback回调方法---consumerTag:{}",consumerTag);
        log.info("deliveryTag:{}",message.getEnvelope().getDeliveryTag());
        log.info("消息：{}",new String(message.getBody(),StandardCharsets.UTF_8));
    }
}
