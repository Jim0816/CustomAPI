package com.ljm.vo;

import lombok.Data;
import lombok.experimental.Accessors;
@Data
@Accessors(chain = true)
public class Field{
    //字段名称
    private String name;

    //字段类型
    private String type;

    //字段是否必须
    private Integer isRequire;

    //字段值是否唯一
    private Integer isUnique;

    //字段最大长度
    private Integer length;

    //字段默认值
    private Object defaultVal;

    //字段备注
    private String remark;
}
