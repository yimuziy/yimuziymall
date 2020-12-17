package com.yimuziy.mall.ware.vo;

import lombok.Data;

import java.util.List;

/**
 * @author ywz
 * @date 2020/12/5 19:23
 * @description
 */
@Data
public class MergeVo {

   private Long  purchaseId; //整单id
   private List<Long> items; //合并项集合


}

