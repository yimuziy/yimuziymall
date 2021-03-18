package com.yimuziy.mall.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yimuziy.common.to.mq.SeckillOrderTo;
import com.yimuziy.common.utils.PageUtils;
import com.yimuziy.mall.order.entity.OrderEntity;
import com.yimuziy.mall.order.vo.*;

import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * 订单
 *
 * @author yimuziy
 * @email yimuziy@gmail.com
 * @date 2020-11-26 17:37:58
 */
public interface OrderService extends IService<OrderEntity> {

    PageUtils queryPage(Map<String, Object> params);

    /**
     * 订单确认页返回需要用的数据
     * @return
     */
    OrderConfirmVo confirmOrder() throws ExecutionException, InterruptedException;

    /**
     * 下单操作
     * @param vo
     * @return
     */
    SubmitOrderResponseVo submitOrder(OrderSubmitVo vo);

    /**
     * 根据订单号获取订单状态
     * @param orderSn
     * @return
     */
    OrderEntity getOrderByOrderSn(String orderSn);

    /**
     * 关闭订单
     * @param entity
     */
    void closeOrder(OrderEntity entity);

    /**
     * 获取当前订单的支付信息
     * @param orderSn
     * @return
     */
    PayVo getOrderPay(String orderSn);

    /**
     *  分页查询当前用户的所有订单
     * @param params
     * @return
     */
    PageUtils queryPageWithItem(Map<String, Object> params);

    /**
     * 支付成功的回调方法,用于修改订单状态
     * @param vo
     * @return
     */
    String handlePayresult(PayAsyncVo vo);

    /**
     * 秒杀后创建订单信息
     * @param seckillOrder
     */
    void createSeckillOrder(SeckillOrderTo seckillOrder);
}

