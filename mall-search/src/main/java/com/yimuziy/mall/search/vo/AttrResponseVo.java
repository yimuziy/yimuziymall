package com.yimuziy.mall.search.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

/**
 * @author ywz
 * @date 2020/12/20 16:33
 * @description
 */
@Data
public class AttrResponseVo {

    /**
     * 属性id
     */
    @TableId
    private Long attrId;
    /**
     * 属性名
     */
    private String attrName;
    /**
     * 是否需要检索[0-不需要，1-需要]
     */
    private Integer searchType;
    /**
     * 属性图标
     */
    private String icon;
    /**
     * 值类型[0-单属性，1-多属性性]
     */
    private Integer valueType;
    /**
     * 可选值列表[用逗号分隔]
     */
    private String valueSelect;
    /**
     * 属性类型[0-销售属性，1-基本属性，2-既是销售属性又是基本属性]
     */
    private Integer attrType;
    /**
     * 启用状态[0 - 禁用，1 - 启用]
     */
    private Long enable;
    /**
     * 所属分类
     */
    private Long catelogId;
    /**
     * 快速展示【是否展示在介绍上；0-否 1-是】，在sku中仍然可以调整
     */
    private Integer showDesc;

    @TableField(exist = false)
    private Long attrGroupId;

    /**
     * "catelogName": "手机/数码/手机", //所属分类名字
     * "groupName": "主体", //所属分组名字
     */
    private String catelogName;
    private String groupName;


    private Long[] catelogPath;


}
