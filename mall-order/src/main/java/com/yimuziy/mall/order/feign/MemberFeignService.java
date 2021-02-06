package com.yimuziy.mall.order.feign;

import com.yimuziy.mall.order.vo.MemberAddressVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

/**
 * @author ywz
 * @date 2021/1/10 23:23
 * @description
 */
@FeignClient("mall-member")
public interface MemberFeignService {

    @GetMapping("/member/memberreceiveaddress/{memberId}/addresses")
    public List<MemberAddressVo> getAddress(@PathVariable("memberId") Long memberId) ;

}
