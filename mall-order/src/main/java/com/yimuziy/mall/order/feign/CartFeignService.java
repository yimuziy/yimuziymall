package com.yimuziy.mall.order.feign;

import com.yimuziy.mall.order.vo.OrderItemVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * @author ywz
 * @date 2021/1/10 23:46
 * @description
 */
@FeignClient("mall-cart")
public interface CartFeignService {

    @GetMapping("/currentUserCartItems")
    @ResponseBody
    public List<OrderItemVo> getCurrentUserCartItems();
}
