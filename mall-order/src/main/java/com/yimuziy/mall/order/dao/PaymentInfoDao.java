package com.yimuziy.mall.order.dao;

import com.yimuziy.mall.order.entity.PaymentInfoEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 支付信息表
 *
 * @author yimuziy
 * @email yimuziy@gmail.com
 * @date 2020-11-26 17:37:58
 */
@Mapper
public interface PaymentInfoDao extends BaseMapper<PaymentInfoEntity> {

}
