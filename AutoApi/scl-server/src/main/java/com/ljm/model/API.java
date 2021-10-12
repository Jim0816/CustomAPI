package com.ljm.model;

import com.ljm.util.StringUtil;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;

@Data
public class API extends BaseInfo{

    private String uuid;

    /**
     * 接口操作的数据实体(表名)
     * */
    private String model;

    /**
     * 接口操作的数据实体(表名)
     * */
    private String name;

    /**
     * 接口注册标识
     * */
    private String tag;

    /**
     * 接口功能描述
     * */
    private String url;

    /**
     * 接口功能描述
     * */
    private String desc;

    /**
     * 接口权限
     * */
    private Map<String,Object> permission;

    /**
     * 接口要求
     * */
    private Map<String,Object> require;


    /*public API(String desc, String createUser, Map<String,Object> permission, Map<String,Object> require){
        this.setCreateUser(createUser);
        this.setIsDelete(0);
        this.setTime(LocalDateTime.now());
        this.desc = desc;
        this.permission = permission;
        this.require = require;
    }*/

    public void generateInfo(){
        this.setIsDelete(0);
        this.setTime(LocalDateTime.now());
        this.uuid = StringUtil.generateUUID();
        this.tag = StringUtil.generateByRandom(10);
        StringBuilder sb = new StringBuilder("127.0.0.1:8081/service/data?tag=");
        sb.append(this.tag);
        this.url = sb.toString();
    }


}
