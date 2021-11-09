package com.ljm.vo;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class BeanField {
    //字段名称
    private String type;

    //字段名称
    private Object value;

    public BeanField(String type, Object value){
        this.type = type;
        this.value = value;
    }
}
