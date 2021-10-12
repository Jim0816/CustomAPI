package com.ljm.common;

import com.ljm.util.StringUtil;
import lombok.AllArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;


/*@AllArgsConstructor
@Component*/
public class MyApplicationRunner implements ApplicationRunner {

    /**
     * springboot启动成功后，先加载系统配置到数据库
     * @author Jim
     */
    @Override
    public void run(ApplicationArguments args) throws Exception {

    }


}
