package com.ljm.config;

import com.ljm.entity.Role;
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

    @Value("${system.load.path}")
    private String loadPath;

    @Value("${system.load.tables}")
    private String loadTables;

    @Value("${system.user.supermanager.username}")
    private String superManagerUsername;

    @Value("${system.user.supermanager.nickname}")
    private String superManagerNickname;

    @Value("${system.user.supermanager.password}")
    private String superManagerPassword;

    @Value("${system.user.supermanager.role}")
    private String superManagerRole;
    /**
     * springboot启动成功后，先加载系统配置到数据库
     * @author Jim
     */
    @Override
    public void run(ApplicationArguments args) throws Exception {
        loadSystemTable(loadPath, loadTables);
    }


    /**
     * springboot启动成功后，先加载系统级别的表到数据库
     * @author Jim
     */
    public void loadSystemTable(String loadPath, String tableNames) throws IOException {
        String[] tables = tableNames.split(",");
        for(int i=0 ; i<tables.length ; i++){
            String curTableName = tables[i];
            log.info("====================================开始初始化表: "+curTableName);
            //判断表是否已经存在，如果存在则不需要创建
            if(!mongoDBUtil.isExistCollection(curTableName)){
                //当前表不存在，需要创建并且登记
                mongoDBUtil.registerAndCreateCollectionFromProperties(loadPath, curTableName);
            }
        }
        insertInitDataToRoleAndUser();
    }

    /**
     * sys_role、sys_user 创建成功后，需要插入初始数据
     * @author Jim
     */
    public boolean insertInitDataToRoleAndUser(){
        // 1.先查询默认角色和默认用户是否存在



        Role role = new Role();
        role.setId(StringUtil.generateUUID()).setRoleCode(superManagerRole).setRoleName("超级管理员").setMenuPermission("*").setState(1).setIsDelete(0);
        try{
            if(mongoDBUtil.insertDocumentNeedCheckData(role,"sys_role")){
                User user = new User();
                String salt = StringUtil.generateByRandom(6);
                //仿照：前端传递过来的密文
                String webToPlatPassword = MD5Util.encryptFromUserToPass(superManagerPassword);
                //准备存入数据库的密文
                String platToDBPassword = MD5Util.encryptFromWebSecretToDB(webToPlatPassword,salt);
                user.setId(StringUtil.generateUUID()).setUsername(superManagerUsername).setNickname(superManagerNickname).setPassword(platToDBPassword)
                        .setRoleId(role.getId()).setSalt(StringUtil.generateByRandom(6)).setState(1).setIsDelete(0);
                mongoDBUtil.insertDocumentNeedCheckData(user,"sys_user");
            }
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
        return true;
    }


}
