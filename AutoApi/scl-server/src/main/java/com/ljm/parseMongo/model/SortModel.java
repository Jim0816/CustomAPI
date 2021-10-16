package com.ljm.parseMongo.model;

public class SortModel {

    //排序字段名;ep:age
    private String sortName;

    //排序方向，小于0倒序,ep:1升序  -1降序
    private Integer sortWay = 0;

    public SortModel(String sortName, Integer sortWay) {
        this.sortName = sortName;
        this.sortWay = sortWay;
    }

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
