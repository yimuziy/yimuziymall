package com.yimuziy.mall.thirdparty;

import com.aliyun.oss.OSSClient;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

@SpringBootTest
class MallThirdPartyApplicationTests {

    @Resource
    OSSClient ossClient ;
    @Test
    public void testUpload() throws FileNotFoundException {
        // 上传文件流。
        InputStream inputStream = new FileInputStream("D:\\桌面\\Spring Boot 笔记+课件\\鼓励商城\\资料源码\\docs\\pics\\0d40c24b264aa511.jpg");
        ossClient.putObject("yimuziy-mall", "嘀嘀嘀 .png", inputStream);

        // 关闭OSSClient。
        ossClient.shutdown();

        System.out.println("上传完成");
    }

}
