package com.yimuziy.mall.product.vo;

import com.yimuziy.mall.product.entity.SkuImagesEntity;
import com.yimuziy.mall.product.entity.SkuInfoEntity;
import com.yimuziy.mall.product.entity.SpuInfoDescEntity;
import lombok.Data;

import java.util.List;

/**
 * @author ywz
 * @date 2020/12/23 14:24
 * @description 抽取详情页需要的数据模型
 */
@Data
public class SkuItemVo {

    //1、sku基本信息获取 pms_sku_info
    SkuInfoEntity info;

    boolean hasStock = true;

    //2、sku的图片信息  pms_sku_images
    List<SkuImagesEntity> images;

    //3、获取spu的销售属性组合
    List<SkuItemSaleAttrVo> saleAttr;

    //4、获取spu的介绍
    SpuInfoDescEntity desp;

    //5、获取spu的规格参数信息。
    List<SpuItemAttrGroupVo> groupAttrs;


}
