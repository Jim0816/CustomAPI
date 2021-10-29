package com.ljm;



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
        Logger logger = LoggerFactory.getLogger(getClass());
        logger.info("======================================info=========================================");
        logger.warn("======================================warn=========================================");
        logger.error("======================================error=========================================");
    }

}
