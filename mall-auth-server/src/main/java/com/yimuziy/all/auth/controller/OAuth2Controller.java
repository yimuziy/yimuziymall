package com.yimuziy.all.auth.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.yimuziy.all.auth.feign.MemberFeignService;
import com.yimuziy.all.auth.vo.SocialUser;
import com.yimuziy.common.utils.HttpUtils;
import com.yimuziy.common.utils.R;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.yimuziy.common.vo.MemberRespVo;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

/**
 * @author ywz
 * @date 2020/12/27 17:56
 * @description
 * 处理社交登陆请求
 */
@Controller
@Slf4j
public class OAuth2Controller {

    @Autowired
    MemberFeignService memberFeignService;

    /**
     * 社交登陆成功回调
     * @param code
     * @return
     * @throws Exception
     */
    @GetMapping("/oauth2.0/weibo/success")
    public String weibo(@RequestParam("code")String code, HttpSession session, HttpServletResponse servletResponse) throws Exception {
        Map<String, String> header = new HashMap<String,String>();
        Map<String, String> query = new HashMap<String,String>();

        Map<String, String> map = new HashMap<String,String>();
        map.put("client_id","1840293055");
        map.put("client_secret","7f498bdcc9461ac371ca402118c71f2f");
        map.put("grant_type","authorization_code");
        map.put("redirect_uri","http://auth.yimuziymall.com/oauth2.0/weibo/success");
        map.put("code",code);

        //1、根据code换取accessToken
//        https://api.weibo.com/oauth2/access_token
        HttpResponse response = HttpUtils.doPost("https://api.weibo.com", "/oauth2/access_token", "POST", header, query, map);
        log.info("根据code换取accessToken  处理结果:"+ response.getStatusLine().getStatusCode() );
        //2、处理
        if(response.getStatusLine().getStatusCode()==200){
            //获取到了accessToken
            String json = EntityUtils.toString(response.getEntity());
            SocialUser socialUser = JSON.parseObject(json, SocialUser.class);


            //知道当前是哪个社交用户
            //1）、当前用户如果是第一次进网站，自动注册进来（为当前社交用户生成一个会员账号信息，以后这个社交帐号就对应指定的会员）
            //登陆或者注册这个社交用户
            R oauthlogin = memberFeignService.oauthlogin(socialUser);
            if(oauthlogin.getCode() == 0){
                MemberRespVo data = oauthlogin.getData(new TypeReference<MemberRespVo>() {
                });
                log.info("登陆成功: 用户:{}",data.toString());
                //1、第一次使用session；命令浏览器保存卡号。JSESSIONID这个cookie
                //以后浏览器访问哪个网站就会带上这个王章的cookie；
                //子域之间； yimuziymall.com  auth.yimuziymall.com order.yimuziymall.com
                //发卡的时候（指定域名为父域名），即使是子域系统发的卡，也能让父域直接使用
                //TODO 1、默认发的令牌。 session=dsajdkjl。 作用域：当前域；（解决子域session共享问题）
                //TODO 2、使用JSON的序列化方式来序列化对象数据到redis中
                session.setAttribute("loginUser",data);
//                servletResponse.addCookie(new Cookie("JSESSION","dataa").setDomain(""));
                //2、登陆成功就跳回首页
                return "redirect:http://yimuziymall.com";
            }else{
                return "redirect:http://auth.yimuziymall.com/login.html";
            }

        }else{
            return "redirect:http://auth.yimuziymall.com/login.html";
        }
    }
}
