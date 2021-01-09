package com.yimuziy.mall.order.service.impl;

import com.alibaba.fastjson.JSON;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.impl.AMQImpl;
import com.yimuziy.mall.order.entity.OrderEntity;
import com.yimuziy.mall.order.entity.OrderReturnReasonEntity;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.util.Map;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yimuziy.common.utils.PageUtils;
import com.yimuziy.common.utils.Query;

import com.yimuziy.mall.order.dao.OrderItemDao;
import com.yimuziy.mall.order.entity.OrderItemEntity;
import com.yimuziy.mall.order.service.OrderItemService;


@RabbitListener(queues = {"hello-java-queue"})
@Service("orderItemService")
public class OrderItemServiceImpl extends ServiceImpl<OrderItemDao, OrderItemEntity> implements OrderItemService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<OrderItemEntity> page = this.page(
                new Query<OrderItemEntity>().getPage(params),
                new QueryWrapper<OrderItemEntity>()
        );

        return new PageUtils(page);
    }

    /**
     * queues：声明需要监听的所有队列
     *
     *  org.springframework.amqp.core.Message
     *
     * 参数可以写一下类型
     * 1、Message message：原生消息详细信息。头+体
     * 2、T<发送的消息类型> OrderReturnReasonEntity content
     * 3、Channel channel：当前传输数据的通道
     *
     * Queue：可以很多人都来监听。只要收到消息，队列删除消息，而且只能有一个收到次消息
     * 场景：
     *      1）、订单服务启动多个：同一个消息，只能有一个客户端收到
     *      2）、只有一个消息完全处理完，方法运行结束，我们就可以接收到下一个消息
     */
//    @RabbitListener(queues = {"hello-java-queue"})
    @RabbitHandler
    public void recieveMessage(Message message,
                               OrderReturnReasonEntity content,
                               Channel channel){
        //{"id":1,"name":"哈哈哈","sort":null,"status":null,"createTime":1610187197125}' MessageProperties [headers={__TypeId__=com.yimuziy.mall.order.entity.OrderReturnReasonEntity}
        System.out.println("接收到的消息....."+message+"===>内容是:"+content);
        byte[] body = message.getBody();
        //消息头属性信息
        MessageProperties messageProperties = message.getMessageProperties();
//        try {
//            Thread.sleep(3000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
        System.out.println("消息处理完成==>"+ content.getName());
        //channel内按顺序自增的
        long deliveryTag = message.getMessageProperties().getDeliveryTag();
        System.out.println("deliverTag==>"+deliveryTag);

        //签收货物,非批量模式
        try{
            if(deliveryTag%2==0) {
                //收获
                channel.basicAck(deliveryTag, false);
                System.out.println("签收了货物...." + deliveryTag);
            }else{
                //退货  requeue=false 丢弃 requeue=true 发回服务器，服务器重新入队
                //long deliveryTag, boolean multiple, boolean requeue
                channel.basicNack(deliveryTag,false,true);
                //long deliveryTag, boolean requeue
//                channel.basicReject();
                System.out.println("没有签收了货物...." + deliveryTag);
            }
        }catch (Exception e){
            //网络中断
        }

    }

    @RabbitHandler
    public void recieveMessage02(OrderEntity content){
        //{"id":1,"name":"哈哈哈","sort":null,"status":null,"createTime":1610187197125}' MessageProperties [headers={__TypeId__=com.yimuziy.mall.order.entity.OrderReturnReasonEntity}
        System.out.println("接收到的消息.....内容是:"+content);


    }
}