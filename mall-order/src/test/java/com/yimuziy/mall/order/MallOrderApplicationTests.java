package com.yimuziy.mall.order;

import com.yimuziy.mall.order.entity.OrderEntity;
import com.yimuziy.mall.order.entity.OrderReturnReasonEntity;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Date;
import java.util.UUID;

@Slf4j
@SpringBootTest
class MallOrderApplicationTests {


    @Autowired
    AmqpAdmin amqpAdmin;

    @Autowired
    RabbitTemplate rabbitTemplate;

    @Test
    public void sendMessageTest(){


        //1、发送消息,如果发送的消息是个对象，我们会使用序列化机制，将对象写出去。对象必须实现Serializable接口
        String msg = "Hello World";

        //2、发送的对象类型的消息，可以是一个json
        for (int i = 0; i < 10; i++) {
            if(1%2==0) {
                OrderReturnReasonEntity reasonEntity = new OrderReturnReasonEntity();
                reasonEntity.setId(1L);
                reasonEntity.setCreateTime(new Date());
                reasonEntity.setName("哈哈哈-" + i);
                rabbitTemplate.convertAndSend("hello-java-exchange", "hello.java", reasonEntity);
            }else{
                OrderEntity entity = new OrderEntity();
                entity.setOrderSn(UUID.randomUUID().toString());
                rabbitTemplate.convertAndSend("hello-java-exchange", "hello.java", entity);
            }
        }
        log.info("消息发送完成{}");
    }


    /**
     * 1、如何创建Exchange[hello-java-exchange]、Queue、Binding
     *      1）、使用AmqpAdmin进行创建
     * 2、如何收发消息
     */
    @Test
    void contextLoads() {
        //amqpAdmin
        //Exchange
        /**
         * 	public DirectExchange(String name, boolean durable, boolean autoDelete, Map<String, Object> arguments) {
         */
        DirectExchange directExchange = new DirectExchange("hello-java-exchange",true,false);
        amqpAdmin.declareExchange(directExchange);
        log.info("Exchange[{}]创建成功","hello-java-exchange");
    }

    @Test
    public void createQueue(){
        /**
         * public Queue(String name, boolean durable, boolean exclusive, boolean autoDelete)
         */
        Queue queue = new Queue("hello-java-queue",true,false,false);
        amqpAdmin.declareQueue(queue);
        log.info("Queue[{}]创建成功","hello-java-queue");
    }

    @Test
    public void craeteBinding(){
        /**
         * String destination【目的地】,
         * DestinationType destinationType【目的地的类型】,
         * String exchange 【交换机】,
         * String routingKey 【路由键】,
         * @Nullable Map<String, Object> arguments 【自定义参数】
         */
        //将 exchange指定的交换机和destination目的地进行绑定，使用rotingKey作为指定的路由键
        Binding binding = new Binding("hello-java-queue",
                Binding.DestinationType.QUEUE,
                "hello-java-exchange",
                "hello.java",null);
        amqpAdmin.declareBinding(binding);
        log.info("Binding[{}]创建成功","hello-java-binding");
    }








}
