package com.yimuziy.mall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yimuziy.common.utils.PageUtils;
import com.yimuziy.mall.product.entity.CategoryEntity;
import com.yimuziy.mall.product.vo.Catelog2Vo;

import java.util.List;
import java.util.Map;

/**
 * 商品三级分类
 *
 * @author yimuziy
 * @email yimuziy@gmail.com
 * @date 2020-11-26 16:42:21
 */
public interface CategoryService extends IService<CategoryEntity> {

    PageUtils queryPage(Map<String, Object> params);

    List<CategoryEntity> listWithTree();

    void removeMenuByIds(List<Long> asList);

    /**
     * 找到catelogId的完整路径
     * 【父/子/孙子】
     * @param catelogId
     * @return
     */
    Long[] findCatelogPath(Long catelogId);


    /**
     * 级联更新数据
     * @param category
     */
    void updateCascade(CategoryEntity category);

    /**
     * 查询所有的一级分类
     * @return
     */
    List<CategoryEntity> getLevel1Categorys();

    /**
     * 获取二级分类和三级分类
     * @return
     */
    Map<String, List<Catelog2Vo>> getCatalogJson();
}

