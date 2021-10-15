package com.ljm;


import com.alibaba.fastjson.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
public class MyTest {
    @Autowired
    private MongoTemplate mongoTemplate;
    //private MongoDBUtil mongoDBUtil;

    @Test
    public void testMethod() {
        String key = "(name){<>,string,and}";
        String fieldName = key.substring(key.indexOf("(") + 1, key.indexOf(")"));
        String rule = key.substring(key.indexOf("{") + 1, key.indexOf("}"));
        String[] splits = rule.split(",");
        System.out.println(rule);



        /*ClassPathResource classPathResource = new ClassPathResource("properties/model.properties");
        Properties properties=new Properties();
        BufferedReader bf = new BufferedReader(new  InputStreamReader(classPathResource.getInputStream(),"UTF-8"));//解决读取properties文件中产生中文乱码的问题
        properties.load(bf);
        System.out.println(properties.getProperty("table"));
        JSONObject josn = JSONObject.parseObject(properties.getProperty("table"));
        System.out.println(josn);*/
    }

}
