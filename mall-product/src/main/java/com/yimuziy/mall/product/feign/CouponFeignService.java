package com.yimuziy.mall.product.feign;

import com.yimuziy.common.to.SkuReductionTo;
import com.yimuziy.common.to.SpuBoundTo;
import com.yimuziy.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @author ywz
 * @date 2020/12/4 18:46
 * @description
 */
@FeignClient("mall-coupon")
public interface CouponFeignService {

    /**
     * 1、CouponFeignService.saveSpuBounds(SpuBoundTo);
     * 1）、@RequestBody 将这个对象转为json。
     * 2）、找到mall-coupon服务，给/coupon/spubounds/save 发送请求。
     * 将上一步转的json放在请求体位置，发送请求。
     * 3）、对方服务收到请求。请求体里有json数据。
     * (@RequestBody SpuBoundTo spuBoundTo);将请求体的json转换为SpuBoundEntity
     * <p>
     * <p>
     * 只要json数据模型是兼容的。双方服务无需使用同一个to
     *
     * @return
     * @paraspuBoundTo
     */
    @PostMapping("/coupon/spubounds/save")
    R saveSpuBounds(@RequestBody SpuBoundTo spuBoundTo);


    @PostMapping("/coupon/skufullreduction/saveinfo")
    R saveSkuReduction(@RequestBody SkuReductionTo skuReductionTo);
}
