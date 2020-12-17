package com.yimuziy.mall.product.web;

import com.yimuziy.mall.product.entity.CategoryEntity;
import com.yimuziy.mall.product.service.CategoryService;
import com.yimuziy.mall.product.vo.Catelog2Vo;
import org.redisson.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @author ywz
 * @date 2020/12/12 19:34
 * @description
 */
@Controller
public class IndexController {

    @Autowired
    CategoryService categoryService;

    @Autowired
    RedissonClient redisson;

    @Autowired
    StringRedisTemplate redisTemplate;

    @GetMapping({"/", "/index.html"})
    public String indexPage(Model model) {

        //TODO 1、查出所有的1级分类
        List<CategoryEntity> categoryEntities = categoryService.getLevel1Categorys();

        //视图解析器进行拼串
        //"classpath:/templates/"  +  返回值 +  ".html"
        model.addAttribute("categorys", categoryEntities);
        return "index";
    }

    //index/catalog.json
    @ResponseBody
    @GetMapping("/index/catalog.json")
    public Map<String, List<Catelog2Vo>> getCatalogJson() {

        Map<String, List<Catelog2Vo>> catalogJson = categoryService.getCatalogJson();

        return catalogJson;
    }

    @ResponseBody
    @GetMapping("/hello")
    public String hello() {
        //1、获取一把锁，只要锁的名字一样，就是同一把锁
        RLock lock = redisson.getLock("my-lock");

        //2、加锁
        lock.lock();  //阻塞式等待。 默认加的锁都是30s时间
        //1）、锁的自动续期，如果业务超长，运行期间自动给锁续上新的30s。不用担心业务时间长，锁自动过期被删除
        //2）、加锁的业务只要运行完成，就不会给当前续期，即时不手动解锁，锁默认在30s以后自动删除

//        lock.lock(10, TimeUnit.SECONDS);//10秒自动解锁，自动解锁时间一定要大于业务的执行时间。
        //问题： lock.lock(10, TimeUnit.SECONDS); 在锁时间到了以后，不会自动续期。
        //1、如果我们传递了锁的超时时间，就发送给redis执行脚本，进行占锁，默认超时就是我们指定的时间
        //2、如果我们未指定锁的超时间，就使用30 * 1000【lockWatchdogTimeout看门狗的默认事件】；
        //  只要占锁成功，就会启动一个定时任务 【重新给锁设置过期事件，新的过期事件就是看门狗的默认时间】，每隔10s会自动再次续期，续成30s
        // 【internalLockLeaseTime 看门狗时间】 / 3 ， 10s

        //最佳实战
        //1）、lock.lock(10, TimeUnit.SECONDS); 省掉了续期操作。手动解锁
        try {
            System.out.println("加锁成功，执行业务...." + Thread.currentThread().getId());
            Thread.sleep(25000);
        } catch (Exception e) {

        } finally {
            //3、解锁 假设解锁代码没有运行，redisson会不会出现死锁
            lock.unlock();
        }

        return "hello";
    }


    //保证一定能读到最新数据，修改期间，写锁是一个排他锁（互斥锁、独享锁）。读锁式一个共享锁
    //写锁没释放读就必须等待
    // 读 + 读： 相当于无锁，并发读，只会在redis 收纳柜记录好，所有当前的读锁。他们都会同时加锁成功
    // 写 + 读： 等待写锁释放
    // 写 + 写： 阻塞方式
    // 读 + 写： 有读锁。写也需要等待。
    // 只要有写的存在，都必须等待
    @GetMapping("/write")
    @ResponseBody
    public String writeValue() {
        RReadWriteLock lock = redisson.getReadWriteLock("rw-lock");
        String s = "";
        RLock rLock = lock.writeLock();
        try {
            //1、改数据加写锁，读数据加读锁
            rLock.lock();
            s = UUID.randomUUID().toString();
            Thread.sleep(30000);
            redisTemplate.opsForValue().set("writeValue", s);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            rLock.unlock();
        }

        return s;
    }

    @GetMapping("/read")
    @ResponseBody
    public String readValue() {
        RReadWriteLock lock = redisson.getReadWriteLock("rw-lock");
//        new ReentrantReadWriteLock();
        //加读锁
        RLock rLock = lock.readLock();
        String s = "";
        try {
            rLock.lock();
            s = redisTemplate.opsForValue().get("writeValue");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            rLock.unlock();
        }

        return s;
    }

    /**
     * 车库停车，
     * 3车位
     * 信号量也可以用作分布式限流；
     */
    @GetMapping("/park")
    @ResponseBody
    public String park() throws InterruptedException {
        RSemaphore park = redisson.getSemaphore("park");
//        park.acquire(); //获取一个信号，获取一个值,占一个车位
        boolean b = park.tryAcquire();
        if(b){
            //执行业务
        }else {
            return "error";
        }

        return "ok =>"+b;
    }

    @GetMapping("/go")
    @ResponseBody
    public String go() throws InterruptedException {
        RSemaphore park = redisson.getSemaphore("park");
        park.release(); //释放一个车位


        return "ok";
    }




    /**
     * 放假，锁门
     * 1班没人了，2版没人了。。。。。
     * 5个班全部走完了，我们可以锁大门
     *
     */
    @GetMapping("/lockDoor")
    @ResponseBody
    public String lookDoor() throws InterruptedException {
        RCountDownLatch door = redisson.getCountDownLatch("door");
        door.trySetCount(5);

        door.await(); //等待闭锁都完成

        return "放假了";
    }

    @ResponseBody
    @GetMapping("/gogogo/{id}")
    public String gogogo(@PathVariable("id")Long id){
        RCountDownLatch door = redisson.getCountDownLatch("door");
        door.countDown(); //计数减一;

        //CountDownLatch

        return id + "班的人都走了....";
    }
}
