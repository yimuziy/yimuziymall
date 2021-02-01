package com.yimuziy.mall.order.web;

import com.yimuziy.mall.order.service.OrderService;
import com.yimuziy.mall.order.vo.OrderConfirmVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.concurrent.ExecutionException;

/**
 * @author ywz
 * @date 2021/1/10 22:46
 * @description
 */
@Controller
public class OrderWebController {

    @Autowired
    OrderService orderService;

    @GetMapping("/toTrade")
    public String toTrade(Model model) throws ExecutionException, InterruptedException {
        OrderConfirmVo confirmVo = orderService.confirmOrder();

        model.addAttribute("orderConfirmData",confirmVo);

        //展示订单确认的数据
        return "confirm";
    }

}
