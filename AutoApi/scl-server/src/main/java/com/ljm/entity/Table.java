package com.ljm.entity;

import com.alibaba.fastjson.JSONObject;
import com.ljm.entity.common.BaseEntity;
import com.ljm.util.DateUtil;
import com.ljm.util.StringUtil;
import com.ljm.vo.Field;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;
import java.util.List;

@Data
@ToString(callSuper = true)
@Accessors(chain = true)
public class Table{

    private String id;

    /**
     * 数据对象表名
     * */
    private String tableName;

    /**
     * 数据库存储类型 (MongoDB Mysql)
     * */
    private String dbType;

    /**
     * 数据对象描述
     * */
    private String desc;

    /**
     * 集合字段
     * */
    private List<Field> fields;

    /**
     * 是否删除 (默认0 表示不删除)
     * */
    private Integer isDelete;

}
