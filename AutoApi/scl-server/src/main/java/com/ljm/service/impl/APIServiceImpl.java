package com.ljm.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.ljm.model.API;
import com.ljm.parseMongo.SqlMongoDBParser;
import com.ljm.parseMongo.model.FilterModel;
import com.ljm.parseMongo.model.QueryModel;
import com.ljm.service.APIService;
import com.ljm.util.MongoDBUtil;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import lombok.AllArgsConstructor;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.UpdateDefinition;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
@Service
public class APIServiceImpl implements APIService {
    private MongoDBUtil mongoDBUtil;

    private static final String TABLE_NAME ="sys_api";

    @Override
    public Integer addApi(API api) {
        //先查看当前表是否存在，若不存在，则返回提示先创建表结构数据
        return (!mongoDBUtil.isExistCollection(TABLE_NAME)) ? 0 : (mongoDBUtil.insertDocument(api, TABLE_NAME) ? 1 : -1);
    }

    @Override
    public List<Map> getApi(String tag, String createUser) {
        QueryModel queryModel = new QueryModel(TABLE_NAME, "*", 0, 10);
        List<FilterModel> filters = new ArrayList<>();
        if(tag != null && !tag.equals("")){
            FilterModel filterModel = new FilterModel("tag", tag, "string", "=", "and");
            filters.add(filterModel);
        }
        if(createUser != null && !createUser.equals("")){
            FilterModel filterModel = new FilterModel("createUser", createUser, "string", "=", "and");
            filters.add(filterModel);
        }
        queryModel.setFilter(filters);
        return mongoDBUtil.query(queryModel);
    }

    @Override
    public boolean updateApi(List<FilterModel> filters, String updateFields, JSONObject data) {
        UpdateDefinition update = SqlMongoDBParser.parseRequestUpdate(updateFields, data);
        UpdateResult updateResult = mongoDBUtil.updateDoc(TABLE_NAME, SqlMongoDBParser.addFilters(new Query(), filters), update);
        if(updateResult.getModifiedCount() > 0){
            return true;
        }
        return false;
    }

    @Override
    public boolean removeApi(String tag) {
        //query添加过滤条件
        List<FilterModel> filters = new ArrayList<>();
        if(tag != null && !tag.equals("")){
            FilterModel filterModel = new FilterModel("tag", tag, "string", "=", "and");
            filters.add(filterModel);
        }
        DeleteResult deleteResult = mongoDBUtil.removeDoc(TABLE_NAME, SqlMongoDBParser.addFilters(new Query(), filters));
        if(deleteResult.getDeletedCount() > 0){
            return true;
        }
        return false;
    }

    @Override
    public boolean removeApi(List<FilterModel> filters) {
        DeleteResult deleteResult = mongoDBUtil.removeDoc(TABLE_NAME, SqlMongoDBParser.addFilters(new Query(), filters));
        if(deleteResult.getDeletedCount() > 0){
            return true;
        }
        return false;
    }
}
