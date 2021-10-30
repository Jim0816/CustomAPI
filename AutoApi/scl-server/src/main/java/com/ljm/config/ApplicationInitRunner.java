package com.ljm.config;

import com.alibaba.fastjson.JSONObject;
import com.ljm.entity.User;
import com.ljm.util.MD5Util;
import com.ljm.util.MongoDBUtil;
import com.ljm.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
@Component
public class ApplicationInitRunner implements ApplicationRunner {

    @Autowired
    private MongoDBUtil mongoDBUtil;

    @Value("${manager.id}")
    private String managerId;

    @Value("${manager.nickname}")
    private String managerUserName;

    @Value("${manager.password}")
    private String managerPassword;

    @Value("${manager.role}")
    private String managerRole;
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
                    String salt = StringUtil.generateByRandom(6);
                    String password = MD5Util.encryptFromWebSecretToDB(MD5Util.encryptFromUserToPass(managerPassword),salt);
                    User user = new User(managerId, managerUserName, password, salt, managerRole);
                    String jsonStr = JSONObject.toJSONString(user);
                    JSONObject jsonObject = JSONObject.parseObject(jsonStr);
                    mongoDBUtil.insertDocumentNeedCheckData(jsonObject, "sys_user");
                }
            }
        }
    }


}
