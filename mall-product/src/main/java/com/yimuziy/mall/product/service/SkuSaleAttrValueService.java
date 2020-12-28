package com.yimuziy.mall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yimuziy.common.utils.PageUtils;
import com.yimuziy.mall.product.entity.SkuSaleAttrValueEntity;
import com.yimuziy.mall.product.vo.SkuItemSaleAttrVo;

import java.util.List;
import java.util.Map;

/**
 * sku销售属性&值
 *
 * @author yimuziy
 * @email yimuziy@gmail.com
 * @date 2020-11-26 16:42:21
 */
public interface SkuSaleAttrValueService extends IService<SkuSaleAttrValueEntity> {

    PageUtils queryPage(Map<String, Object> params);

    /**
     * 获取所有spu对应的sku
     * @param spuId
     * @return
     */
    List<SkuItemSaleAttrVo> getSaleAttrsBySpuId(Long spuId);
}

