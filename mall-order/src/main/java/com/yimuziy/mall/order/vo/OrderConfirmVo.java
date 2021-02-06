package com.yimuziy.mall.order.vo;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * @author ywz
 * @date 2021/1/10 23:08
 * @description
 *  订单确认页需要用的数据
 */

public class OrderConfirmVo {

    @Getter @Setter
    // 收货地址，ums_member_receive_address 表
    List<MemberAddressVo> address;

    @Getter @Setter
    //所有选中的购物项
    List<OrderItemVo> items;

    //发票记录。。。。

    @Getter @Setter
    //优惠券信息。。。 会员的积分信息
    private Integer integration;


    /**
     * 库存信息
     */
    @Getter @Setter
    Map<Long,Boolean> stocks;

    @Getter @Setter
    //防重令牌
    String orderToken;



    public Integer getCount(){
        Integer i = 0;
        if(items!=null & items.size()>0){
            for (OrderItemVo item : items) {
               i+= item.getCount();
            }
        }
        return i;
    }

//    BigDecimal total; //订单总额

    public BigDecimal getTotal(){
        BigDecimal sum = new BigDecimal("0");
        if(items!=null & items.size()>0){
            for (OrderItemVo item : items) {
                BigDecimal multiply = item.getPrice().multiply(new BigDecimal(item.getCount().toString()));
                sum =  sum.add(multiply);
            }
        }
        return sum;
    }

//    BigDecimal payPrice; //应付价格

    public BigDecimal getPayPrice() {
        return getTotal();
    }
}
