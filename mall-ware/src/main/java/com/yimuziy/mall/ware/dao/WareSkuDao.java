package com.yimuziy.mall.ware.dao;

import com.yimuziy.mall.ware.entity.WareSkuEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 商品库存
 *
 * @author yimuziy
 * @email yimuziy@gmail.com
 * @date 2020-11-26 17:51:52
 */
@Mapper
public interface WareSkuDao extends BaseMapper<WareSkuEntity> {

    void addStock(@Param("skuId") Long skuId, @Param("wareId") Long wareId, @Param("skuNum") Integer skuNum);

    Long getSkuStock(Long skuId);

    /**
     * 查询这个商品在哪些仓库有存货
     * @param skuId
     * @return
     */
    List<Long> listWareIdHasSkuStock(@Param("skuId") Long skuId);

    /**
     * 锁库存
     * @param skuId
     * @param wareId
     * @param num
     * @return
     */
    Long lockSkuStock(@Param("skuId") Long skuId, @Param("wareId") Long wareId, @Param("num") Integer num);
}
