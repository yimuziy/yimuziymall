package com.yimuziy.mall.order.interceptor;

import com.yimuziy.common.constant.AuthServerConstant;
import com.yimuziy.common.vo.MemberRespVo;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author ywz
 * @date 2021/1/10 22:51
 * @description
 */
@Component
public class LoginUserInterceptor implements HandlerInterceptor {

    public static ThreadLocal<MemberRespVo> loginUser = new ThreadLocal<>();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        // /order/order/status/{orderSn}
        String uri = request.getRequestURI();
        AntPathMatcher antPathMatcher = new AntPathMatcher();
        boolean match = antPathMatcher.match("/order/order/status/**", uri);
        boolean notifyUri = antPathMatcher.match("/payed/notify", uri);

        if(match || notifyUri){
            return true;
        }

        MemberRespVo attribute = (MemberRespVo) request.getSession().getAttribute(AuthServerConstant.LOGIN_USER);
        if (attribute != null) {
            loginUser.set(attribute);
            return true;
        }else{
            //没登陆就去登录
            request.getSession().setAttribute("msg","请先进行登录");
            response.sendRedirect("http://auth.yimuziymall.com/login.html");
            return false;
        }

    }
}
