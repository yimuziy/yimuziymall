package com.yimuziy.mall.order.dao;

import com.yimuziy.mall.order.entity.OrderEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 订单
 *
 * @author yimuziy
 * @email yimuziy@gmail.com
 * @date 2020-11-26 17:37:58
 */
@Mapper
public interface OrderDao extends BaseMapper<OrderEntity> {

    /**
     * 根据订单号修改订单状态
     * @param outTradeNo
     * @param code
     */
    void updateOrderStatus(@Param("outTradeNo") String outTradeNo, @Param("code") Integer code);
}
