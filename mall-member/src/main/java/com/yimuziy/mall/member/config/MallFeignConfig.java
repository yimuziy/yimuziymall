package com.yimuziy.mall.member.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * @author ywz
 * @date 2021/1/19 21:21
 * @description
 */
@Configuration
public class MallFeignConfig {

    @Bean("requestInterceptor")
    public RequestInterceptor requestInterceptor(){
        return new RequestInterceptor() {
            @Override
            public void apply(RequestTemplate template) {
                //1、RequestContextHolder 拿到刚进来的这个请求
                ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
                if(attributes != null){
                    System.out.println("RequestInterceptor线程...."+Thread.currentThread().getId());
                    HttpServletRequest request = attributes.getRequest(); //老请求
                    //同步请求头数据，主要是cookie
                    String cookie = request.getHeader("Cookie");
                    //给新请求同步了老请求的cookie
                    template.header("Cookie", cookie);
                }
            }
        };
    }

}
