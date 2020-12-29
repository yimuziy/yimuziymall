package com.yimuziy.mall.member.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yimuziy.common.utils.PageUtils;
import com.yimuziy.mall.member.entity.MemberEntity;
import com.yimuziy.mall.member.exception.PhoneExistException;
import com.yimuziy.mall.member.exception.UsernameExistException;
import com.yimuziy.mall.member.vo.MemberLoginVo;
import com.yimuziy.mall.member.vo.MemberRegistVo;
import com.yimuziy.mall.member.vo.SocialUser;

import java.util.Map;

/**
 * 会员
 *
 * @author yimuziy
 * @email yimuziy@gmail.com
 * @date 2020-11-26 17:29:00
 */
public interface MemberService extends IService<MemberEntity> {

    PageUtils queryPage(Map<String, Object> params);

    /**
     * 注册用户
     *
     * @param vo
     */
    void regist(MemberRegistVo vo);

    void checkPhoneUnique(String phone) throws PhoneExistException;

    void checkUsernameUnique(String username) throws UsernameExistException;

    /**
     * 用户登录
     *
     * @param vo
     * @return
     */
    MemberEntity login(MemberLoginVo vo);

    /**
     * 社交用户登陆
     *
     * @param socialUser
     * @return
     */
    MemberEntity login(SocialUser socialUser) throws Exception;
}

