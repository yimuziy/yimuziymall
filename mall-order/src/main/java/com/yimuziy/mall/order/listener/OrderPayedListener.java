package com.yimuziy.mall.order.listener;

import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;
import com.yimuziy.mall.order.config.AlipayTemplate;
import com.yimuziy.mall.order.service.OrderService;
import com.yimuziy.mall.order.vo.PayAsyncVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @author: ywz
 * @createDate: 2021/3/7
 * @description:
 */

@RestController
public class OrderPayedListener {

    @Autowired
    OrderService orderService;

    @Autowired
    AlipayTemplate alipayTemplate;

    /**
     * 支付宝异步通知
     *
     * @param request
     * @return
     */
    @PostMapping("/payed/notify")
    public String handleAlipayed(PayAsyncVo vo, HttpServletRequest request) throws UnsupportedEncodingException, AlipayApiException {
        //只要我们收到了支付宝给我们异步的通知，告诉我们订单支付成功。返回success，支付宝就再也不通知
//        Map<String, String[]> parameterMap = request.getParameterMap();
//        for (String key : parameterMap.keySet()) {
//            String value = request.getParameter(key);
//            System.out.println("参数名:" + key + "==> 参数值:"+value);
//        }


        //验签
        //获取支付宝POST过来反馈信息
        Map<String, String> params = new HashMap<String, String>();
        Map<String, String[]> requestParams = request.getParameterMap();
        for (Iterator<String> iter = requestParams.keySet().iterator(); iter.hasNext(); ) {
            String name = (String) iter.next();
            String[] values = (String[]) requestParams.get(name);
            String valueStr = "";
            for (int i = 0; i < values.length; i++) {
                valueStr = (i == values.length - 1) ? valueStr + values[i]
                        : valueStr + values[i] + ",";
            }
            //乱码解决，这段代码在出现乱码时使用
//            valueStr = new String(valueStr.getBytes("ISO-8859-1"), "utf-8");
            params.put(name, valueStr);
        }

        boolean signVerified = AlipaySignature.rsaCheckV1(params,
                AlipayTemplate.alipay_public_key,
                alipayTemplate.getCharset(),
                alipayTemplate.getSign_type()); //调用SDK验证签名

        if (signVerified) {
            //
            System.out.println("签名验证成功!");
            String result = orderService.handlePayresult(vo);
//        System.out.println("支付宝通知到位了...数据："+parameterMap);
            return result;
        } else {
            System.out.println("签名验证失败");
            return "error";
        }
    }

}
