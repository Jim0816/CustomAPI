package com.ljm.parseMongo.model;

import java.util.List;

public class QueryModel {
    //表名
    private String name;
    //过滤条件
    private List<FilterModel> filter;
    //返回字段，逗号分隔，*表示全返回,name,age,sex
    private String resFields;
    //排序字段
    private List<SortModel> sort;
    //页码
    private Integer pageNow;
    //页大小
    private Integer pageSize;

    public QueryModel(String name, String resFields, Integer pageNow, Integer pageSize){
        this.name = name;
        this.resFields = (resFields == null || resFields.equals("")) ? "*" : resFields;
        this.pageNow = pageNow == null ? 0 : pageNow;
        this.pageSize = pageSize == null ? 10 : pageSize;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<FilterModel> getFilter() {
        return filter;
    }

    public void setFilter(List<FilterModel> filter) {
        this.filter = filter;
    }

    public String getResFields() {
        return resFields;
    }

    public void setResFields(String resFields) {
        this.resFields = resFields;
    }

    public List<SortModel> getSort() {
        return sort;
    }

    public void setSort(List<SortModel> sort) {
        this.sort = sort;
    }

    public Integer getPageNow() {
        return pageNow;
    }

    public void setPageNow(Integer pageNow) {
        this.pageNow = pageNow;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }
}
