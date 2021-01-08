package com.yimuziy.mall.cart.vo;

import lombok.Data;
import lombok.ToString;

/**
 * @author ywz
 * @date 2020/12/29 21:31
 * @description
 */
@Data
@ToString
public class UserInfoTo {

    private Long userId;
    private String userKey;

    //存储是否为临时用户
    private boolean tempUser=false;
}
