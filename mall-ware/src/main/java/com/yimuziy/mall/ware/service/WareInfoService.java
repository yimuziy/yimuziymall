package com.yimuziy.mall.ware.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yimuziy.common.utils.PageUtils;
import com.yimuziy.mall.ware.entity.WareInfoEntity;
import com.yimuziy.mall.ware.vo.FareVo;

import java.math.BigDecimal;
import java.util.Map;

/**
 * 仓库信息
 *
 * @author yimuziy
 * @email yimuziy@gmail.com
 * @date 2020-11-26 17:51:52
 */
public interface WareInfoService extends IService<WareInfoEntity> {

    PageUtils queryPage(Map<String, Object> params);

    /**
     * 根据用户的收获地址计算运费
     * @param addrId
     * @return
     */
    FareVo getFare(Long addrId);
}

