package com.yimuziy.mall.seckill.config;

import com.alibaba.csp.sentinel.adapter.servlet.callback.WebCallbackManager;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.fastjson.JSON;
import com.yimuziy.common.exception.BizCodeEnum;
import com.yimuziy.common.utils.R;
import org.springframework.context.annotation.Configuration;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author: ywz
 * @createDate: 2021/3/10
 * @description:
 */
@Configuration
public class SeckillSentinelConfig {

    public SeckillSentinelConfig(){
        WebCallbackManager.setUrlBlockHandler((request, response, ex) -> {
            R error = R.error(BizCodeEnum.TO_MANY_REQUEST.getCode(), BizCodeEnum.TO_MANY_REQUEST.getMsg());
            response.setCharacterEncoding("UTF-8");
            response.setContentType("json");
            response.getWriter().write(JSON.toJSONString(error));
        });


    }

}
