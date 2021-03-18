package com.yimuziy.mall.order.feign;

import com.yimuziy.common.utils.R;
import com.yimuziy.mall.order.vo.WareSkuLockVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

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


    @GetMapping("/ware/wareinfo/fare")
     R getFare(@RequestParam("addrId") Long addrId);

    @PostMapping("/ware/waresku/lock/order")
     R orderLockStock(@RequestBody WareSkuLockVo vo);
}
