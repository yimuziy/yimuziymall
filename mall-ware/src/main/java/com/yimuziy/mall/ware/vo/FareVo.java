package com.yimuziy.mall.ware.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author ywz
 * @date 2021/2/3 13:14
 * @description
 */
@Data
public class FareVo {
    private MemberAddressVo address;

    private BigDecimal fare;

}
