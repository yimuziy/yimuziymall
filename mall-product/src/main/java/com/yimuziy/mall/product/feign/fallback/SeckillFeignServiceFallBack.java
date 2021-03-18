package com.yimuziy.mall.product.feign.fallback;

import com.yimuziy.common.exception.BizCodeEnum;
import com.yimuziy.common.utils.R;
import com.yimuziy.mall.product.feign.SeckillFeignService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

/**
 * @author: ywz
 * @createDate: 2021/3/10
 * @description:
 */
@Slf4j
@Component
public class SeckillFeignServiceFallBack implements SeckillFeignService {

    @Override
    public R getSkuSeckillInfo(Long skuId) {
        log.info("熔断方法调用......");
        return R.error(BizCodeEnum.TO_MANY_REQUEST.getCode(), BizCodeEnum.TO_MANY_REQUEST.getMsg());
    }
}
