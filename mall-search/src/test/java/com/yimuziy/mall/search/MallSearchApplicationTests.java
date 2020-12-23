package com.yimuziy.mall.search;


import com.alibaba.fastjson.JSON;
import com.yimuziy.mall.search.config.MallElasticConfig;
import lombok.Data;
import lombok.ToString;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;

import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.Avg;
import org.elasticsearch.search.aggregations.metrics.AvgAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.Map;


@RunWith(SpringRunner.class)
@SpringBootTest
public class MallSearchApplicationTests {

    @Autowired
    private RestHighLevelClient client;


    @ToString
    @Data
    static class Account {

         private int account_number;
         private int balance;
         private String firstname;
         private String lastname;
         private int age;
         private String gender;
         private String address;
         private String employer;
         private String email;
         private String city;
         private String state;
     }

    /**
     *  {
     *      skuId:1,
     *      spuId:11
     *      skuTitle:华为xx
     *      price：998
     *      saleCount：99
     *      attrs:[
     *          {尺寸： 5村},
     *          {CPU： 高通945},
     *          {分辨率：全高清}
     *      ]
     *  }
     *
     * 冗余：
     *      100万*20 = 1000000*2kb = 2000MB = 2G 20
     * (2)、
     *      sku索引{
     *          skuId:1
     *          spuId:11
     *          xxxxx
     *      }
     *
     *      attr索引{
     *          spuId:11,
     *          attrs:{
     *              {尺寸： 5村},
     *              {CPU： 高通945},
     *              {分辨率：全高清}
     *          }
     *      }
     *
     *  搜索小米： 粮食，手机，电器。
     *  10000个，4000个spu
     *  分布，4000个spu对应的所有可能属性
     *  esClient: spuId:[4000spuid]  4000*8=32000byte = 32kb
     *
     *  32kb*10000 = 320mb;
     *
     * @throws IOException
     */
    @Test
    public void searchData() throws IOException{
        //1、创建检索请求
        SearchRequest searchRequest = new SearchRequest();
        //指定索引
        searchRequest.indices("bank");
        //指定DSL
        //SearchSourceBuilder  sourceBuilder 封装的条件
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        searchRequest.source(sourceBuilder);
        //1.1）、构造检索条件
//        sourceBuilder.query();
//        sourceBuilder.from();
//        sourceBuilder.size();
//        sourceBuilder.aggregations();
        sourceBuilder.query(QueryBuilders.matchQuery("address", "mill"));


        //1.2)、按照年龄值分布进行聚合
        TermsAggregationBuilder ageAgg = AggregationBuilders.terms("ageAgg").field("age").size(10);
        sourceBuilder.aggregation(ageAgg);


        //1.3)、计算平均薪资
        AvgAggregationBuilder balanceAvg = AggregationBuilders.avg("balanceAvg").field("balance");
        sourceBuilder.aggregation(balanceAvg);


        System.out.println("检索条件" + sourceBuilder.toString());
        searchRequest.source(sourceBuilder);



        //2、执行检索
        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);

        //3、分析结果SearchResponse
        System.out.println(searchResponse.toString());
//        JSON.parseObject(searchRequest.toString(), Map.class);
        //3.1)、获取所有查到的数据
        SearchHits hits = searchResponse.getHits();
        SearchHit[] searchHits = hits.getHits();
        for (SearchHit hit : searchHits) {
            /**
             *         "_index" : "bank",
             *         "_type" : "account",
             *         "_id" : "1",
             *         "_score" : 1.0,
             *         "_source" : {
             */
//            hit.getIndex();hit.getType();hit.getId();
            String sourceAsString = hit.getSourceAsString();
            Account account = JSON.parseObject(sourceAsString, Account.class);
            System.out.println(account);


        }

        //3.2)、获取这次检索到的分析信息;
        Aggregations aggregations = searchResponse.getAggregations();
//        for (Aggregation aggregation : aggregations.asList()) {
//            System.out.println("当前聚合:"+aggregation.getName());
//            //aggregation.get
//
//        }
       Terms ageAgg1 = aggregations.get("ageAgg");
        for (Terms.Bucket bucket : ageAgg1.getBuckets()) {
            System.out.println("年龄:"+bucket.getKeyAsString());
        }

        Avg balanceAvg1 = aggregations.get("balanceAvg");
        System.out.println("平均薪资: "+balanceAvg1.getValue());


//        Aggregation balanceAvg2 = aggregations.get("balanceAvg");
    }
    /**
     * 测试存储数据到es
     * 更新也可以
     */
    @Test
    public void indexData() throws IOException {
        IndexRequest indexRequest = new IndexRequest("users");
        indexRequest.id("1");   //数据的id
//        indexRequest.source("userName","zhangsan","age","18","gender","男");
        User user = new User();
        user.setUserName("张三");
        user.setAge(18);
        user.setGender("男");
        String s = JSON.toJSONString(user);
        indexRequest.source(s, XContentType.JSON); //要保存的内容

        //执行操作
        IndexResponse index = client.index(indexRequest, MallElasticConfig.COMMON_OPTIONS);

        //提取有用的响应数据
        System.out.println(index);

    }

    @Test
    public void getData() throws IOException {
        GetRequest getRequest = new GetRequest("users","1");

        GetResponse documentFields = client.get(getRequest, RequestOptions.DEFAULT);
        String sourceAsString = documentFields.getSourceAsString();
        System.out.println(sourceAsString);

    }

    @Data
    class User{
        private String userName;
        private String gender;
        private Integer age;
    }

    @Test
    public void contextLoads() {
        System.out.println(client);

    }

}
