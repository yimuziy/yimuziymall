package com.yimuziy.mall.member.exception;

/**
 * @author ywz
 * @date 2020/12/26 18:00
 * @description
 */
public class UsernameExistException extends RuntimeException{

    public UsernameExistException() {
        super("用户名存在");
    }
}
