package com.ljm.entity;

import com.ljm.entity.common.BaseEntity;
import com.ljm.util.StringUtil;
import lombok.Data;
import lombok.experimental.Accessors;
import org.omg.CORBA.PUBLIC_MEMBER;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Accessors(chain = true)
public class Table extends BaseEntity implements Serializable {

    private String uuid;
    /**
     * 数据对象表名
     * */
    private String tableName;

    /**
     * 数据对象描述
     * */
    private String desc;

    /**
     * 集合字段
     * */
    private List<Object> fields;



    public Table addBaseInfo(String username){
        this.uuid = StringUtil.generateUUID();
        this.setIsDelete(0);
        this.setCreateTime(LocalDateTime.now());
        this.setCreateUser(username == null || username.equals("") ? "root" : username);
        return this;
    }

}
