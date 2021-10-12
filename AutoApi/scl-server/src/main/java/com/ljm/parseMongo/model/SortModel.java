package com.ljm.parseMongo.model;

public class SortModel {

    //排序字段名;ep:age
    private String sortName;

    //排序方向，小于0倒序,ep:1
    private Integer sortWay = 0;

    public String getSortName() {
        return sortName;
    }

    public void setSortName(String sortName) {
        this.sortName = sortName;
    }

    public Integer getSortWay() {
        return sortWay;
    }

    public void setSortWay(Integer sortWay) {
        this.sortWay = sortWay;
    }
}
