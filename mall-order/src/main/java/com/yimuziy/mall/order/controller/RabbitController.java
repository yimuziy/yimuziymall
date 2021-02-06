package com.yimuziy.mall.order.controller;

import com.yimuziy.mall.order.entity.OrderEntity;
import com.yimuziy.mall.order.entity.OrderReturnReasonEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.UUID;

/**
 * @author ywz
 * @date 2021/1/9 18:37
 * @description
 */
@RestController
@Slf4j
public class RabbitController {

    @Autowired
    RabbitTemplate rabbitTemplate;

    @GetMapping("/sendMq")
    public String sendMq(@RequestParam(value = "num",defaultValue = "10") Integer num){
        for (int i = 0; i < num; i++) {
            if(i%2==0) {
                OrderReturnReasonEntity reasonEntity = new OrderReturnReasonEntity();
                reasonEntity.setId(1L);
                reasonEntity.setCreateTime(new Date());
                reasonEntity.setName("哈哈哈-" + i);
                rabbitTemplate.convertAndSend("hello-java-exchange", "hello.java", reasonEntity,new CorrelationData(UUID.randomUUID().toString()));
            }else{
                OrderEntity entity = new OrderEntity();
                entity.setOrderSn(UUID.randomUUID().toString());
                rabbitTemplate.convertAndSend("hello-java-exchange", "hello22.java", entity,new CorrelationData(UUID.randomUUID().toString()));
            }
        }
        return "ok";
    }
}
