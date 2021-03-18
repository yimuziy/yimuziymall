package com.yimuziy.mall.order.web;

import com.yimuziy.common.exception.NoStockException;
import com.yimuziy.mall.order.service.OrderService;
import com.yimuziy.mall.order.vo.OrderConfirmVo;
import com.yimuziy.mall.order.vo.OrderSubmitVo;
import com.yimuziy.mall.order.vo.SubmitOrderResponseVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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


    /**
     * 下单功能
     * @param vo
     * @return
     */
    @PostMapping("/submitOrder")
    public String sumitOrder(OrderSubmitVo vo, Model model, RedirectAttributes redirectAttributes){

        try {
            SubmitOrderResponseVo responseVo = orderService.submitOrder(vo);
            //下单失败回到订单确认页重新确认订单信息
            System.out.println("订单提交的数据......"+vo);
            if(responseVo.getCode() == 0){
                //下单成功来到支付选择页
                model.addAttribute("submitOrderResp",responseVo);
                return "pay";
            }else{
                //下单失败回到订单确认页重新确认订单信息
                String msg = "下单失败";
                switch (responseVo.getCode()) {
                    case 1:
                        msg += "订单信息过期，请刷新再次提交";
                        break;
                    case 2:
                        msg += "订单商品发生变化，请确认后再次提交";
                        break;
                    case 3:
                        msg += "库存锁定失败，商品库存不足";
                        break;
                }
                redirectAttributes.addFlashAttribute("msg",msg);
                return "redirect:http://order.yimuziymall.com/toTrade";
            }
        } catch (Exception e) {
            if(e instanceof NoStockException){
                String message =( (NoStockException)e ).getMessage();
                redirectAttributes.addFlashAttribute("msg",message);
            }

            return "redirect:http://order.yimuziymall.com/toTrade";
        }
    }

}
