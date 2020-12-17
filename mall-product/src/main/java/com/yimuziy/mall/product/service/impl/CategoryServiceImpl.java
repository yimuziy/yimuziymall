package com.yimuziy.mall.product.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.yimuziy.mall.product.service.CategoryBrandRelationService;
import com.yimuziy.mall.product.vo.Catelog2Vo;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yimuziy.common.utils.PageUtils;
import com.yimuziy.common.utils.Query;

import com.yimuziy.mall.product.dao.CategoryDao;
import com.yimuziy.mall.product.entity.CategoryEntity;
import com.yimuziy.mall.product.service.CategoryService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;


@Service("categoryService")
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, CategoryEntity> implements CategoryService {

    //    @Autowired
//    CategoryDao categoryDao;
//    private Map<String, Object> cache = new HashMap<>();

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    CategoryBrandRelationService categoryBrandRelationService;

    @Autowired
    RedissonClient redisson;

    /**
     * 级联更新所有关联的数据
     *  @CacheEvict： 失效模式
     *  1、同时进行多种缓存操作 @Caching
     *  2、指定删除某个分区写所有的数据 @CacheEvict(value = "category",allEntries = true)
     *  3、存储同一类型的数据，都可以指定成同一个分区。 分区名默认就是缓存的前缀
     *
     * @param category
     */
    //category : key
//    @Caching(evict = { @CacheEvict(value = "category",key = "'getLevel1Categorys'"),
//                        @CacheEvict(value = "category",key = "'getCatalogJson'")})
    @CacheEvict(value = "category",allEntries = true)  //失效模式
//    @CachePut  //双写模式
    @Transactional
    @Override
    public void updateCascade(CategoryEntity category) {
        this.updateById(category);
        categoryBrandRelationService.updateCategory(category.getCatId(), category.getName());

        //同时修改缓存中的数据
        //redis，del（“catalogJSON”）; 等待下次主动查询进行更新
    }

    /**
     *  1、每一个需要缓存的数据我们都来指定要放到那个名字的缓存。  【缓存的分区（按照业务类型分）】
     *  2、@Cacheable({"category"})
     *      代表当前方法的结果需要缓存，如果缓存中有，方法不用调用。
     *      如果缓存中没有，会调用方法，最后将方法的结果放入缓存
     *  3、默认行为
     *      1）、如果缓存中有，方法不用调用。
     *      2）、key默认自动生成； 缓存的名字::SimpleKey []（自主生成的key值）
     *      3）、缓存的value的值。默认使用jdk序列化机制。将序列化后的数据存到redis
     *      4）、默认时间： -1；
     *
     *     自定义：
     *        1）、指定生成的缓存使用的key：  key属性指定。接收一个spEL表达式
     *               spEL的详细: https://docs.spring.io/spring-framework/docs/5.2.12.RELEASE/spring-framework-reference/integration.html#cache-spel-context
     *        2）、指定缓存的数据的存活时间:  配置文件中修改ttl
     *        3）、将数据保存为json格式;
     *               CacheAutoConfiguration
     *               RedisCacheConfiguration
     *  4、Spring-Cache的不足：
     *      1）、读模式：
     *          缓存穿透：查询一个个null数据。  解决：缓存空数据  spring.cache.redis.cache-null-values=true
     *          缓存击穿：大量并发进来同时查询一个正好过期的数据。   解决：加锁（分布式锁） ？ 默认是无加锁的; sync = true(加锁，解决击穿)
     *          缓存雪崩：大量的key同时过期。 解决： 加随机时间。加上过期时间
     *      2）、写模式：（缓存与数据库一致）
     *          1）、读写加锁。
     *          2）、引入Canal,感知到MySQL的更新去更新数据库
     *          3）、读多写多，直接去数据库查询就行
     *     总结：
     *          常规数据（读多写少，即时性，一致性要求不高的数据）；完全可以使用Spring-Cache；写模式（只要缓存的数据有过期时间就足够了）
     *          特殊数据: 特殊设计
     *  原理：
     *      CacheManager(RedisCacheManager) -> Cache(RedisCache) -> Cache负责缓存的读写
     *
     *
     * @return
     */
    @Cacheable(value = {"category"},key = "#root.method.name",sync = true)   //
    @Override
    public List<CategoryEntity> getLevel1Categorys() {
        System.out.println("CategoryServiceImpl.getLevel1Categorys");
        long l = System.currentTimeMillis();
        List<CategoryEntity> categoryEntities = baseMapper.selectList(new QueryWrapper<CategoryEntity>().
                eq("parent_cid", 0));
        return categoryEntities;
    }

    @Cacheable(value = "category",key = "#root.methodName")
    @Override
    public Map<String, List<Catelog2Vo>> getCatalogJson() {

        System.out.println("真正查询了数据库.......");

        List<CategoryEntity> selectList = baseMapper.selectList(null);
        List<CategoryEntity> level1Categorys = getParent_cid(selectList, 0L);

        //2、封装数据
        Map<String, List<Catelog2Vo>> parent_cid = level1Categorys.stream().collect(Collectors.toMap(
                k -> k.getCatId().toString(),
                v -> {
                    //1、每一个的1级分类，查到这个一级分类的二级分类
                    List<CategoryEntity> categoryEntities = getParent_cid(selectList, v.getParentCid());
                    //2、封装上面的的结果
                    List<Catelog2Vo> catelog2Vos = null;
                    if (categoryEntities != null) {
                        catelog2Vos = categoryEntities.stream().map(l2 -> {
                            Catelog2Vo catelog2Vo = new Catelog2Vo(v.getCatId().toString(), null, l2.getCatId().toString(), l2.getName());
                            //1、找当前二级分类的三级分类封装成vo
                            List<CategoryEntity> level3Catelog = getParent_cid(selectList, l2.getParentCid());
                            if (level3Catelog != null) {
                                List<Catelog2Vo.Catelog3Vo> leve3List = level3Catelog.stream().map(l3 -> {
                                    //2、封装成指定的格式
                                    return new Catelog2Vo.Catelog3Vo(l2.getCatId().toString(), l3.getCatId().toString(), l3.getName());
                                }).collect(Collectors.toList());
                                catelog2Vo.setCatalog3List(leve3List);
                            }


                            return catelog2Vo;
                        }).collect(Collectors.toList());
                    }
                    return catelog2Vos;
                }));


        return parent_cid;
    }

    //TODO 产生堆外内存溢出： OutOfDirectMemoryError
    //1）、springBoot2.0 默认使用lettuce作为redis的客户端 。它使用netty进行网络通信。
    //2）、lettuce的bug导致堆外内存溢出 -Xmx300m; netty如果没有指定堆外内存。默认使用-Xms300m
    //  可以通过 -Dio.netty.maxDirectMemory: {} bytes 进行设置
    //解决方案：不能使用 -Dio.netty.maxDirectMemory: {} bytes 只去调大堆外内存。
    //1）、升级lettuce客户端。
    //2）、切换jedis
    // redisTemplate：
    //  lettuce、jedis 操作redis 的底层客户端。Spring 再次封装redisTemplate
//    @Override
    public Map<String, List<Catelog2Vo>> getCatalogJson2() {
        //给缓存中放json字符串，拿出的json字符串，还能逆转为能用的对象类型；【序列化与反序列化】


        /**
         * 1、空结果缓存： 解决缓存穿透
         * 2、设置过期时间（加随机值）：解决缓存雪崩
         * 3、加锁： 解决缓存击穿
         */


        //1、加入缓存逻辑，缓存中存的数据是json字符串。
        //JSON跨语言 跨平台兼容。
        String catalogJSON = redisTemplate.opsForValue().get("catalogJSON");
        if (StringUtils.isEmpty(catalogJSON)) {
            //2、缓存中没有，查询数据库
            //数据库查询完成以后，将数据放在redis中，这是一个原子操作，
            System.out.println("缓存未命中。。。将要查询了数据库。。。。");
            Map<String, List<Catelog2Vo>> catalogJsonFromDb = getCatalogJsonFromDbWithRedissonLock();
            return catalogJsonFromDb;
        }

        System.out.println("缓存命中。。。直接返回");
        //转为指定的对象。
        Map<String, List<Catelog2Vo>> result = JSON.parseObject(catalogJSON, new TypeReference<Map<String, List<Catelog2Vo>>>() {
        });


        return result;
    }


    /**
     * 使用Redisson做分布式锁
     *      问题：缓存里面的数据如何和数据库保持一致
     *          缓存数据一致性
     *          1）、双写模式
     *          2）、失效模式
     * @return
     */
    public Map<String, List<Catelog2Vo>> getCatalogJsonFromDbWithRedissonLock() {

        //1、锁的名字。 锁的粒度，越细越快。
        //所得粒度。具体缓存的式某个数据，11-号商品；   product-11-lock  product-12-lock
        RLock lock = redisson.getLock("catalogJson-json");
        lock.lock();



        Map<String, List<Catelog2Vo>> dataFromDb = null;
        try {
            dataFromDb = getDataFromDb();
        } finally {
            //删除锁
            lock.unlock();
        }
        return dataFromDb;
    }


    /**
     * 使用Redis做分布式锁
     *
     * @return
     */
    public Map<String, List<Catelog2Vo>> getCatalogJsonFromDbWithRedisLock() {

        //1、抢占分布式锁。去redis占坑
        String uuid = UUID.randomUUID().toString();
        Boolean lock = redisTemplate.opsForValue().setIfAbsent("lock", uuid, 300, TimeUnit.SECONDS);
        if (lock) {
            System.out.println("获取分布式锁成功");
            //加锁成功....  执行业务
            //2、设置过期时间，必须和加锁是同步的，保证原子性  如果突然断电！ 加锁失败
//            redisTemplate.expire("lock",30,TimeUnit.SECONDS);
            Map<String, List<Catelog2Vo>> dataFromDb = null;
            try {
                dataFromDb = getDataFromDb();
            } finally {
                //获取对比值，对比成功删除 = 原子操作  lua 脚本解锁
//            String lockValue = redisTemplate.opsForValue().get("lock");
//            if(uuid.equals(lockValue)){
//                //删除我自己的锁
//                redisTemplate.delete("lock");//删除锁
//            }
                String script = "if redis.call(\"get\",KEYS[1]) == ARGV[1]\n" +
                        "then\n" +
                        "    return redis.call(\"del\",KEYS[1])\n" +
                        "else\n" +
                        "    return 0\n" +
                        "end";
                //删除锁
                Long lock1 = redisTemplate.execute(new DefaultRedisScript<Long>(script, Long.class), Arrays.asList("lock"), uuid);
            }
            return dataFromDb;
        } else {
            //加锁失败......重试 sychroized();
            //休眠100ms重试
            System.out.println("获取分布式锁失败。。等待重试");
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return getCatalogJsonFromDbWithRedisLock(); // 自旋的方式
        }
    }

    /**
     * 从数据库查询对象
     *
     * @return
     */
    private Map<String, List<Catelog2Vo>> getDataFromDb() {
        String catalogJSON = redisTemplate.opsForValue().get("catalogJSON");
        if (!StringUtils.isEmpty(catalogJSON)) {
            //如果缓存不为null直接返回
            Map<String, List<Catelog2Vo>> result = JSON.parseObject(catalogJSON, new TypeReference<Map<String, List<Catelog2Vo>>>() {
            });
            return result;
        }
        System.out.println("真正查询了数据库.......");

        List<CategoryEntity> selectList = baseMapper.selectList(null);

        List<CategoryEntity> level1Categorys = getParent_cid(selectList, 0L);

        //2、封装数据
        Map<String, List<Catelog2Vo>> parent_cid = level1Categorys.stream().collect(Collectors.toMap(
                k -> k.getCatId().toString(),
                v -> {
                    //1、每一个的1级分类，查到这个一级分类的二级分类
                    List<CategoryEntity> categoryEntities = getParent_cid(selectList, v.getParentCid());
                    //2、封装上面的的结果
                    List<Catelog2Vo> catelog2Vos = null;
                    if (categoryEntities != null) {
                        catelog2Vos = categoryEntities.stream().map(l2 -> {
                            Catelog2Vo catelog2Vo = new Catelog2Vo(v.getCatId().toString(), null, l2.getCatId().toString(), l2.getName());
                            //1、找当前二级分类的三级分类封装成vo
                            List<CategoryEntity> level3Catelog = getParent_cid(selectList, l2.getParentCid());
                            if (level3Catelog != null) {
                                List<Catelog2Vo.Catelog3Vo> leve3List = level3Catelog.stream().map(l3 -> {
                                    //2、封装成指定的格式
                                    return new Catelog2Vo.Catelog3Vo(l2.getCatId().toString(), l3.getCatId().toString(), l3.getName());
                                }).collect(Collectors.toList());
                                catelog2Vo.setCatalog3List(leve3List);
                            }


                            return catelog2Vo;
                        }).collect(Collectors.toList());
                    }
                    return catelog2Vos;
                }));

        //3、查到的数据放入缓存,将对象转为json放在缓存中
        String s = JSON.toJSONString(parent_cid);
        redisTemplate.opsForValue().set("catalogJSON", s, 1, TimeUnit.DAYS);

        return parent_cid;
    }

    //从数据库查询并封装数据
    public Map<String, List<Catelog2Vo>> getCatalogJsonFromDbWithLocalLock() {

//        //1、如果缓存有就用缓存的
//        Map<String, List<Catelog2Vo>> catelogJson = (Map<String, List<Catelog2Vo>>) cache.get("catalogJson");
//        if (cache.get("catalogJson") == null) {
//            //调用业务 xxx
//            //返回数据有放入缓存
//            cache.put("catalogJson", parent_cid);
//        }

        //只要是同一把锁就能锁住需要这个锁的所有线程
        //1、sychronized（this）: SpringBoot所有的组件在容器中都是单例的
        //TODO 本地锁： synchronized，JUC（Lock），在分布式情况下，想要锁住所有，必须使用分布式锁

        synchronized (this) {
            return getDataFromDb();
        }


    }

    private List<CategoryEntity> getParent_cid(List<CategoryEntity> selectList, Long parent_cid) {
        //return baseMapper.selectList(new QueryWrapper<CategoryEntity>().eq("parent_cid", v.getCatId()));
        List<CategoryEntity> collect = selectList.stream().filter(item -> item.getParentCid().equals(parent_cid)).collect(Collectors.toList());
        return collect;
    }

    //[2,25,225]
    @Override
    public Long[] findCatelogPath(Long catelogId) {
        List<Long> paths = new ArrayList<>();
        List<Long> parentPath = findParentPath(catelogId, paths);

        Collections.reverse(parentPath);


        return (Long[]) parentPath.toArray(new Long[parentPath.size()]);
    }


    private List<Long> findParentPath(Long catelogId, List<Long> paths) {
        //1、手机当前节点id
        paths.add(catelogId);
        CategoryEntity byId = this.getById(catelogId);
        if (byId.getParentCid() != 0) {
            findParentPath(byId.getParentCid(), paths);
        }
        return paths;
    }

    /**
     * 批量删除category
     *
     * @param asList
     */
    @Override
    public void removeMenuByIds(List<Long> asList) {
        //TODO  1、检查当前删除的菜单，是否被别的地方引用


        //逻辑删除
        baseMapper.deleteBatchIds(asList);
    }

    @Override
    public List<CategoryEntity> listWithTree() {
        //1、查出所有分类
        List<CategoryEntity> entities = baseMapper.selectList(null);

        //2、组装成父子的树形结构

        //2、1）、找到所有的一级分类
        List<CategoryEntity> level1Menus = entities.stream().filter(categoryEntity ->
                categoryEntity.getParentCid() == 0
        ).map((menu) -> {
            menu.setChildren(getChildrens(menu, entities));
            return menu;
        }).sorted((menu1, menu2) -> {
            return (menu1.getSort() == null ? 0 : menu1.getSort()) - (menu2.getSort() == null ? 0 : menu2.getSort());
        }).collect(Collectors.toList());


        return level1Menus;
    }

    /**
     * 递归查找所有菜单的子菜单
     *
     * @param root
     * @param all
     * @return
     */
    private List<CategoryEntity> getChildrens(CategoryEntity root, List<CategoryEntity> all) {
        List<CategoryEntity> children = all.stream().filter(categoryEntity -> {
            return categoryEntity.getParentCid() == root.getCatId();
        }).map(categoryEntity -> {
            //1、找到子菜单
            categoryEntity.setChildren(getChildrens(categoryEntity, all));
            return categoryEntity;
        }).sorted((menu1, menu2) -> {
            //2、菜单的排序
            return (menu1.getSort() == null ? 0 : menu1.getSort()) - (menu2.getSort() == null ? 0 : menu2.getSort());
        }).collect(Collectors.toList());
        return children;
    }

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<CategoryEntity> page = this.page(
                new Query<CategoryEntity>().getPage(params),
                new QueryWrapper<CategoryEntity>()
        );

        return new PageUtils(page);
    }

}