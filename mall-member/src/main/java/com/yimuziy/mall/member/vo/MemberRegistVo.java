package com.yimuziy.mall.member.vo;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

/**
 * @author ywz
 * @date 2020/12/26 17:48
 * @description
 */
@Data
public class MemberRegistVo {


    private String userName;

    private String password;

    private String phone;
}
