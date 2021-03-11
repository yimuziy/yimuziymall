package com.yimuziy.mall.cart.config;

import com.alibaba.csp.sentinel.adapter.servlet.callback.WebCallbackManager;
import com.alibaba.fastjson.JSON;
import com.yimuziy.common.exception.BizCodeEnum;
import com.yimuziy.common.utils.R;
import org.springframework.context.annotation.Configuration;

/**
 * @author: ywz
 * @createDate: 2021/3/10
 * @description:
 */
@Configuration
public class CartSentinelConfig {

    public CartSentinelConfig(){
        WebCallbackManager.setUrlBlockHandler((request, response, ex) -> {
            R error = R.error(BizCodeEnum.TO_MANY_REQUEST.getCode(), BizCodeEnum.TO_MANY_REQUEST.getMsg());
            response.setCharacterEncoding("UTF-8");
            response.setContentType("json");
            response.getWriter().write(JSON.toJSONString(error));
        });


    }

}
