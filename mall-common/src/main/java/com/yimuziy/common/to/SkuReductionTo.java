package com.yimuziy.common.to;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author ywz
 * @date 2020/12/4 19:12
 * @description
 */
@Data
public class SkuReductionTo {

    private Long skuId;
    private int fullCount;
    private BigDecimal discount;
    private int countStatus;
    private BigDecimal fullPrice;
    private BigDecimal reducePrice;
    private int priceStatus;
    private List<MemberPrice> memberPrice;
}
