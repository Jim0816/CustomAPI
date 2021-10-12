package com.ljm.parseMongo.model;

/**
 * 条件对象元素  （暂时没有使用）
 */
public class Condition {
    /**
     * 字段名称
     */
    private String fieldName;

    /**
     * 字段操作 eg: like > < = !=
     */
    private String operate;

    /**
     * 条件值
     */
    private Object value;

    public Condition(String fieldName, String operate, Object value) {
        this.fieldName = fieldName;
        this.operate = operate;
        this.value = value;
    }
}
