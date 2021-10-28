package com.ljm.config;

import com.alibaba.fastjson.JSONObject;
import com.ljm.entity.User;
import com.ljm.util.MongoDBUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import java.io.IOException;


/**
 * 用于启动服务时加载执行动作
 * @author Jim
 */

@Component
public class ApplicationInitRunner implements ApplicationRunner {

    @Autowired
    private MongoDBUtil mongoDBUtil;

    @Value("${manager.username}")
    private String managerUserName;

    @Value("${manager.password}")
    private String managerPassword;
    /**
     * springboot启动成功后，先加载系统配置到数据库
     * @author Jim
     */
    @Override
    public void run(ApplicationArguments args) throws Exception {
        //创建并且登记所有系统表
        String initTableNames = "sys_table,sys_user,sys_api";
        loadSystemTable(initTableNames);
    }


    /**
     * springboot启动成功后，先加载系统级别的表到数据库
     * @author Jim
     */
    public void loadSystemTable(String tableNames) throws IOException {
        String[] tables = tableNames.split(",");
        for(int i=0 ; i<tables.length ; i++){
            String curTableName = tables[i];
            //判断表是否已经存在，如果存在则不需要创建
            if(!mongoDBUtil.isExistCollection(curTableName)){
                //当前表不存在，需要创建并且登记
                mongoDBUtil.registerAndCreateCollectionFromProperties(curTableName);
                //新建的用户信息表需要把管理员账号存进去
                if("sys_user".equals(curTableName)){
                    User user = new User(managerUserName, managerPassword, "manager");
                    String jsonStr = JSONObject.toJSONString(user);
                    JSONObject jsonObject = JSONObject.parseObject(jsonStr);
                    mongoDBUtil.insertDocument(jsonObject, "sys_user");
                }
            }
        }
    }


}
