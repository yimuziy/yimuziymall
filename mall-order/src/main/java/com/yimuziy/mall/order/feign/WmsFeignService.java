package com.yimuziy.mall.order.feign;

import com.yimuziy.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * @author ywz
 * @date 2021/2/1 18:02
 * @description
 */
@FeignClient("mall-ware")
public interface WmsFeignService {

    /**
     * 查询sku是否有库存
     */
    @PostMapping("/ware/waresku/hasstock")
    R getSkuHasStock(@RequestBody List<Long> skuIds) ;
}
