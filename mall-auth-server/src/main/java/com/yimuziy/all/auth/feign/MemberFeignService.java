package com.yimuziy.all.auth.feign;

import com.yimuziy.all.auth.vo.SocialUser;
import com.yimuziy.all.auth.vo.UserLoginVo;
import com.yimuziy.all.auth.vo.UserRegistVo;
import com.yimuziy.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @author ywz
 * @date 2020/12/27 13:58
 * @description
 */
@FeignClient("mall-member")
public interface MemberFeignService {
    @PostMapping("/member/member/regist")
    public R regist(@RequestBody UserRegistVo vo);

    @PostMapping("/member/member/login")
    public R login(@RequestBody UserLoginVo vo);

    @PostMapping("/member/member/oauth2/login")
    public R oauthlogin(@RequestBody SocialUser socialUser) throws Exception;
}
