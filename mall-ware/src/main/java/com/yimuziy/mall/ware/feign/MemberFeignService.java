package com.yimuziy.mall.ware.feign;

import com.yimuziy.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author ywz
 * @date 2021/2/3 11:51
 * @description
 */
@FeignClient("mall-member")
public interface MemberFeignService {

    @RequestMapping("/member/memberreceiveaddress/info/{id}")
     R addrInfo(@PathVariable("id") Long id);
}
