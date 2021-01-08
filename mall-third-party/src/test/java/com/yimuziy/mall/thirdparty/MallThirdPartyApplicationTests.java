package com.yimuziy.mall.thirdparty;

import com.aliyun.oss.OSSClient;
import com.aliyuncs.CommonRequest;
import com.aliyuncs.CommonResponse;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.ecs.model.v20140526.DescribeInstancesRequest;
import com.aliyuncs.ecs.model.v20140526.DescribeInstancesResponse;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.exceptions.ServerException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import com.yimuziy.mall.thirdparty.component.SmsComponent;
import com.yimuziy.mall.thirdparty.util.HttpUtils;
import org.apache.http.HttpResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

@SpringBootTest
class MallThirdPartyApplicationTests {

    @Resource
    OSSClient ossClient;

    @Value("${spring.cloud.alicloud.access-key}")
    private String accessId;

    @Value("${spring.cloud.alicloud.secret-key}")
    private String secret;

    @Autowired
    SmsComponent smsComponent;

    @Test
    public void testSmsComponent() {
        smsComponent.sendCode("17600300108", "code:1234");
    }


    @Test
    public void testSendSms() {
        String host = "http://dingxin.market.alicloudapi.com";
        String path = "/dx/sendSms";
        String method = "POST";
        String appcode = "1222c06c2f29474cb630214a2f14051c";
        Map<String, String> headers = new HashMap<String, String>();
        //最后在header中的格式(中间是英文空格)为Authorization:APPCODE 83359fd73fe94948385f570e3c139105
        headers.put("Authorization", "APPCODE " + appcode);
        Map<String, String> querys = new HashMap<String, String>();
        querys.put("mobile", "17600300108");
        querys.put("param", "code:5789");
        querys.put("tpl_id", "TP1711063");
        Map<String, String> bodys = new HashMap<String, String>();


        try {
            /**
             * 重要提示如下:
             * HttpUtils请从
             * https://github.com/aliyun/api-gateway-demo-sign-java/blob/master/src/main/java/com/aliyun/api/gateway/demo/util/HttpUtils.java
             * 下载
             *
             * 相应的依赖请参照
             * https://github.com/aliyun/api-gateway-demo-sign-java/blob/master/pom.xml
             */
            HttpResponse response = HttpUtils.doPost(host, path, method, headers, querys, bodys);
            System.out.println(response.toString());
            //获取response的body
            //System.out.println(EntityUtils.toString(response.getEntity()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Test
    public void sendSms() {
        DefaultProfile profile = DefaultProfile.getProfile("cn-hangzhou", accessId, secret);
        IAcsClient client = new DefaultAcsClient(profile);

        CommonRequest request = new CommonRequest();
        request.setSysMethod(MethodType.POST);
        request.setSysDomain("dysmsapi.aliyuncs.com");
        request.setSysVersion("2017-05-25");
        request.setSysAction("SendSms");
        request.putQueryParameter("RegionId", "cn-hangzhou");
        request.putQueryParameter("PhoneNumbers", "17600300108");
        request.putQueryParameter("SignName", "阿里云");
        request.putQueryParameter("TemplateCode", "SMS_153055065");
        request.putQueryParameter("TemplateParam", "{\"code\":\"3333\"}");
        try {
            CommonResponse response = client.getCommonResponse(request);
            System.out.println(response.getData());
        } catch (ServerException e) {
            e.printStackTrace();
        } catch (ClientException e) {
            e.printStackTrace();
        }
    }


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
