package com.yimuziy.mall.seckill.service;

import com.yimuziy.mall.seckill.to.SecKillSkuRedisTo;

import java.util.List;

public interface SeckillService {
    /**
     * 上架近3天需要秒杀的项目
     */
    void uploadSeckillSkuLatest3Days();

    /**
     * 获取当前时间可以参与秒杀的商品信息
     * @return
     */
    List<SecKillSkuRedisTo> getCurrentSeckillSkus();

    /**
     * 根据skuId查询当前商品是否正在秒杀
     * @param skuId
     * @return
     */
    SecKillSkuRedisTo getSkuSeckillInfo(Long skuId);

    /**
     * 秒杀商品
     * @param killId
     * @param key
     * @param num
     * @return
     */
    String kill(String killId, String key, Integer num);
}
