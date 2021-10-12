package com.ljm.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@Document("sys_table")
public class Table extends BaseInfo{
    @Id
    private String id;

    /**
     * 数据对象表名
     * */
    private String tableName;

    /**
     * 数据对象描述
     * */
    private String desc;

    /**
     * 集合权限
     * */
    private Map<String,Object> permission;

    /**
     * 集合字段
     * */
    private List<Object> fields;

    public Table addBaseInfo(){
        this.setIsDelete(0);
        this.setTime(LocalDateTime.now());
        this.setCreateUser("000000");
        return this;
    }

}
