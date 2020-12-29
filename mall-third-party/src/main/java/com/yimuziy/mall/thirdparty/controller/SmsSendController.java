package com.yimuziy.mall.thirdparty.controller;

import com.yimuziy.common.utils.R;
import com.yimuziy.mall.thirdparty.component.SmsComponent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author ywz
 * @date 2020/12/26 14:53
 * @description
 */

@RestController
@RequestMapping("/sms")
public class SmsSendController {

    @Autowired
    SmsComponent smsComponent;

    /**
     * 提供给别的服务进行调用
     *
     * @param phone
     * @param code
     * @return
     */
    @PostMapping("/sendCode")
    public R sendCode(@RequestParam("phone") String phone, @RequestParam("code") String code) {
        smsComponent.sendCode(phone, code);
        return R.ok();
    }

    /**
     * 提供给别的服务进行调用
     *
     * @param phone
     * @return
     */
    @PostMapping("/sendsms")
    public R sendSms(@RequestParam("phone") String phone, @RequestParam("code") String code) {
        smsComponent.sendSms(phone, code);
        return R.ok();
    }
}
