package com.ljm.config;

import com.ljm.entity.Role;
import com.ljm.entity.User;
import com.ljm.service.CommonService;
import com.ljm.service.RoleService;
import com.ljm.service.UserService;
import com.ljm.util.MD5Util;
import com.ljm.util.MongoDBUtil;
import com.ljm.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;


/**
 * 用于启动服务时加载执行动作
 * @author Jim
 */
@Slf4j
@Component
public class ApplicationInitRunner implements ApplicationRunner {

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private MongoDBUtil mongoDBUtil;

    @Autowired
    private CommonService commonService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private UserService userService;

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
    public void run(ApplicationArguments args){
        boolean result = false;
        try{
            result = loadSystemTable(loadTables);
            insertInitDataToRoleAndUser();
        }catch (Exception e){
            e.printStackTrace();
            result = false;
        }

        if(!result){
            log.error("初始化系统表失败,停止启动系统!");
            ConfigurableApplicationContext ctx = (ConfigurableApplicationContext) applicationContext;
            ctx.close();
        }


    }


    /**
     * springboot启动成功后，先加载系统级别的表到数据库
     * @author Jim
     */
    public boolean loadSystemTable(String tableNames) {
        String[] tables = tableNames.split(",");
        for(int i=0 ; i<tables.length ; i++){
            String curTableName = tables[i];
            log.info("===================开始初始化表: "+curTableName + " ===================");
            //判断表是否已经存在，如果存在则不需要创建
            if(!commonService.tableIsExist(curTableName)){
                //当前表不存在，需要创建并且登记
                if(!mongoDBUtil.registerAndCreateCollectionFromProperties(curTableName)){
                    //登记表的元数据信息失败
                    return false;
                }
            }
            log.info("该表已经存在，不需要重复创建！");
        }
        //所有表都创建并且登记成功
        return true;
    }

    /**
     * sys_role、sys_user 创建成功后，需要插入初始数据
     * @author Jim
     */
    public boolean insertInitDataToRoleAndUser(){
        // 1.先查询默认角色和默认用户是否存在
        Role role = new Role();
        String menuPermission = "table,table-list,api,api-list,permission,user-manage";
        role.setId(StringUtil.generateUUID()).setRoleCode(superManagerRole).setRoleName("超级管理员").setMenuPermission(menuPermission).setState(1).setIsDelete(0);
        Set<String> uniqueFields = new HashSet<>();
        uniqueFields.add("id");
        if(roleService.add(role)){
            User user = new User();
            //仿照：前端传递过来的密文
            String webToPlatPassword = MD5Util.encryptFromUserToPass(superManagerPassword);
            //准备存入数据库的密文
            String salt = StringUtil.generateByRandom(6);
            String platToDBPassword = MD5Util.encryptFromWebSecretToDB(webToPlatPassword,salt);
            user.setId(StringUtil.generateUUID()).setUsername(superManagerUsername).setNickname(superManagerNickname).setPassword(platToDBPassword)
                    .setRoleId(role.getId()).setSalt(salt).setState(1).setIsDelete(0);
            return userService.add(user);
        }
        return false;
    }


}
