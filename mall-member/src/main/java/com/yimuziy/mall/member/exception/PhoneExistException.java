package com.yimuziy.mall.member.exception;

/**
 * @author ywz
 * @date 2020/12/26 18:00
 * @description
 */
public class PhoneExistException extends RuntimeException {

    public PhoneExistException() {
        super("手机号已经存在");
    }
}
