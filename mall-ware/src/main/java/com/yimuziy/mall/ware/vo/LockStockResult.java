package com.yimuziy.mall.ware.vo;

import lombok.Data;

/**
 * @author: ywz
 * @createDate: 2021/2/24
 * @description: 判断有没有锁定库存
 */
@Data
public class LockStockResult {

    private Long skuId;
    private Integer num; //要锁定的数量
    private Boolean locked; //有没有锁定成功


}
