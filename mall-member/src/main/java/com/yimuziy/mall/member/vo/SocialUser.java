package com.yimuziy.mall.member.vo;

import lombok.Data;

/**
 * @author ywz
 * @date 2020/12/27 18:47
 * @description
 */
@Data
public class SocialUser {
    private String access_token;
    private String remind_in;
    private long expires_in;
    private String uid;
    private String isRealName;
}
