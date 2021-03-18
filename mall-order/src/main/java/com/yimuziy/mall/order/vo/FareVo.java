package com.yimuziy.mall.order.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author: ywz
 * @createDate: 2021/2/10
 * @description:
 */
@Data
public class FareVo {
    private MemberAddressVo address;

    private BigDecimal fare;
}
