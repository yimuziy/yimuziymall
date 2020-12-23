package com.yimuziy.common.to;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author ywz
 * @date 2020/12/4 18:54
 * @description
 */
@Data
public class SpuBoundTo {

    private Long spuId;
    private BigDecimal buyBounds;
    private BigDecimal growBounds;
}
