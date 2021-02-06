package com.yimuziy.all.auth.feign;

import com.yimuziy.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author ywz
 * @date 2020/12/26 15:10
 * @description
 */
@FeignClient("mall-third-party")
public interface ThiredPartFeignService {

    @PostMapping("/sms/sendCode")
    public R sendCode(@RequestParam("phone") String phone, @RequestParam("code") String code);

    @PostMapping("/sendsms")
    public R sendSms(@RequestParam("phone") String phone, @RequestParam("code") String code);
}
