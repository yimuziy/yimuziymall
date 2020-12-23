package com.yimuziy.mall.search.service;

import com.yimuziy.mall.search.vo.SearchParam;
import com.yimuziy.mall.search.vo.SearchResult;

/**
 * @author ywz
 * @date 2020/12/17 22:27
 * @description
 */
public interface MallSearchService {

    /**
     *
     * @param param 检索的所有参数
     * @return 检索的结果,里面包含页面所需要的所有信息
     */
    SearchResult search(SearchParam param);
}
