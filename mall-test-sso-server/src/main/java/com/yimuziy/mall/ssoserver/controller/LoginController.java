package com.yimuziy.mall.ssoserver.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.UUID;

/**
 * @author ywz
 * @date 2020/12/29 0:08
 * @description
 */
@Controller
public class LoginController {
    @Autowired
    StringRedisTemplate redisTemplate;


    @ResponseBody
    @GetMapping("userInfo")
    public String userInfo(@RequestParam("token") String token){
        String s = redisTemplate.opsForValue().get(token);
        return s;
    }

    @PostMapping("/doLogin")
    public String doLogin(String username, String password, String url,
                          HttpServletResponse response){
        if(!StringUtils.isEmpty(username) && !StringUtils.isEmpty(password)){
            //登陆成功,条回到之前到的页面;
            //把登录成功的用户保存起来
            String uuid = UUID.randomUUID().toString().replace("_", "");
            redisTemplate.opsForValue().set(uuid,username);
            Cookie sso_token = new Cookie("sso_token",uuid);

            response.addCookie(sso_token);
            return "redirect:"+url+"?token="+uuid;
        }
        //登陆失败，展示当前页
        return "login";
    }

    @GetMapping("/login.html")
    public String loginPage(@RequestParam(value="redirect_url",required = false)String url, Model model,
                            @CookieValue(value = "sso_token",required = false)String sso_token){
        if(!StringUtils.isEmpty(sso_token)){
            //说明之前有人登录过，浏览器留下了痕迹
            return "redirect:"+url+"?token="+sso_token;

        }
        model.addAttribute("url", url);
        return "login";
    }

}
