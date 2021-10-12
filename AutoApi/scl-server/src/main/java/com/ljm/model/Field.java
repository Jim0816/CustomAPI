package com.ljm.model;

import org.springframework.data.annotation.Id;

import java.util.Date;

public class Field {

    @Id
    private String id;

    /**
     * 字段所属数据对象的表名(其实通过dataId可以查询到，为了减少后期查询工作，先提前预留该字段)
     * */
    private String tableName;

    /**
     * 字段来源（从哪条数据中产生）
     *  参照对应插入数据的主键
     * */
    private String dataId;

    /**
     * 字段名称
     * */
    private String fieldName;

    /**
     * 字段别名
     * */
    private String fieldAlias;

    /**
     * 字段数据类型
     * */
    private String fieldType;

    /**
     * 字段描述
     * */
    private String fieldDesc;

    /**
     * 字段创建时间
     * */
    private Date createTime;

    /**
     * 是否删除
     * */
    private Integer isDelete;


}
