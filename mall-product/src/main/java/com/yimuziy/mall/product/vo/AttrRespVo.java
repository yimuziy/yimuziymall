package com.yimuziy.mall.product.vo;

import lombok.Data;

/**
 * @author ywz
 * @date 2020/12/2 19:12
 * @description
 */
@Data
public class AttrRespVo extends AttrVo {
    /**
     * "catelogName": "手机/数码/手机", //所属分类名字
     * "groupName": "主体", //所属分组名字
     */
    private String catelogName;
    private String groupName;


    private Long[] catelogPath;
}
