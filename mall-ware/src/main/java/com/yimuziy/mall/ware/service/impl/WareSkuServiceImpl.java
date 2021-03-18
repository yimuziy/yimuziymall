package com.yimuziy.mall.ware.service.impl;

import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.databind.util.BeanUtil;
import com.rabbitmq.client.Channel;
import com.yimuziy.common.to.mq.OrderTo;
import com.yimuziy.common.to.mq.StockDetailTo;
import com.yimuziy.common.to.mq.StockLockedTo;
import com.yimuziy.common.utils.PageUtils;
import com.yimuziy.common.utils.Query;
import com.yimuziy.common.utils.R;
import com.yimuziy.mall.ware.dao.WareSkuDao;
import com.yimuziy.mall.ware.entity.WareOrderTaskDetailEntity;
import com.yimuziy.mall.ware.entity.WareOrderTaskEntity;
import com.yimuziy.mall.ware.entity.WareSkuEntity;
import com.yimuziy.common.exception.NoStockException;
import com.yimuziy.mall.ware.feign.OrderFeignService;
import com.yimuziy.mall.ware.feign.ProductFeignService;
import com.yimuziy.mall.ware.service.WareOrderTaskDetailService;
import com.yimuziy.mall.ware.service.WareOrderTaskService;
import com.yimuziy.mall.ware.service.WareSkuService;
import com.yimuziy.mall.ware.vo.OrderItemVo;
import com.yimuziy.mall.ware.vo.OrderVo;
import com.yimuziy.mall.ware.vo.SkuHasStockVo;
import com.yimuziy.mall.ware.vo.WareSkuLockVo;
import lombok.Data;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;



@Service("wareSkuService")
public class WareSkuServiceImpl extends ServiceImpl<WareSkuDao, WareSkuEntity> implements WareSkuService {

    @Autowired
    WareSkuDao wareSkuDao;

    @Autowired
    ProductFeignService productFeignService;

    @Autowired
    RabbitTemplate rabbitTemplate;

    @Autowired
    WareOrderTaskService orderTaskService;

    @Autowired
    WareOrderTaskDetailService orderTaskDetailService;

    @Autowired
    OrderFeignService orderFeignService;





