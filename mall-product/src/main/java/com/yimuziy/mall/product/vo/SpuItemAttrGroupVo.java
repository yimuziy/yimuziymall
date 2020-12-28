package com.yimuziy.mall.product.vo;

import lombok.Data;
import lombok.ToString;

import java.util.List;

/**
 * @author ywz
 * @date 2020/12/23 17:46
 * @description
 */
@Data
@ToString
public class SpuItemAttrGroupVo {

    private String groupName;
    private List<Attr> attrs;
}
