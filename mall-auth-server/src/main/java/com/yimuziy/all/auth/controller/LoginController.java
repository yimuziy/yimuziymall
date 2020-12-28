package com.yimuziy.all.auth.controller;

import com.alibaba.fastjson.TypeReference;
import com.yimuziy.all.auth.feign.MemberFeignService;
import com.yimuziy.all.auth.feign.ThiredPartFeignService;
import com.yimuziy.all.auth.vo.UserLoginVo;
import com.yimuziy.all.auth.vo.UserRegistVo;
import com.yimuziy.common.constant.AuthServerConstant;
import com.yimuziy.common.exception.BizCodeEnum;
import com.yimuziy.common.utils.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author ywz
 * @date 2020/12/25 21:36
 * @description
 */
@Controller
public class LoginController {

    /**
     * 发送一个请求直接跳转到一个页面。
     * SpringMVC viewController; 将请求和页面映射过来
     *
     * @return
     */

    @Autowired
    ThiredPartFeignService thiredPartFeignService;

    @Autowired
    StringRedisTemplate redisTemplate;

    @Autowired
    MemberFeignService memberFeignService;


    /**
     * 发送验证码
     * @param phone
     * @return
     */
    @PostMapping("/sms/sendcode")
    @ResponseBody
    public R sendCode(@RequestParam("phone") String phone) {
        //TODO 1、接口防刷
        String redisCode = redisTemplate.opsForValue().get(AuthServerConstant.SMS_CODE_CACHE_PREFIX + phone);
        if(redisCode != null) {
            Long l = Long.parseLong(redisCode.split("_")[1]);
            if (System.currentTimeMillis() - l < 60000) {
                //60秒内不能再发
                return R.error(BizCodeEnum.SMS_CODE_EXCEPTION.getCode(), BizCodeEnum.SMS_CODE_EXCEPTION.getMsg());
            }
        }


        //2、验证码的再次校验。redis. 存 key-phone,value-code  sms:code:172987329323 -> 45678
        String code = UUID.randomUUID().toString().substring(0, 5);
        String subString = code +"_"+System.currentTimeMillis();
        //redis缓存验证码，防止同一个phone在60秒内再次发送验证码
        redisTemplate.opsForValue().set(AuthServerConstant.SMS_CODE_CACHE_PREFIX+phone,subString,10, TimeUnit.MINUTES);


        thiredPartFeignService.sendCode(phone, code);

        return R.ok();
    }




    /**
     * //TODO 重定向携带数据，利用session原理。将数据放在session中。
     *      只要跳到下一个页面取出这个数据以后。session里面的数据就会删掉
     *
     * //TODO 1、分布式下的session问题
     * RedirectAttributes redirectAttributes ： 模拟重定向携带数据
     * @param vo
     * @param result
     * @param redirectAttributes
     * @return
     */
    @PostMapping("/regist")
    public String regist(@Valid UserRegistVo vo, BindingResult result, RedirectAttributes redirectAttributes){
        if(result.hasErrors()){
            /**
             * map(fieldError -> {
             *                 errors.put(fieldError.getField(),fieldError.getDefaultMessage());
             *                 return fieldError;
             *             })
             */
            Map<String,String> errors=  result.getFieldErrors().stream().collect(
                    Collectors.toMap(FieldError::getField,FieldError::getDefaultMessage));
            //Request method 'POST' not supported
            //用户注册 -》 /regist[post]  -->转发 /reg.html(路径映射都是get方式访问的)

            redirectAttributes.addFlashAttribute("errors",errors);
            //校验出错，转发到注册页
            return "redirect:http://auth.yimuziymall.com/reg.html";
        }
        //真正注册。调用远程服务进行注册
        //1、校验验证码
        String code = vo.getCode();


        String s = redisTemplate.opsForValue().get(AuthServerConstant.SMS_CODE_CACHE_PREFIX + vo.getPhone());
        if(!StringUtils.isEmpty(s)) {
            if (code.equals(s.split("_")[0])){
                //删除验证码; 令牌机制
                redisTemplate.delete(AuthServerConstant.SMS_CODE_CACHE_PREFIX + vo.getPhone());
                //验证码通过  //真正注册。调用远程服务进行注册
                R r = memberFeignService.regist(vo);
                if(r.getCode() == 0){
                    //成功

                    return "redirect:http://auth.yimuziymall.com/login.html";
                }else{
                    Map<String,String> errors = new HashMap<>();
                    errors.put("msg",r.getData("msg",new TypeReference<String>(){}));
                    redirectAttributes.addFlashAttribute("error",errors);
                    return "redirect:http://auth.yimuziymall.com/reg.html";
                }

            }else{
                Map<String,String> errors =  new HashMap<>();
                errors.put("code","验证码错误");
                redirectAttributes.addFlashAttribute("errors",errors);
                return "redirect:http://auth.yimuziymall.com/reg.html";
            }
        }else{
            //说明redis存入的code过期
            Map<String,String> errors =  new HashMap<>();
            errors.put("code","验证码错误");
            redirectAttributes.addFlashAttribute("errors",errors);
            //校验出错，转发到注册页
            return "redirect:http://auth.yimuziymall.com/reg.html";
        }

    }




    @PostMapping("/login")
    public String login(UserLoginVo vo,RedirectAttributes redirectAttributes){


        //远程登录
        R login = memberFeignService.login(vo);
        if(login.getCode() == 0){
            //成功
            return "redirect:http://yimuziymall.com";
        }else {
            Map<String, String> errors = new HashMap<>();
            errors.put("msg",login.getData("msg",new TypeReference<String>(){}));
            redirectAttributes.addFlashAttribute("errors",errors);
            return "redirect:http://auth.yimuziymall.com/login.html";
        }
    }

}
