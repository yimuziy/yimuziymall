package com.yimuziy.mall.seckill.service.impl;

import com.alibaba.csp.sentinel.Entry;
import com.alibaba.csp.sentinel.SphU;
import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.yimuziy.common.to.mq.SeckillOrderTo;
import com.yimuziy.common.utils.R;
import com.yimuziy.common.vo.MemberRespVo;
import com.yimuziy.mall.seckill.feign.CouponFeignService;
import com.yimuziy.mall.seckill.feign.ProductFeignService;
import com.yimuziy.mall.seckill.interceptor.LoginUserInterceptor;
import com.yimuziy.mall.seckill.service.SeckillService;
import com.yimuziy.mall.seckill.to.SecKillSkuRedisTo;
import com.yimuziy.mall.seckill.vo.SeckillSessionsWithSkus;
import com.yimuziy.mall.seckill.vo.SkuInfoVo;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RSemaphore;
import org.redisson.api.RedissonClient;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.TimeoutUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author: ywz
 * @createDate: 2021/3/7
 * @description:
 */
@Slf4j
@Service
public class SeckillServiceImpl implements SeckillService {

    @Autowired
    CouponFeignService couponFeignService;

    @Autowired
    ProductFeignService productFeignService;

    @Autowired
    RedissonClient redissonClient;

    @Autowired
    StringRedisTemplate redisTemplate;

    @Autowired
    RabbitTemplate rabbitTemplate;


    private final String SESSIONS_CACHE_PREFIX = "seckill:sessions:";
    private final String SKUKILL_CACHE_PREFIX = "seckill:skus";
    private final String SKU_STOCK_SEMAPHORE = "seckill:stock:"; //+商品随机码

    @Override
    public void uploadSeckillSkuLatest3Days() {
        //1、扫描最近三天需要参与秒杀的活动
        R session = couponFeignService.getLates3DaySession();
        if (session.getCode() == 0) {
            //上架商品信息
            List<SeckillSessionsWithSkus> sessionData = session.getData(new TypeReference<List<SeckillSessionsWithSkus>>() {
            });
            //缓存到redis中
            //1、缓存活动信息
            saveSessionInfos(sessionData);
            //2、缓存活动的关联信息
            saveSessionSkuInfos(sessionData);


        }
    }

    public List<SecKillSkuRedisTo> blockHandler(BlockException e){
        log.error("getCurrentSeckillSkus被限流了....");
        return null;
    }


    /**
     * blockHandler = "blockHandler" 函数会在原方法被限流/降级/系统保护的时候调用，而 fallback 函数会针对所有类型的异常
     * @return
     */
    //返回当前时间可以参与秒杀的商品信息
    @SentinelResource(value = "getCurrentSeckillSkusResource",blockHandler = "blockHandler")
    @Override
    public List<SecKillSkuRedisTo> getCurrentSeckillSkus() {
        //1、确定当前时间属于哪个秒杀场次
        //1970 -
        long time = new Date().getTime();

        try(Entry entry = SphU.entry("seckillSkus" )){
            Set<String> keys = redisTemplate.keys(SESSIONS_CACHE_PREFIX + "*");
            for (String key : keys) {
                //seckill:sessions:1615276478000_1615478400000
                String replace = key.replace(SESSIONS_CACHE_PREFIX, "");
                String[] s = replace.split("_");
                long start = Long.parseLong(s[0]);
                long end = Long.parseLong(s[1]);
                if(time >= start && time<=end){
                    //2、获取这个秒杀场次需要的所有商品信息
                    List<String> range = redisTemplate.opsForList().range(key, -100, 100);
                    BoundHashOperations<String, String, String> hashOps = redisTemplate.boundHashOps(SKUKILL_CACHE_PREFIX);
                    List<String> list = hashOps.multiGet(range);
                    if(list != null){
                        List<SecKillSkuRedisTo> collect = list.stream().map(item -> {
                            SecKillSkuRedisTo redis = JSON.parseObject(item.toString(), SecKillSkuRedisTo.class);
//                        redis.setRandomCode(null);  当前秒杀开始就需要随机码
                            return redis;
                        }).collect(Collectors.toList());
                        return collect;
                    }
                    break;
                }
            }
        }catch (BlockException e){
            log.error("资源被限流",e.getMessage());
        }








        return null;
    }

    @Override
    public SecKillSkuRedisTo getSkuSeckillInfo(Long skuId) {

        //1、找到所有需要参与秒杀的商品的key
        BoundHashOperations<String, String, String> hashOps = redisTemplate.boundHashOps(SKUKILL_CACHE_PREFIX);

        Set<String> keys = hashOps.keys();
        if(keys!=null && keys.size()>0){
            String reg = "\\d_"+skuId;
            for (String key : keys) {
                //6_4
                if (Pattern.matches(reg, key)) {
                    String json = hashOps.get(key);
                    SecKillSkuRedisTo skuRedisTo = JSON.parseObject(json, SecKillSkuRedisTo.class);

                    //随机码
                    long time = new Date().getTime();
                    if(time >=  skuRedisTo.getStartTime() && time <= skuRedisTo.getEndTime()){

                    }else{
                        skuRedisTo.setRandomCode(null);
                    }
                    return skuRedisTo;
                }
            }
        }

        return null;
    }

