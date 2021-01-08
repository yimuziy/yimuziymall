package com.yimuziy.mall.cart.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author ywz
 * @date 2020/12/24 17:07
 * @description
 */
@ConfigurationProperties(prefix = "yimuziymall.thread")
@Component
@Data
public class ThreadPoolConfigProperties {
    private Integer coreSize;
    private Integer maxSize;
    private Integer keepAliveTime;
}
