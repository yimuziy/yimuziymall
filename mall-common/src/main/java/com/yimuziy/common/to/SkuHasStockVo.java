package com.yimuziy.common.to;

import lombok.Data;

/**
 * @author ywz
 * @date 2020/12/11 15:11
 * @description
 */
@Data
public class SkuHasStockVo {
    private Long skuId;
    private Boolean  hasStock;
}
