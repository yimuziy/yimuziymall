package com.yimuziy.mall.product.dao;

import com.yimuziy.mall.product.entity.CategoryEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 商品三级分类
 *
 * @author yimuziy
 * @email yimuziy@gmail.com
 * @date 2020-11-26 16:42:21
 */
@Mapper
public interface CategoryDao extends BaseMapper<CategoryEntity> {

}
