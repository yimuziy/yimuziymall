package com.yimuziy.mall.member.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yimuziy.common.utils.PageUtils;
import com.yimuziy.mall.member.entity.MemberReceiveAddressEntity;

import java.util.List;
import java.util.Map;

/**
 * 会员收货地址
 *
 * @author yimuziy
 * @email yimuziy@gmail.com
 * @date 2020-11-26 17:29:00
 */
public interface MemberReceiveAddressService extends IService<MemberReceiveAddressEntity> {

    PageUtils queryPage(Map<String, Object> params);

    /**
     * 根据会员Id获取地址信息
     * @param memberId
     * @return
     */
    List<MemberReceiveAddressEntity> getAddress(Long memberId);
}

