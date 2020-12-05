package com.yimuziy.mall.product.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author ywz
 * @date 2020/12/4 17:11
 * @description
 */
@Data
public class MemberPrice {
    private Long id;
    private String name;
    private BigDecimal price;
}

