package com.ljm.vo;

import com.alibaba.fastjson.JSONObject;
import com.ljm.parseMongo.SqlMongoDBParser;
import com.ljm.parseMongo.model.FilterModel;
import com.ljm.parseMongo.model.SortModel;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;
import java.util.Map;

@Data
@Accessors(chain = true)
public class Query {
    /**
     *  表名
     */
    private String tableName;

    /**
     *  操作类型 add update get delete
     */
    private String operateType;

    /**
     *  操作类型
     */
    private List<FilterModel> filter;

    /**
     *  排序规则
     */
    private List<SortModel> sort;

    /**
     *  分页规则
     */
    private Map<String,Integer> limit;

    /**
     *  返回字段列表
     */
    private String returns;

    /**
     *  修改字段列表
     */
    private String update;

    /**
     *  操作数据
     */
    private JSONObject data;

}