    private void  unLockStock(Long skuId, Long wareId, Integer num ,Long taskDetailId){
        //库存解锁
        wareSkuDao.unlockStocke(skuId,wareId,num);
        //更新库存工作单的状态
        WareOrderTaskDetailEntity entity = new WareOrderTaskDetailEntity();
        entity.setId(taskDetailId);
        entity.setLockStatus(2); //变为已解锁

        orderTaskDetailService.updateById(entity);
    }

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
     *
     * 库存解锁的场景
     * 1）、下订单成功，订单过期没有支付被系统自动取消、被用户手动取消
     *
     *
     * 2）、下订单成功，库存锁定成功，接下来的业务调用失败，导致订单回滚。
     *       之前锁定的库存就要自动解锁。
     *
     *
     * @return
     */
    @Transactional
    @Override
    public Boolean orderLockStock(WareSkuLockVo vo) {

        /**
         * 保存库存工作单的详情。
         * 追溯。
         */
        WareOrderTaskEntity taskEntity = new WareOrderTaskEntity();
        taskEntity.setOrderSn(vo.getOrderSn());

        orderTaskService.save(taskEntity);



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
            //1、如果每一个商品都锁定成功，将当前商品锁定了几件的工作单记录发送给MQ
            //2、锁定失败。前面保存的工作单信息就回滚了。 发送出去的消息，即使要解锁记录，由于去数据库查不到id，所以就不用解锁
            //   1:1 - 2 - 1   2: 2-1-2   3: 3-1-1(x)
            for (Long wareId : wareIds) {
                //成功就返回1，否则就是0
               Long count =  wareSkuDao.lockSkuStock(skuId,wareId,hasStock.getNum());
               if(count == 1){
                   //
                   skuStocked = true;
                   //TODO  告诉MQ库存锁定成功
                   WareOrderTaskDetailEntity entity = new WareOrderTaskDetailEntity(null,
                           skuId,
                           "",
                           hasStock.getNum(),
                           taskEntity.getId(),
                           wareId,
                           1);
                   orderTaskDetailService.save(entity);
                   StockLockedTo lockedTo = new StockLockedTo();
                   lockedTo.setId(taskEntity.getId());
                   StockDetailTo stockDetailTo = new StockDetailTo();
                   BeanUtils.copyProperties(entity,stockDetailTo);
                   //只发id不行， 防止回滚以后找不到数据
                   lockedTo.setDetail(stockDetailTo);
//                   rabbitTemplate
                   rabbitTemplate.convertAndSend("stock-event-exchange","stock.locked",lockedTo);
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

    @Override
    public void unlockStock(StockLockedTo to) {


            StockDetailTo detail = to.getDetail();
            Long skuId = detail.getSkuId();
            Long detailId = detail.getId();

            //解锁
            //1、查询数据库关于这个订单锁定库存信息。
            // 有：证明库存锁定成功了
            //    解锁：订单情况。
            //       1、没有这个订单。必须解锁
            //       2、有这个订单。不是解锁库存。
            //            订单状态： 已取消：解锁库存
            //                      没取消：不能解锁库存
            // 没有：库存锁定失败了，库存回滚了。这种情况无需解锁
            WareOrderTaskDetailEntity byId = orderTaskDetailService.getById(detailId);
            if(byId != null ){
                //解锁
                Long id = to.getId();  //库存工作单的id；
                WareOrderTaskEntity taskEntity = orderTaskService.getById(id);
                String orderSn = taskEntity.getOrderSn();//根据订单号查询订单的状态
                R r = orderFeignService.getOrderStatus(orderSn);
                if(r.getCode() == 0){
                    //订单数据返回成功
                    OrderVo orderVo = r.getData(new TypeReference<OrderVo>() {
                    });

                    if(orderVo == null ||orderVo.getStatus() == 4){
                        //订单不存在
                        //订单已经被取消了。才能解锁库存
                        //detailId
                        if(byId.getLockStatus() == 1){
                            //挡墙库存工作单详情，状态为1 已锁定但是未解锁才可以解锁
                            unLockStock(detail.getSkuId(), detail.getWareId(), detail.getSkuNum(), detailId);
                        }
                    }

                }else{
                    //消息拒绝以后重新放到队列，让别人继续消费解锁
                    throw new RuntimeException("远程服务失败");
                }


            }else{
                //无需解锁
            }


    }


    /**
     * 防止订单服务卡顿，导致订单状态消息一直改不了，库存消息优先到期。查询订单状态为新建状态，什么都不做就走了。
     * 导致卡顿的订单，永远不能解锁库存
     * @param orderTo
     */
    @Transactional
    @Override
    public void unlockStock(OrderTo orderTo) {
        String orderSn = orderTo.getOrderSn();
        //查一下最新的状态（按理说，这里的是由订单发送的解锁库存消息，订单号不会改动）
//        R r = orderFeignService.getOrderStatus(orderSn);

        //查一下库存的最新状态，防止重复解锁库存
        WareOrderTaskEntity task = orderTaskService.getOrderTaskByOrderSn(orderSn);
        Long id = task.getId();
        //根据工作单找到所有 没有解锁的库存,进行解锁
        List<WareOrderTaskDetailEntity> entities = orderTaskDetailService.list(new QueryWrapper<WareOrderTaskDetailEntity>().
                eq("task_id", id).eq("lock_status",1));


        //Long skuId, Long wareId, Integer num ,Long taskDetailId
        for (WareOrderTaskDetailEntity entity : entities) {
            unLockStock(entity.getSkuId(),entity.getWareId(), entity.getSkuNum(), entity.getId());
        }


    }

    @Data
    class SkuWareHasStock {
        private Long skuId;
        private Integer num;
        private List<Long> wareId;
    }
}