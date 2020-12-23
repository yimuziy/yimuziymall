package com.yimuziy.mall.search.feign;

import com.yimuziy.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @author ywz
 * @date 2020/12/20 16:24
 * @description
 */
@FeignClient("mall-product")
public interface ProductFeignService {


    @GetMapping("/product/attr/info/{attrId}")
    public R attrsInfo(@PathVariable("attrId") Long attrId);

    @GetMapping("/product/brand/infos")
    public R brandsInfo(@RequestParam("brandIds") List<Long> brandIds);


}
