package com.yimuziy.mall.product.web;

import com.yimuziy.mall.product.service.SkuInfoService;
import com.yimuziy.mall.product.vo.SkuItemVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @author ywz
 * @date 2020/12/23 14:17
 * @description
 */
@Controller
public class ItemController {

    @Autowired
    SkuInfoService skuInfoService;

    /**
     * 展示当前sku的详情
     * @param skuId
     * @return
     */
    @GetMapping("/{skuId}.html")
    public String skuItem(@PathVariable Long skuId){

        System.out.println("准备查询"+ skuId+"详情");
        SkuItemVo vo = skuInfoService.item(skuId);

        return "item";
    }
}
