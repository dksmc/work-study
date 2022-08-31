package com.dk.study.core.mq.rabbitmq.features.controller;

import com.dk.study.core.mq.rabbitmq.RabbitMqConfig;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.MessageProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

import static com.dk.study.core.mq.rabbitmq.RabbitMqConfig.*;

/**
 * @author dk
 * @date 2022/08/26 10:02
 **/
@Slf4j
@Validated
@RestController
@RequestMapping("/rabbitmq/features")
public class ProducerFeaturesController {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    /**
     * TTL:最大存活时间(ms)
     *      过期时间支持队列与消息,如果都设置了则取TTL最小的执行。如果消费时间超过TTL，则消息就会成为死信
     *      设置消息TTL，则消息从入队开始，如果到时间后还未被消费则成为私信
     *          setExpiration(String expiration)
     *      设置队列TTL，则队列在时间到期后队列里的消息统一成为私信
     *          QueueBuilder.durable(FEATURES_QUEUE_TTL_QUEUE).ttl(10*1000).build();
     *
     */
    @GetMapping("/testTTL/{delayTime}")
    public void testTTL(@PathVariable(value = "delayTime") String delayTime) {
        rabbitTemplate.convertAndSend(RabbitMqConfig.FEATURES_TTL_EXCHANGE,RabbitMqConfig.ROUTING_KEY_MESSAGE_TTL,"测试消息的ttl", message -> {
            message.getMessageProperties().setExpiration(delayTime);
            return message;
        });
        rabbitTemplate.convertAndSend(RabbitMqConfig.FEATURES_TTL_EXCHANGE,RabbitMqConfig.ROUTING_KEY_QUEUE_TTL,"测试队列的ttl");
    }


    /**
     * 死信队列
     *  当消息在一个队列中变成死信之后，他能被重新发送到另一个交换器中，这个交换器称为死信交换机，与该交换机绑定的队列称为死信队列。
     *     消息变成死信有下面几种情况：
     *         -- 消息被拒绝
     *         -- 消息TTL过期
     *         -- 队列达到最大长度
     *     死信队列有什么用？
     *         当发生异常的时候，消息不能够被消费者正常消费，被加入到了死信队列中。后续的程序可以根据死信队列中的内容分析当时发生的异常，进而改善和优化系统。
     * 队列绑定死信交换机：给队列设置x-dead-letter-exchange  x-dead-letter-routing-key
     */
    @GetMapping("/testDeadLetter")
    public void testDeadLetter() {
        rabbitTemplate.convertAndSend(NORMAL_EXCHANGE,KEY2,"测试ttl成为死信");
        for (int i = 1; i <= 15; i++) {
            rabbitTemplate.convertAndSend(NORMAL_EXCHANGE,KEY2,"测试队列最大长度成为死信" + i);
        }
        rabbitTemplate.convertAndSend(NORMAL_EXCHANGE,KEY2,"测试消息被拒绝成为死信");
    }


    /**
     *  延迟队列
     *      由于rabbitmq没有给出延迟队列的功能，可以使用私信队列+TTL实现,
     *      但是在最开始的时候，就介绍过如果使用在消息属性上设置 TTL 的方式,
     *      消息可能并不会按时“死亡“，因为 RabbitMQ 只会检查第一个消息是否过期，
     *      如果过期则丢到死信队列， 如果第一个消息的延时时长很长，而第二个消息的延时时长很短，第二个消息并不会优先得到执行。
     *  rabbitmq提供了实现延迟队列的插件，可以基于此插件实现延迟队列
     *      启用插件 rabbitmq-plugins enable rabbitmq_delayed_message_exchange
     *      自定义交换机 并声明类型为x-delayed-message，设置x-delayed-type参数
     *      生产段发送消息提供延迟时间  setDelay()
     */
    @GetMapping("/testDelayed")
    public void testDelayed() {
        log.info(new Date() + "");
        rabbitTemplate.convertAndSend(FEATURES_DELAYED_EXCHANGE,ROUTING_KEY_DELAYED,"测试基于插件的延迟队列",msg -> {
            msg.getMessageProperties().setDelay(5000);
            return msg;
        });
    }


    /**
     * 消息确认：为了保证消息从队列可靠地到达消费者，RabbitMQ提供了消息确认机制。
     *     生产者消息确认 callback
     *         1.表示消息从producer到exchange：只要消息被broker接收就触发confirmCallback
     *             publisher-confirm-type: correlated
     *             实现RabbitTemplate.ConfirmCallback接口中的confirm方法，在方法中如果ack为true则发送成功，否则失败
     *         2.表示消息从exchange到queue：如果消息未能投递到queue则触发returnCallback
     *             开启publisher-returns: true
     *             实现RabbitTemplate.ReturnsCallback接口中的returnedMessage方法，在方法中可做处理
     *             mandatory参数：如果消息没有路由到queue
     *                  true：则返回给消息发送发送方重新发送
     *                  false：直接丢弃(默认)
     *     消费者消息确认 ACK
     *         3.表示consumer收到消息的确认方式
     *             执行一个任务可能需要花费几秒钟，你可能会担心如果一个消费者在执行任务过程中挂掉了。
     *             一旦RabbitMQ将消息分发给了消费者，就会从内存中删除。
     *             在这种情况下，如果正在执行任务的消费者宕机，会丢失正在处理的消息和分发给这个消费者但尚未处理的消息。
     *             但是，我们不想丢失任何任务，如果有一个消费者挂掉了，那么我们应该将分发给它的任务交付给另一个消费者去处理。为了确保消息不会丢失，RabbitMQ支持消息应答。
     *             消费者发送一个消息应答，告诉RabbitMQ这个消息已经接收并且处理完毕了。RabbitMQ就可以删除它了。
     *             消费者订阅队列的时候，可以指定autoAck参数，
     *         方法：
     *             acknowledge-mode: (manual:手动应答,auto:自动应答,none:不管消费是否成功mq都会把消息剔除，这是默认配置方式。)
     *             声明监听@RabbitListener或者实现监听ChannelAwareMessageListener接口
     *             如果消息成功处理，调用basicAck()：可设置批量签收
     *             如果消息处理失败，调用basicNack()/basicReject(),可设置broker重新发送给consumer,或做补偿机制
     */
    @GetMapping("/testMessage")
    public void testMessage() {
        rabbitTemplate.convertAndSend(FEATURES_MESSAGE_EXCHANGE,ROUTING_KEY_MESSAGE,"测试消息确认",new CorrelationData("1"));
        rabbitTemplate.convertAndSend(FEATURES_MESSAGE_EXCHANGE + "111",ROUTING_KEY_MESSAGE,"测试交换机错误消息确认",new CorrelationData("2"));
        rabbitTemplate.convertAndSend(FEATURES_MESSAGE_EXCHANGE,ROUTING_KEY_MESSAGE + "111","测试队列错误消息确认",new CorrelationData("3"));
    }

    @GetMapping("testPrioryMessage")
    public void testPrioryMessage(){
        rabbitTemplate.convertAndSend("EXCHANGE_NAME","ROUTING_KEY","消息",message -> {
            message.getMessageProperties().setPriority(5);
            return message;
        });
    }
}
