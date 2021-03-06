package com.yimuziy.mall.ware.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author ywz
 * @date 2021/1/10 23:17
 * @description
 */
@Data
public class OrderItemVo {
    private Long skuId;
    private String title;
    private String image;
    private List<String> skuAttr;
    private BigDecimal price;
    private Integer count;
    private BigDecimal totalPrice;


    private BigDecimal weight;
}
