package com.yimuziy.mall.ware.vo;


import lombok.Data;

import java.util.List;

/**
 * @author: ywz
 * @createDate: 2021/2/24
 * @description:
 */
@Data
public class WareSkuLockVo {

    private String orderSn; //订单号

    private List<OrderItemVo> locks;//需要锁住的所有库存信息

}
