package com.yimuziy.mall.member.controller;

import java.util.Arrays;
import java.util.Map;

//import org.apache.shiro.authz.annotation.RequiresPermissions;
import com.yimuziy.common.exception.BizCodeEnum;
import com.yimuziy.mall.member.exception.PhoneExistException;
import com.yimuziy.mall.member.exception.UsernameExistException;
import com.yimuziy.mall.member.feign.CouponFeighService;
import com.yimuziy.mall.member.vo.MemberLoginVo;
import com.yimuziy.mall.member.vo.MemberRegistVo;
import com.yimuziy.mall.member.vo.SocialUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.yimuziy.mall.member.entity.MemberEntity;
import com.yimuziy.mall.member.service.MemberService;
import com.yimuziy.common.utils.PageUtils;
import com.yimuziy.common.utils.R;



/**
 * 会员
 *
 * @author yimuziy
 * @email yimuziy@gmail.com
 * @date 2020-11-26 17:29:00
 */
@RestController
@RequestMapping("member/member")
public class MemberController {
    @Autowired
    private MemberService memberService;

    @Autowired
    CouponFeighService couponFeighService;


    @PostMapping("/oauth2/login")
    public R oauthlogin(@RequestBody SocialUser socialUser ) throws Exception {

        MemberEntity entity = memberService.login(socialUser);
        if(entity!=null){
            //TODO 1、登陆成功处理
            return  R.ok().setData(entity);
        }else {
            return R.error(BizCodeEnum.LOGINACCT_PASSWORD_INVAILD_EXCEPTION.getCode(),
                    BizCodeEnum.LOGINACCT_PASSWORD_INVAILD_EXCEPTION.getMsg());
        }

    }


    @PostMapping("/login")
    public R login(@RequestBody MemberLoginVo vo ){

        MemberEntity entity = memberService.login(vo);
        if(entity!=null){
            //TODO 1、登陆成功处理
            return  R.ok();
        }else {
            return R.error(BizCodeEnum.LOGINACCT_PASSWORD_INVAILD_EXCEPTION.getCode(),
                    BizCodeEnum.LOGINACCT_PASSWORD_INVAILD_EXCEPTION.getMsg());
        }

    }


    /**
     * 注册功能
     * @return
     */
    @PostMapping("/regist")
    public R regist(@RequestBody MemberRegistVo vo){

        try{
            memberService.regist(vo);
        }catch (PhoneExistException e){
           return R.error(BizCodeEnum.PHONE_EXIST_EXCEPTION.getCode(), BizCodeEnum.PHONE_EXIST_EXCEPTION.getMsg());
        }catch (UsernameExistException e){
            return R.error(BizCodeEnum.USER_EXIST_EXCEPTION.getCode(), BizCodeEnum.USER_EXIST_EXCEPTION.getMsg());
        }
        return R.ok();
    }


    @RequestMapping("/coupons")
    public R test(){
        MemberEntity memberEntity = new MemberEntity();
        memberEntity.setNickname("张三");

        R membercoupons = couponFeighService.membercoupons();
        return R.ok().put("member",memberEntity).put("coupons",membercoupons);

    }
    /**
     * 列表
     */
    @RequestMapping("/list")
    //@RequiresPermissions("member:member:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = memberService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    //@RequiresPermissions("member:member:info")
    public R info(@PathVariable("id") Long id){
		MemberEntity member = memberService.getById(id);

        return R.ok().put("member", member);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
   // @RequiresPermissions("member:member:save")
    public R save(@RequestBody MemberEntity member){
		memberService.save(member);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    //@RequiresPermissions("member:member:update")
    public R update(@RequestBody MemberEntity member){
		memberService.updateById(member);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    //@RequiresPermissions("member:member:delete")
    public R delete(@RequestBody Long[] ids){
		memberService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
