package com.yimuziy.mall.product.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.yimuziy.mall.product.entity.AttrEntity;
import lombok.Data;

import java.util.List;

/**
 * @author ywz
 * @date 2020/12/3 22:57
 * @description
 */
@Data
public class AttrGroupWithAttrsVo {


    /**
     * 分组id
     */
    @TableId
    private Long attrGroupId;
    /**
     * 组名
     */
    private String attrGroupName;
    /**
     * 排序
     */
    private Integer sort;
    /**
     * 描述
     */
    private String descript;
    /**
     * 组图标
     */
    private String icon;
    /**
     * 所属分类id
     */
    private Long catelogId;

    private List<AttrEntity> attrs;
}
