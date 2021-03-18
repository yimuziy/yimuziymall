package com.yimuziy.mall.seckill.controller;

import com.yimuziy.common.utils.R;
import com.yimuziy.mall.seckill.service.SeckillService;
import com.yimuziy.mall.seckill.to.SecKillSkuRedisTo;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RScript;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author: ywz
 * @createDate: 2021/3/8
 * @description:
 */
@Slf4j
@Controller
public class SeckillController {

    @Autowired
    SeckillService seckillService;

    /**
     * 返回当前时间可以参与的秒杀商品信息
     * @return
     */
    @ResponseBody
    @GetMapping("/currentSeckillSkus")
    public R getCurrentSeckillSkus(){
        log.info("currentSeckillSkus正在执行......");
        List<SecKillSkuRedisTo> vos =seckillService.getCurrentSeckillSkus();

        return R.ok().setData(vos);
    }


    @ResponseBody
    @GetMapping("/sku/seckill/{skuId}")
    public R getSkuSeckillInfo(@PathVariable("skuId") Long skuId){

        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        SecKillSkuRedisTo to = seckillService.getSkuSeckillInfo(skuId);
        return R.ok().setData(to);
    }


    @GetMapping("/kill")
    public String seckill(@RequestParam("killid") String killId,
                          @RequestParam("key") String key,
                          @RequestParam("num") Integer num,
                          Model model){
        String orderSn = seckillService.kill(killId,key,num);


        model.addAttribute("orderSn",orderSn);

        return "success";
    }



}