    //TODO 上架秒杀商品的时候，么一个数据都有过期时间。
    //TODO 秒杀后续流程，简化了收货地址等信息。
    //TODO 秒杀提前锁定库存   如果超过秒杀时间段要根据信号量回退库存
    @Override
    public String kill(String killId, String key, Integer num) {
        MemberRespVo respVo = LoginUserInterceptor.loginUser.get();

        //1、获取当前秒杀商品的详细信息
        BoundHashOperations<String, String, String> hashOps = redisTemplate.boundHashOps(SKUKILL_CACHE_PREFIX);

        String json = hashOps.get(killId);
        if(StringUtils.isEmpty(json)){
            return null;
        }else{
            SecKillSkuRedisTo redis = JSON.parseObject(json, SecKillSkuRedisTo.class);
            //校验合法性
            Long startTime = redis.getStartTime();
            Long endTime = redis.getEndTime();
            long time = new Date().getTime();

            long ttl = endTime - time;
            //1、校验时间的合法性
            if(time>=startTime && time<=endTime){
                //2、校验随机码和商品id
                String randomCode = redis.getRandomCode();
                String skuId = redis.getPromotionSessionId()+"_"+redis.getSkuId();
                if(randomCode.equals(key) && skuId.equals(killId)){
                    //3、验证购物数量是否合理
                    if(num <=redis.getSeckillLimit()){
                        //4、验证这个人是否已经购买过了。  幂等性； 如果只要秒杀成功，就去占位. userId_SessionId_skuId
                        //SETNX
                        String redisKey = respVo.getId()+"_"+skuId;
                        //自动过期
                        Boolean aBoolean = redisTemplate.opsForValue().setIfAbsent(redisKey, num.toString(), ttl, TimeUnit.MILLISECONDS);
                        if(aBoolean){
                            //占位成功说明从来没有买过
                            RSemaphore semaphore = redissonClient.getSemaphore(SKU_STOCK_SEMAPHORE + randomCode);
                            try {
                                //  20ms
                                boolean b = semaphore.tryAcquire(num,100, TimeUnit.MILLISECONDS);
                                if(b){
                                    //秒杀成功；
                                    //快速下单。发送MQ消息  10ms
                                    String timeId = IdWorker.getTimeId();
                                    SeckillOrderTo orderTo = new SeckillOrderTo();
                                    orderTo.setOrderSn(timeId);
                                    orderTo.setMemberId(respVo.getId());
                                    orderTo.setNum(num);
                                    orderTo.setPromotionSessionId(redis.getPromotionSessionId());
                                    orderTo.setSkuId(redis.getSkuId());
                                    orderTo.setSeckillPrice(redis.getSeckillPrice());

                                    rabbitTemplate.convertAndSend("order-event-exchange","order.seckill.order",orderTo);

                                    return timeId;
                                }
                                return null;
                            } catch (InterruptedException e) {
                                return null;
                            }
                        }else{
                            //说明已经买过了
                            return null;
                        }
                    }
                }else{
                    return null;
                }
            }else{
                return null;
            }
        }

        return null;
    }


    private void saveSessionInfos(List<SeckillSessionsWithSkus> sessions) {
        if(sessions!=null) {
            sessions.stream().forEach(session -> {
                Long startTime = session.getStartTime().getTime();
                Long endTime = session.getEndTime().getTime();
                String key = SESSIONS_CACHE_PREFIX + startTime + "_" + endTime;
                //缓存活动信息
                Boolean aBoolean = redisTemplate.hasKey(key);
                if (!aBoolean) {
                    List<String> collect = session.getRelationSkus().stream().map(item -> item.getPromotionSessionId() + "_" + item.getSkuId().toString()).collect(Collectors.toList());
                    redisTemplate.opsForList().leftPushAll(key, collect);
                }

            });
        }
    }

    private void saveSessionSkuInfos(List<SeckillSessionsWithSkus> sessions) {
        //准备hash操作
        BoundHashOperations<String, Object, Object> ops = redisTemplate.boundHashOps(SKUKILL_CACHE_PREFIX);


        if(sessions!=null) {
            sessions.stream().forEach(session -> {
                session.getRelationSkus().stream().forEach(seckillSkuVo -> {
                    //4、随机码？ seckill?skuId=1&key=dsafdjsaioje
                    String token = UUID.randomUUID().toString().replace("_", "");

                    if (!ops.hasKey(seckillSkuVo.getPromotionSessionId().toString() + "_" + seckillSkuVo.getSkuId().toString())) {
                        //缓存商品
                        SecKillSkuRedisTo redisTo = new SecKillSkuRedisTo();
                        //1、sku的基本数据
                        R skuInfo = productFeignService.getSkuInfo(seckillSkuVo.getSkuId());
                        if (skuInfo.getCode() == 0) {
                            SkuInfoVo info = skuInfo.getData("skuInfo", new TypeReference<SkuInfoVo>() {
                            });
                            redisTo.setSkuInfo(info);
                        }


                        //2、sku的秒杀信息
                        BeanUtils.copyProperties(seckillSkuVo, redisTo);

                        //3、设置当前商品的秒杀时间信息
                        redisTo.setStartTime(session.getStartTime().getTime());
                        redisTo.setEndTime(session.getEndTime().getTime());

                        //设置随机码
                        redisTo.setRandomCode(token);


                        String jsonString = JSON.toJSONString(redisTo);
                        ops.put(seckillSkuVo.getPromotionSessionId().toString() + "_" + seckillSkuVo.getSkuId().toString(), jsonString);

                        //如果当前这个场次的商品的库存信息已经上架就不需要上架
                        //5、使用库存作为分布式的信号量  限流；
                        RSemaphore semaphore = redissonClient.getSemaphore(SKU_STOCK_SEMAPHORE + token);
                        //商品可以秒杀的数量作为信号量
                        semaphore.trySetPermits(seckillSkuVo.getSeckillCount());


                    }


                });
            });
        }
    }
}
