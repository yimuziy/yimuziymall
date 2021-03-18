package com.yimuziy.mall.seckill.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

/**
 * @author ywz
 * @date 2020/12/15 19:57
 * @description
 */
@Configuration
public class MyRedissonConfig {


    /**
     * 所有堆Redis的使用都是通过RedissonClient对象
     *
     * @return
     * @throws IOException
     */
    @Bean(destroyMethod = "shutdown")
    public RedissonClient redisson() throws IOException {
        //1、创建配置
        Config config = new Config();
        //rediss:// 来启动SSL连接    redis使用普通连接
        //Redis url should start with redis:// or rediss:// (for SSL connection)
        config.useSingleServer().setAddress("redis://47.103.221.135:6379");

        //2、根据Config创建出RedissonClient示例
        RedissonClient redissonClient = Redisson.create(config);
        return redissonClient;
    }

}
