package com.yimuziy.mall.order.vo;

import com.yimuziy.mall.order.entity.OrderEntity;
import lombok.Data;

/**
 * @author: ywz
 * @createDate: 2021/2/9
 * @description:
 */
@Data
public class SubmitOrderResponseVo {

    private OrderEntity order;
    private Integer code; //0 成功   其他： 错误状态码

}
