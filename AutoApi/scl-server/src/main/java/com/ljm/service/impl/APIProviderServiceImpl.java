package com.ljm.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.ljm.parseMongo.SqlMongoDBParser;
import com.ljm.parseMongo.model.FilterModel;
import com.ljm.parseMongo.model.QueryModel;
import com.ljm.parseMongo.model.SortModel;
import com.ljm.service.APIProviderService;
import com.ljm.util.MongoDBUtil;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import lombok.AllArgsConstructor;
import org.springframework.data.mongodb.core.aggregation.ArrayOperators;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.UpdateDefinition;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
@Service
public class APIProviderServiceImpl implements APIProviderService {
    private MongoDBUtil mongoDBUtil;

    @Override
    public Map<String,Object> analysisApi(Map api) {
        //解析结果
        Map<String,Object> result = new HashMap<>();

        //1.操作对象表名
        String tableName = api.get("model").toString();
        result.put("tableName", tableName);
        //操作要求
        Map<String,Object> require = (Map<String,Object>)api.get("require");
        //2.操作类型
        String operateType = require.get("operate").toString();
        result.put("operateType", operateType);

        //操作条件(难点) put操作无任何操作条件，get、post、delete操作都可能包含filter条件
        Map<String,Object> condition = (Map<String,Object>)require.get("condition");
        if(condition != null && condition.size() > 0){
            //condition不为空
            //3.解析出filter
            Map<String,Object> filtersMap = (Map<String, Object>) condition.get("filter");
            if(filtersMap != null && filtersMap.size() > 0){
                //有filter条件
                result.put("filter", SqlMongoDBParser.parseFilter(filtersMap));
            }else{
                //put操作无filter条件
                result.put("filter", null);
            }

            //4.其他特别条件
            if(operateType.equals("get")){
                //查询操作,还可能有分页、排序、返回字段   待完
                //排序条件
                Map<String,Integer> sortMap = (Map<String, Integer>) condition.get("sort");
                result.put("sort", SqlMongoDBParser.parseSort(sortMap));
                //分页条件
                Map<String,Integer> limitMap = (Map<String, Integer>) condition.get("limit");
                result.put("limit", limitMap);
                //返回字段
                result.put("return", condition.get("return") == null ? "*" : condition.get("return").toString());
            }else if(operateType.equals("post")){
                //更新操作,还可能指定修改字段
                String update = condition.get("update") == null ? "*" : condition.get("update").toString();
                result.put("update", update);
            }

        }

        return result;
    }

    @Override
    public boolean add(Map<String, Object> operateCondition, JSONObject data) {
        return mongoDBUtil.insertDocument(data, operateCondition.get("tableName").toString());
    }

    @Override
    public boolean update(Map<String, Object> operateCondition, JSONObject data) {
        UpdateDefinition update = SqlMongoDBParser.parseRequestUpdate(operateCondition.get("update").toString(), data);
        UpdateResult updateResult = mongoDBUtil.updateDoc(operateCondition.get("tableName").toString(), SqlMongoDBParser.addFilters(new Query(), (List<FilterModel>) operateCondition.get("filter")), update);
        if(updateResult.getModifiedCount() > 0){
            return true;
        }
        return false;
    }

    @Override
    public boolean delete(Map<String, Object> operateCondition) {
        DeleteResult deleteResult = mongoDBUtil.removeDoc(operateCondition.get("tableName").toString(), SqlMongoDBParser.addFilters(new Query(), (List<FilterModel>) operateCondition.get("filter")));
        if(deleteResult.getDeletedCount() > 0){
            return true;
        }
        return false;
    }

    @Override
    public List<Map> get(Map<String, Object> operateCondition) {
        Map<String,Integer> limitMap = (Map<String, Integer>) operateCondition.get("limit");
        Integer pageNow = 0, pageSize = 10;
        if(limitMap != null && limitMap.size() > 0){
            pageNow = limitMap.get("page_now");
            pageSize = limitMap.get("page_size");
        }
        QueryModel queryModel = new QueryModel(operateCondition.get("tableName").toString(), operateCondition.get("return").toString(), pageNow, pageSize);
        queryModel.setFilter((List<FilterModel>) operateCondition.get("filter"));
        queryModel.setSort((List<SortModel>) operateCondition.get("sort"));
        return mongoDBUtil.query(queryModel);
    }
}
