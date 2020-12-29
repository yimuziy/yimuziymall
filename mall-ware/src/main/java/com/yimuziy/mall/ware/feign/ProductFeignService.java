package com.yimuziy.mall.ware.feign;

import com.yimuziy.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author ywz
 * @date 2020/12/5 23:16
 * @description
 */
@FeignClient(value = "mall-product")
public interface ProductFeignService {


    /**
     * /product/skuinfo/info/{skuId}
     * /api/product/skuinfo/info/{skuId}
     * <p>
     * <p>
     * 1)、让所有请求过网关；
     * 1、@FeignClient(value = "mall-gateway"):给mall-gateway所在的机器发请求
     * 2、配置请求路径/api/product/skuinfo/info/{skuId}
     * 2)、直接让后台指定服务处理
     * 1、@FeignClient(value = "mall-product")
     * 2、/product/skuinfo/info/{skuId}
     *
     * @param skuId
     * @return
     */
    @RequestMapping("/product/skuinfo/info/{skuId}")
    public R info(@PathVariable("skuId") Long skuId);

}
