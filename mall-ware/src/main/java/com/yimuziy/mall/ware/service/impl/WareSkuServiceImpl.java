package com.yimuziy.mall.ware.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yimuziy.common.utils.PageUtils;
import com.yimuziy.common.utils.Query;
import com.yimuziy.common.utils.R;
import com.yimuziy.mall.ware.dao.WareSkuDao;
import com.yimuziy.mall.ware.entity.WareSkuEntity;
import com.yimuziy.common.exception.NoStockException;
import com.yimuziy.mall.ware.feign.ProductFeignService;
import com.yimuziy.mall.ware.service.WareSkuService;
import com.yimuziy.mall.ware.vo.OrderItemVo;
import com.yimuziy.mall.ware.vo.SkuHasStockVo;
import com.yimuziy.mall.ware.vo.WareSkuLockVo;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service("wareSkuService")
public class WareSkuServiceImpl extends ServiceImpl<WareSkuDao, WareSkuEntity> implements WareSkuService {

    @Autowired
    WareSkuDao wareSkuDao;

    @Autowired
    ProductFeignService productFeignService;


    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        /**
         * skuId: 1
         * wareId: 1
         */
        QueryWrapper<WareSkuEntity> wrapper = new QueryWrapper<>();

        String skuId = (String) params.get("skuId");
        if (!StringUtils.isEmpty(skuId)) {
            wrapper.eq("sku_id", skuId);
        }

        String wareId = (String) params.get("wareId");
        if (!StringUtils.isEmpty(wareId)) {
            wrapper.eq("ware_id", wareId);
        }


        IPage<WareSkuEntity> page = this.page(
                new Query<WareSkuEntity>().getPage(params),
                wrapper
        );

        return new PageUtils(page);
    }

    @Transactional
    @Override
    public void addStock(Long skuId, Long wareId, Integer skuNum) {
        //判断如果没有这个库存记录新增
        List<WareSkuEntity> wareSkuEntities = wareSkuDao.selectList(new QueryWrapper<WareSkuEntity>().
                eq("sku_id", skuId).
                eq("ware_id", wareId));


        if (wareSkuEntities.size() == 0 || wareSkuEntities == null) {
            WareSkuEntity wareSkuEntity = new WareSkuEntity();
            wareSkuEntity.setSkuId(skuId);
            wareSkuEntity.setWareId(wareId);
            wareSkuEntity.setStock(skuNum);
            wareSkuEntity.setStockLocked(0);
            //TODO 远程查询sku的名字,如果失败整个事务无需回滚
            //1、自己catche异常
            //TODO 还可以用什么办法异常出现以后不回滚？高级不放呢
            try {
                R info = productFeignService.info(skuId);
                if (info.getCode() == 0) {
                    Map<String, Object> data = (Map<String, Object>) info.get("skuInfo");
                    wareSkuEntity.setSkuName((String) data.get("skuName"));
                }
            } catch (Exception e) {

            }

            wareSkuDao.insert(wareSkuEntity);
        } else {
            wareSkuDao.addStock(skuId, wareId, skuNum);
        }
    }

    @Override
    public List<SkuHasStockVo> getSkuHasStock(List<Long> skuIds) {
        List<SkuHasStockVo> collect = skuIds.stream().map(skuId -> {
            SkuHasStockVo vo = new SkuHasStockVo();

            //查询sku的总库存量
            Long count = baseMapper.getSkuStock(skuId);

            vo.setSkuId(skuId);
            vo.setHasStock(count == null ? false : true);


            return vo;
        }).collect(Collectors.toList());


        return collect;
    }

    /**
     * 为某个订单锁定库存
     *
     * (rollbackFor = NoStockException.class)
     * 默认只要是运行时异常都是回滚
     * @param vo
     * @return
     */
    @Transactional
    @Override
    public Boolean orderLockStock(WareSkuLockVo vo) {

        //1、按照下单的收货地址，找到一个就近仓库，锁定库存

        //1、找到每个商品在哪个仓库都有库存
        List<OrderItemVo> locks = vo.getLocks();

        List<SkuWareHasStock> collect = locks.stream().map(item -> {
            SkuWareHasStock stock = new SkuWareHasStock();
            Long skuId = item.getSkuId();
            stock.setSkuId(skuId);
            stock.setNum(item.getCount());
            //查询这个商品在哪里有库存
            List<Long> wareIds = wareSkuDao.listWareIdHasSkuStock(skuId);
            stock.setWareId(wareIds);

            return stock;
        }).collect(Collectors.toList());

        //2、锁定库存
        for (SkuWareHasStock hasStock : collect) {
            Boolean skuStocked = false ;
            Long skuId = hasStock.getSkuId();
            List<Long> wareIds = hasStock.getWareId();
            if (wareIds == null || wareIds.size() == 0) {
                //没有任何仓库有这个库存
                throw new NoStockException(skuId);
            }
            for (Long wareId : wareIds) {
                //成功就返回1，否则就是0
               Long count =  wareSkuDao.lockSkuStock(skuId,wareId,hasStock.getNum());
               if(count == 1){
                   //
                   skuStocked = true;
                   break;
               }else{
                   //当前仓库锁失败，重试下一个仓库
               }
            }
            if(skuStocked == false){
                //当前商品所有仓库都没有锁住
                throw new NoStockException(skuId);
            }
        }

        //3、肯定全部都是锁定成功过的
        return true;
    }

    @Data
    class SkuWareHasStock {
        private Long skuId;
        private Integer num;
        private List<Long> wareId;
    }
}