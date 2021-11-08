package com.ljm.entity;

import com.ljm.entity.common.BaseEntity;
import com.ljm.util.DateUtil;
import com.ljm.util.StringUtil;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Map;

@Data
@ToString(callSuper = true)
@Accessors(chain = true)
public class API implements Serializable {

    private String id;

    /**
     * 接口操作的数据实体(表名)
     * */
    private String tableName;

    /**
     * 接口名称
     * */
    private String name;

    /**
     * 接口注册标识
     * */
    private String tag;

    /**
     * 接口地址
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

    /**
     * 是否删除 (默认0 表示不删除)
     * */
    private Integer isDelete;
}
