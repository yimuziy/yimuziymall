package com.yimuziy.mall.cart.controller;

import com.yimuziy.common.constant.AuthServerConstant;
import com.yimuziy.mall.cart.interceptor.CartInterceptor;
import com.yimuziy.mall.cart.service.CartService;
import com.yimuziy.mall.cart.vo.Cart;
import com.yimuziy.mall.cart.vo.CartItem;
import com.yimuziy.mall.cart.vo.UserInfoTo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import java.util.concurrent.ExecutionException;

/**
 * @author ywz
 * @date 2020/12/29 21:18
 * @description
 */
@Controller
public class CartController {


    @Autowired
    CartService cartService;

    /**
     * 删除购物项
     */
    @GetMapping("/deleteItem")
    public String deleteItem(@RequestParam("skuId")Long skuId){
        cartService.deleteItem(skuId);
        return "redirect:http://cart.yimuziymall.com/cart.html";
    }


    /**
     * 修改购物车商品的数量
     * @return
     */
    @GetMapping("/countItem")
    public String countItem(@RequestParam("skuId")Long skuId,
                            @RequestParam("num") Integer num){

        cartService.changeItemCount(skuId,num);
        return "redirect:http://cart.yimuziymall.com/cart.html";
    }

    /**
     * 修改购物车商品的选中状态
     * @param skuId
     * @param check
     * @return
     */
    @GetMapping("/checkItem")
    public String checkItem(@RequestParam("skuId") Long skuId,
                            @RequestParam("check") Integer check){
        cartService.checkItem(skuId,check);

        return "redirect:http://cart.yimuziymall.com/cart.html";
    }


    /**
     *  浏览器有一个cookie： user-key：标识用户身份，一个月后过期；
     *  如果第一次使用jd的购物车，都会给一个临时的用户身份
     *  浏览器以后保存，每次访问都会带上这个cookie；
     *
     *
     *  登录：session有
     *  没登录: 按照cookie里面带来user-key来做。
     *  第一次：如果没有临时用户，帮忙创建一个临时用户。
     * @return
     */
    @GetMapping("/cart.html")
    public String cartListPage(Model model) throws ExecutionException, InterruptedException {

        //1、快速得到用户信息，id，user-key
//        UserInfoTo userInfoTo = CartInterceptor.threadLocal.get();
//        System.out.println(userInfoTo);

        Cart cart = cartService.getCart();
        model.addAttribute("cart",cart);

        return "cartList";
    }

    /**
     * 添加商品到购物车
     *   RedirectAttributes ra
     *           ra.addFlashAttribute() ：将数据放在session里面可以在页面中取出，但是只能取出一次
     *           ra.addAttribute("skuId",skuId); 将数据放在url后面
     * @return
     */
    @GetMapping("/addTocart")
    public String addTocart(@RequestParam("skuId") Long skuId,
                            @RequestParam(value = "num",required = false) Integer num,
                            RedirectAttributes ra) throws ExecutionException, InterruptedException {

        CartItem  cartItem =  cartService.addToCart(skuId,num);
        ra.addAttribute("skuId",skuId);

        return "redirect:http://cart.yimuziymall.com/addToCartSuccess.html";
    }


    /**
     * 跳转到成功页
     *
     * @param skuId
     * @param model
     * @return
     */
    @GetMapping("/addToCartSuccess.html")
    public String addTOCartSuccessPage(@RequestParam("skuId") Long skuId,Model model){
        //重定向到成功页面。再次查询购物车即可
        CartItem item = cartService.getCartItem(skuId);
        model.addAttribute("item", item);

        return "success";
    }











}
