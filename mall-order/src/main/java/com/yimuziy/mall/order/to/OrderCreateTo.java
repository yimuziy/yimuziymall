package com.yimuziy.mall.order.to;

import com.yimuziy.mall.order.entity.OrderEntity;
import com.yimuziy.mall.order.entity.OrderItemEntity;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author: ywz
 * @createDate: 2021/2/10
 * @description:
 */
@Data
public class OrderCreateTo {


    private OrderEntity order ;

    private List<OrderItemEntity> orderItems;

    private BigDecimal payPrice; //订单计算的应付价格

    private BigDecimal fare; //运费


}
