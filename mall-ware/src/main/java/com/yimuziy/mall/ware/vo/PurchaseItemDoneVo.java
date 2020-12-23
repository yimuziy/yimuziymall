package com.yimuziy.mall.ware.vo;

import lombok.Data;

/**
 * @author ywz
 * @date 2020/12/5 20:47
 * @description
 */
@Data
public class PurchaseItemDoneVo {
    //temId:1,status:4,reason:""
    private Long ItemId ;
    private Integer status;
    private String reason;
}
