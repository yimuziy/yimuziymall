package com.yimuziy.mall.product.vo;

import lombok.Data;
import lombok.ToString;

import java.util.List;

/**
 * @author ywz
 * @date 2020/12/23 17:52
 * @description
 */
@Data
@ToString
public class SkuItemSaleAttrVo {
    private Long attrId;
    private String attrName;
    private List<AttrValueWithSkuIdVo> attrValues;
}
