package com.yimuziy.mall.order.service.impl;

import com.yimuziy.common.vo.MemberRespVo;
import com.yimuziy.mall.order.feign.CartFeignService;
import com.yimuziy.mall.order.feign.MemberFeignService;
import com.yimuziy.mall.order.interceptor.LoginUserInterceptor;
import com.yimuziy.mall.order.vo.MemberAddressVo;
import com.yimuziy.mall.order.vo.OrderConfirmVo;
import com.yimuziy.mall.order.vo.OrderItemVo;
import org.eclipse.jetty.util.Promise;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yimuziy.common.utils.PageUtils;
import com.yimuziy.common.utils.Query;

import com.yimuziy.mall.order.dao.OrderDao;
import com.yimuziy.mall.order.entity.OrderEntity;
import com.yimuziy.mall.order.service.OrderService;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;


@Service("orderService")
public class OrderServiceImpl extends ServiceImpl<OrderDao, OrderEntity> implements OrderService {

    @Autowired
    MemberFeignService memberFeignService;

    @Autowired
    CartFeignService cartFeignService;

    @Autowired
    ThreadPoolExecutor executor;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<OrderEntity> page = this.page(
                new Query<OrderEntity>().getPage(params),
                new QueryWrapper<OrderEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public OrderConfirmVo confirmOrder() throws ExecutionException, InterruptedException {
        OrderConfirmVo confirmVo = new OrderConfirmVo();
        MemberRespVo memberRespVo = LoginUserInterceptor.loginUser.get();
        System.out.println("主线程..."+ Thread.currentThread().getId());

        //获取之前的请求
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();

        CompletableFuture<Void> getAddressFuture = Promise.Completable.runAsync(() -> {
            //1、远程查询所有的收获地址列表
            System.out.println("member线程..."+ Thread.currentThread().getId());
            //每一个线程都来共享之前的请求数据
            RequestContextHolder.setRequestAttributes(requestAttributes);
            List<MemberAddressVo> address = memberFeignService.getAddress(memberRespVo.getId());
            confirmVo.setAddress(address);
        }, executor);


        CompletableFuture<Void> cartFuture = CompletableFuture.runAsync(() -> {
            //2、远程查询购物车所有选中的购物项
            System.out.println("cart线程..."+ Thread.currentThread().getId());
            //每一个线程都来共享之前的请求数据
            RequestContextHolder.setRequestAttributes(requestAttributes);
            List<OrderItemVo> items = cartFeignService.getCurrentUserCartItems();
            confirmVo.setItems(items);
            //feign在远程调用之前要构造请求，调用很多的拦截器
            //RequestInterceptor interceptor : requestInterceptors
        }, executor);




        //3、查询用户积分
        Integer integration = memberRespVo.getIntegration();
        confirmVo.setIntegration(integration);

        //4、其他数据自动计算

        //TODO 5、防重令牌
        CompletableFuture.allOf(getAddressFuture,cartFuture).get();

        return confirmVo;
    }

}