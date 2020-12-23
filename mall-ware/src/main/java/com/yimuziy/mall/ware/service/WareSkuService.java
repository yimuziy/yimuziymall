package com.yimuziy.mall.ware.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yimuziy.common.utils.PageUtils;
import com.yimuziy.mall.ware.entity.WareSkuEntity;
import com.yimuziy.mall.ware.vo.SkuHasStockVo;

import java.util.List;
import java.util.Map;

/**
 * 商品库存
 *
 * @author yimuziy
 * @email yimuziy@gmail.com
 * @date 2020-11-26 17:51:52
 */
public interface WareSkuService extends IService<WareSkuEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void addStock(Long skuId, Long wareId, Integer skuNum);


    List<SkuHasStockVo> getSkuHasStock(List<Long> skuIds);
}

