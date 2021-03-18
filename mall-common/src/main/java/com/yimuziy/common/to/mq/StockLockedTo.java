package com.yimuziy.common.to.mq;

import lombok.Data;

import java.util.List;

/**
 * @author: ywz
 * @createDate: 2021/3/3
 * @description:
 */
@Data
public class StockLockedTo {

    private Long id; // 库存工作单的id

    private StockDetailTo detail; //工作详情的所有id

}
