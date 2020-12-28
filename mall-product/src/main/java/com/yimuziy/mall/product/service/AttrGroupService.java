package com.yimuziy.mall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yimuziy.common.utils.PageUtils;
import com.yimuziy.mall.product.entity.AttrGroupEntity;
import com.yimuziy.mall.product.vo.AttrGroupWithAttrsVo;
import com.yimuziy.mall.product.vo.SkuItemVo;
import com.yimuziy.mall.product.vo.SpuItemAttrGroupVo;

import java.util.List;
import java.util.Map;

/**
 * 属性分组
 *
 * @author yimuziy
 * @email yimuziy@gmail.com
 * @date 2020-11-26 16:42:21
 */
public interface AttrGroupService extends IService<AttrGroupEntity> {

    PageUtils queryPage(Map<String, Object> params);

    PageUtils queryPage(Map<String, Object> params, Long catelogId);

    /**
     * 获取分类下所有分组&关联属性
     * @param catelogId
     * @return
     */
    List<AttrGroupWithAttrsVo> getAttrGroupWithAttrsByCatelogId(Long catelogId);


    /**
     * 根据spuId获取spu属性的分组信息
     *
     * @param id
     * @param spuId
     * @return
     */
    List<SpuItemAttrGroupVo> getattrGroupWithAttrsBySpuId(Long id, Long spuId);
}

