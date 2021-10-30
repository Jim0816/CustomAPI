package com.ljm;



import com.ljm.util.MD5Util;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.junit4.SpringRunner;



@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
public class MyTest {
    @Autowired
    private MongoTemplate mongoTemplate;
    //private MongoDBUtil mongoDBUtil;

    @Test
    public void testMethod() {
        String secret = MD5Util.encryptFromUserToPass("123456");
        System.out.println(secret);
    }

}
