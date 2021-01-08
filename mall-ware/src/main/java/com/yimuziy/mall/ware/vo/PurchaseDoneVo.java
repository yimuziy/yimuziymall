package com.yimuziy.mall.ware.vo;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author ywz
 * @date 2020/12/5 20:47
 * @description
 */
@Data
public class PurchaseDoneVo {
    @NotNull
    private Long id;//采购单id

    private List<PurchaseItemDoneVo> items;
}
