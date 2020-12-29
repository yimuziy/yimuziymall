package com.yimuziy.mall.ssoclient.cotroller;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

/**
 * @author ywz
 * @date 2020/12/28 23:56
 * @description
 */
@Controller
public class HelloController {

    @Value("${sso.server.url}")
    String ssoServerUrl;

    /**
     * 无需登录就可访问
     * @return
     */
    @ResponseBody
    @GetMapping("/hello")
    public String hello(){
        return "hello";
    }


    /**
     *  感知这次是在ssoServer登陆成功调回来的
     * @param model
     * @param session
     * @param token 只要区ssoserver登陆成功 就会携带token
     * @return
     */
    @GetMapping("/employees")
    public String employees(Model model, HttpSession session,
                            @RequestParam(value = "token",required = false) String token){
        if(!StringUtils.isEmpty(token)){
            //登录成功  会携带token
            //TODO 1、去ssoserver获取当前token真正对应的用户信息
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> forEntity = restTemplate.getForEntity("http://ssoserver.com:8080/userInfo?token=" + token, String.class);
            String body = forEntity.getBody();
            session.setAttribute("loginUser",body);


        }else{

        }

        Object loginUser = session.getAttribute("loginUser");
        if (loginUser == null) {
            //没登陆,跳转到登陆页面进行登陆

            //跳转过去以后，使用url上的查询参数标识我们自己是哪个页面
            // redirect_url=http://client1.com:8081/employees
            ;
            return "redirect:"+ssoServerUrl+"?redirect_url=http://client1.com:8081/employees";
        } else {
            List<String> emps = new ArrayList<>();
            emps.add("张三");
            emps.add("李四");

            model.addAttribute("emps", emps);
            return "list";


        }


    }
}
