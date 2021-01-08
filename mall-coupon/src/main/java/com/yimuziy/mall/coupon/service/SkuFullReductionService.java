package com.yimuziy.mall.coupon.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yimuziy.common.to.SkuReductionTo;
import com.yimuziy.common.utils.PageUtils;
import com.yimuziy.mall.coupon.entity.SkuFullReductionEntity;

import java.util.Map;

/**
 * 商品满减信息
 *
 * @author yimuziy
 * @email yimuziy@gmail.com
 * @date 2020-11-26 17:10:15
 */
public interface SkuFullReductionService extends IService<SkuFullReductionEntity> {

    PageUtils queryPage(Map<String, Object> params);

    /**
     * 保存Sku的优惠满减信息
     *
     * @param reductionTo
     */
    void saveSkuReduction(SkuReductionTo reductionTo);
}

