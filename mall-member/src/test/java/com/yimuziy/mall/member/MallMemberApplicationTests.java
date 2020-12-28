package com.yimuziy.mall.member;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.codec.digest.Md5Crypt;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

//@SpringBootTest
class MallMemberApplicationTests {

    @Test
    void contextLoads() {


        //e10adc3949ba59abbe56e057f20f883e
        //抗修改性： 彩虹表。。 123456 -> xxxxx
        String s = DigestUtils.md5Hex("123456");

        //MD5不能直接进行密码的加密存储
//        "123456"+System.currentTimeMillis();


        //盐值加密； 随机值 加盐： $1$+8位字符
        //e10adc3949ba59abbe56e057f20f883e
        //验证：123456进行盐值加密
//        String s1 = Md5Crypt.md5Crypt("123456".getBytes(),"$1$qqqqqqq");
//        System.out.println(s1);

        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        //$2a$10$Kcu3c5cvCxHTASuBqb92au9DMxvZjMAMMl9vzz5dyBUN.0IJx0sDe
        //$2a$10$Kcu3c5cvCxHTASuBqb92au9DMxvZjMAMMl9vzz5dyBUN.0IJx0sDe
        //$2a$10$hxSMYXRwzFJveaLnOW0rDeFJ2cNj6pDWrefSqmAjZz6QS0a34eVX6
        String encode = passwordEncoder.encode("123456");
        System.out.println(encode);

        boolean matches = passwordEncoder.matches("123456", "$2a$10$Kcu3c5cvCxHTASuBqb92au9DMxvZjMAMMl9vzz5dyBUN.0IJx0sDe");
        System.out.println(encode + "=>" + matches);

        System.out.println(s);

    }

}
