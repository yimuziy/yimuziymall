package com.yimuziy.mall.order.config;

import com.rabbitmq.client.Channel;
import com.yimuziy.mall.order.entity.OrderEntity;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.listener.Topic;
import org.springframework.lang.Nullable;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author: ywz
 * @createDate: 2021/3/2
 * @description:
 */
@Configuration
public class MyMQConfig {



    //@Bean  Binding，Queue，Exchange


    /**
     * 容器中的  Binding，Queue，Exchange  都会自动创建(RabbitMQ没有的情况)
     * RabbitMQ  只要有。@Bean属性发生变化也不会覆盖
     * @return
     */
    @Bean
    public Queue orderDelayQueue(){

        Map<String, Object> arguments = new HashMap<>();
        /**
         * x-dead-letter-exchange: order-event-exchange
         * x-dead-letter-routing-key: order.release.order
         * x-message-ttl: 60000
         */
        arguments.put("x-dead-letter-exchange","order-event-exchange");
        arguments.put("x-dead-letter-routing-key","order.release.order");
        arguments.put("x-message-ttl",60000);

        //String name, boolean durable, boolean exclusive, boolean autoDelete,@Nullable Map<String, Object> arguments
        Queue queue = new Queue("order.delay.queue", true, false, false,arguments);

        return queue;
    }

    @Bean
    public Queue orderReleaseOrderQueue(){
        //order.release.order.queue
        Queue queue = new Queue("order.release.order.queue", true, false, false);
        return queue;
    }

    @Bean
    public Exchange orderEventExchange(){
        //String name, boolean durable, boolean autoDelete, Map<String, Object> arguments
        return  new TopicExchange("order-event-exchange",true,false);

    }

    @Bean
    public Binding orderCreateOrderBingding(){
        //String destination, DestinationType destinationType, String exchange, String routingKey, @Nullable Map<String, Object> arguments
        return new Binding("order.delay.queue",
                Binding.DestinationType.QUEUE,
                "order-event-exchange",
                "order.create.order",
                null);
    }

    @Bean
    public Binding orderReleaseOrderBingding(){
        return new Binding("order.release.order.queue",
                Binding.DestinationType.QUEUE,
                "order-event-exchange",
                "order.release.order",
                null);
    }


    /**
     * 订单释放直接和库存释放进行绑定
     * @return
     */
    @Bean
    public Binding orderReleaseOtherBingding(){
        return new Binding("stock.release.stock.queue",
                Binding.DestinationType.QUEUE,
                "order-event-exchange",
                "order.release.other.#",
                null);
    }



}
