package com.yimuziy.mall.search.service;

import com.yimuziy.common.to.es.SkuEsModel;

import java.io.IOException;
import java.util.List;

/**
 * @author ywz
 * @date 2020/12/11 15:58
 * @description
 */
public interface ProductSaveService {
    boolean productStatusUp(List<SkuEsModel> skuEsModels) throws IOException;


}
