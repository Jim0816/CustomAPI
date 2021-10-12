package com.ljm.parseMongo.model;

public class FilterModel {
    //字段名,ep:name
    private String key;
    //值,双引号包裹表示字符串，单引号表示日期，其他的系统自行判断；ep:"xiaoming"，'2020-01-01'
    private Object value;
    //值类型 string int date
    private String valueType;
    //操作符，包括 >,<,>=,<=,=,<>,in;ep:=
    private String oper;
    //and / or;ep:and
    private String logOper;

    public FilterModel(String key, String value, String valueType, String oper, String logOper) {
        this.key = key;
        this.value = value;
        this.valueType = valueType;
        this.oper = oper;
        this.logOper = logOper;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getValueType() {
        return valueType;
    }

    public void setValueType(String valueType) {
        this.valueType = valueType;
    }

    public String getOper() {
        return oper;
    }

    public void setOper(String oper) {
        this.oper = oper;
    }

    public String getLogOper() {
        return logOper;
    }

    public void setLogOper(String logOper) {
        this.logOper = logOper;
    }
}
