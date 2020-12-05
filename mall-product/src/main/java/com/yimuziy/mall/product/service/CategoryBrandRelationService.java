package com.yimuziy.mall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yimuziy.common.utils.PageUtils;
import com.yimuziy.mall.product.entity.BrandEntity;
import com.yimuziy.mall.product.entity.CategoryBrandRelationEntity;

import java.util.List;
import java.util.Map;

/**
 * 品牌分类关联
 *
 * @author yimuziy
 * @email yimuziy@gmail.com
 * @date 2020-11-26 17:13:16
 */
public interface CategoryBrandRelationService extends IService<CategoryBrandRelationEntity> {

    PageUtils queryPage(Map<String, Object> params);

    /**
     * 保存详细信息
     * @param categoryBrandRelation
     */
    void saveDetail(CategoryBrandRelationEntity categoryBrandRelation);

    /**
     * 关联更新品牌
     * @param brandId
     * @param name
     */
    void updateBrand(Long brandId, String name);

    /**
     * 级联更新category
     * @param catId
     * @param name
     */
    void updateCategory(Long catId, String name);

    /**
     * 查询执行分类的品牌信息
     * @param catId
     * @return
     */
    List<BrandEntity> getBrandsByCatId(Long catId);
}

