package com.yimuziy.mall.ware.listener;

import com.rabbitmq.client.Channel;
import com.yimuziy.common.to.mq.OrderTo;
import com.yimuziy.common.to.mq.StockLockedTo;
import com.yimuziy.mall.ware.service.WareSkuService;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * @author: ywz
 * @createDate: 2021/3/5
 * @description:
 */
@Service
@RabbitListener(queues = "stock.release.stock.queue")
public class StockReleaseListener {

    @Autowired
    WareSkuService wareSkuService;

    /**
     * 1、库存自动解锁。
     *   下订单成功，库存锁定成功，接下来的业务调用失败，导致订单回滚。  之前锁定的库存就要自动解锁。
     *
     *  2、订单失败。
     *      锁库存失败
     *
     *  只要库存解锁的消息失败。一定要告诉服务解锁失败
     * @param to
     * @param message
     */
    @RabbitHandler
    public void handleStockLockedRelease(StockLockedTo to , Message message, Channel channel) throws IOException {

        System.out.println("收到解锁库存的消息");
        try {
            //当前消息是否被第二次及以后（重新）派发过来。
//            Boolean redelivered = message.getMessageProperties().getRedelivered();
            wareSkuService.unlockStock(to);
            channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
        } catch (Exception e) {
            channel.basicReject(message.getMessageProperties().getDeliveryTag(),true);
        }
    }


    @RabbitHandler
    public void handleOrderCloseRelease(OrderTo orderTo, Message message, Channel channel) throws IOException {
        System.out.println("订单关闭，准备解锁库存");
        try {
            wareSkuService.unlockStock(orderTo);
            channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
        } catch (Exception e) {
            channel.basicReject(message.getMessageProperties().getDeliveryTag(),true);
        }

    }
}
