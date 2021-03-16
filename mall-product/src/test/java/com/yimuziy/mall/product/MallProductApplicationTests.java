package com.yimuziy.mall.product;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yimuziy.mall.product.dao.AttrGroupDao;
import com.yimuziy.mall.product.dao.SkuSaleAttrValueDao;
import com.yimuziy.mall.product.entity.BrandEntity;
import com.yimuziy.mall.product.service.BrandService;
import com.yimuziy.mall.product.service.CategoryService;
import com.yimuziy.mall.product.vo.SkuItemSaleAttrVo;
import com.yimuziy.mall.product.vo.SpuItemAttrGroupVo;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.util.Arrays;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;
import java.util.UUID;

//@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
class MallProductApplicationTests {

    @Resource
    BrandService brandService;

    @Autowired
    CategoryService categoryService;

    @Autowired
    StringRedisTemplate stringRedisTemplate;

    @Autowired
    RedissonClient redissonClient;

    @Autowired
    AttrGroupDao attrGroupDao;

    @Autowired
    SkuSaleAttrValueDao skuSaleAttrValueDao;

    @Test
    public void test02() {
        List<SkuItemSaleAttrVo> saleAttrsBySpuId = skuSaleAttrValueDao.getSaleAttrsBySpuId(22L);
        saleAttrsBySpuId.forEach(System.out::println);
    }

    @Test
    public void test() {
        List<SpuItemAttrGroupVo> spuItemAttrGroupVos = attrGroupDao.etattrGroupWithAttrsBySpuId(22L, 225L);
        for (SpuItemAttrGroupVo spuItemAttrGroupVo : spuItemAttrGroupVos) {
            System.out.println(spuItemAttrGroupVo);
        }
    }

    @Test
    public void redisson() {
        System.out.println(redissonClient);
    }


    @Test
    public void testStringRedisTemplate() {
        //hello world
        ValueOperations<String, String> ops = stringRedisTemplate.opsForValue();

        //保存
        ops.set("hello", "world_" + UUID.randomUUID().toString());

        //查询
        String hello = ops.get("hello");
        System.out.println("之前保存的数据是: " + hello);
    }


    @Test
    public void testFindPath() {
        Long[] catelogPath = categoryService.findCatelogPath(165L);
        log.info("完整路径:{}", Arrays.asList(catelogPath));
    }


//    @Resource
//    OSS ossClient ;
//    @Test
//    public void testUpload() throws FileNotFoundException {
////        // Endpoint以杭州为例，其它Region请按实际情况填写。
////        String endpoint = "http://oss-cn-beijing.aliyuncs.com";
////        // 云账号AccessKey有所有API访问权限，建议遵循阿里云安全最佳实践，创建并使用RAM子账号进行API访问或日常运维，请登录 https://ram.console.aliyun.com 创建。
////        String accessKeyId = "LTAIWEJQ1LXWmrhL";
////        String accessKeySecret = "IgAenHTLteZSYl";
////
////        // 创建OSSClient实例。
////        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
//
//        // 上传文件流。
//        InputStream inputStream = new FileInputStream("D:\\桌面\\Spring Boot 笔记+课件\\鼓励商城\\资料源码\\docs\\pics\\0d40c24b264aa511.jpg");
//        ossClient.putObject("yimuziy-mall", "QQ图片.png", inputStream);
//
//        // 关闭OSSClient。
//        ossClient.shutdown();
//
//        System.out.println("上传完成");
//    }

    @Test
    void contextLoads() {

//        BrandEntity brandEntity = new BrandEntity();
//        brandEntity.setBrandId(1L);
//        brandEntity.setDescript("华为");

//        brandEntity.setName("华为");
//        brandService.save(brandEntity);
//        System.out.println("保存成功。。");

//        brandService.updateById(brandEntity);

        List<BrandEntity> list = brandService.list(new QueryWrapper<BrandEntity>().eq("brand_id", 1));
        list.forEach((item) -> {
            System.out.println(item);
        });
    }

}
