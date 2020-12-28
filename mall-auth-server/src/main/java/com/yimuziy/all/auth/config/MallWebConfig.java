package com.yimuziy.all.auth.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author ywz
 * @date 2020/12/25 22:26
 * @description
 */
@Configuration
public class MallWebConfig implements WebMvcConfigurer {


    /**
     * 视图映射
     * @param registry
     */
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        /**
         *  @GetMapping("/login.html")
         *     public String loginPage(){
         *         return "login";
         *     }
         */
        registry.addViewController("/login.html").setViewName("login");
        registry.addViewController("/reg.html").setViewName("reg");

    }
}
