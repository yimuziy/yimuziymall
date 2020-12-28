package com.yimuziy.all.auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

/**
 * 核心原理
 *   1、@EnableRedisHttpSession，导入了RedisHttpSessionConfiguration配置
 *      1、给容器中添加了一个组件
 *          SessionRepository ==> 【RedisIndexedSessionRepository】 ==> redis操作session。 session的增删改查封装类
 *      2、SessionRepositoryFilter ==> Filter： session存储的过滤器; 每隔请求过来都必须经过filter
 *          1、创建的时候，就自动从容器中获取到了SessionRepository‘
 *          2、原始的request。response都被包装。 SessionRepositoryRequestWrapper   SessionRepositoryResponseWrapper
 *          3、以后获取session。request.geSession();
 *          4、wrappedRequest.getSession(); ==> SessionRepository中获取到的
 *
 *   装饰着模式：
 *
 *   自动延期；redis中的数据有过期时间
 *
 */

@EnableRedisHttpSession // 整合Redis作为session存储
@EnableFeignClients
@EnableDiscoveryClient
@SpringBootApplication
public class MallAuthServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(MallAuthServerApplication.class, args);
    }

}
