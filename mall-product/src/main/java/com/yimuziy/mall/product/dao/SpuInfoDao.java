package com.yimuziy.mall.product.dao;

import com.yimuziy.mall.product.entity.SpuInfoEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * spu信息
 *
 * @author yimuziy
 * @email yimuziy@gmail.com
 * @date 2020-11-26 16:42:21
 */
@Mapper
public interface SpuInfoDao extends BaseMapper<SpuInfoEntity> {


    void updateSpuStatus(@Param("spuId") Long spuId, @Param("code") int code);
}
