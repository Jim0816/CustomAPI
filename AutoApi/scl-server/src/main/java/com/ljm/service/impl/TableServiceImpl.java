package com.ljm.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.ljm.entity.Table;
import com.ljm.parseMongo.SqlMongoDBParser;
import com.ljm.parseMongo.model.FilterModel;
import com.ljm.parseMongo.model.QueryModel;
import com.ljm.service.TableService;
import com.ljm.util.MongoDBUtil;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.UpdateDefinition;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@AllArgsConstructor
@Service
public class TableServiceImpl implements TableService {

    private MongoDBUtil mongoDBUtil;

    private static final String TABLE_NAME ="sys_table";


    @Override
    public boolean tableIsExist(Table table) {
        return false;
    }

    @Override
    public boolean createTable(Table table) {
        String tableName = table.getTableName();
        //当前表已经存在 || 当前表不存在需要新建
        if(mongoDBUtil.isExistCollection(tableName) || mongoDBUtil.createCollection(tableName)){
            //将当前新建的表记录插入sys_table表存储  (登记新建立表的结构信息到sys_table)
            return mongoDBUtil.insertDocument(table,TABLE_NAME);
        }
        return true;
    }

    @Override
    public boolean updateCollection(List<FilterModel> filters, String updateFields, JSONObject data) {
        UpdateDefinition update = SqlMongoDBParser.parseRequestUpdate(updateFields, data);
        UpdateResult updateResult = mongoDBUtil.updateDoc(TABLE_NAME, SqlMongoDBParser.addFilters(new Query(), filters), update);
        if(updateResult.getModifiedCount() > 0){
            return true;
        }
        return false;
    }

    @Override
    public boolean dropCollection(String tableName) {
        //1.先删除表
        if(mongoDBUtil.dropCollection(tableName)){
            //2.从表结构记录中删除
            Query query = new Query();
            //1.2 query添加过滤条件
            List<FilterModel> filters = new ArrayList<>();
            if(tableName != null && !tableName.equals("")){
                FilterModel filterModel = new FilterModel("tableName", tableName, "string", "=", "and");
                filters.add(filterModel);
            }
            //从sys_table中删除该表tableName的信息
            DeleteResult deleteResult = mongoDBUtil.removeDoc(TABLE_NAME, SqlMongoDBParser.addFilters(query, filters));
            if(deleteResult.getDeletedCount() > 0){
                return true;
            }
        }
        return false;
    }

    @Override
    public List<Map> getTables(Table table) {
        QueryModel queryModel = new QueryModel(TABLE_NAME, "*", 0, 10);
        if(table != null){
            List<FilterModel> filters = new ArrayList<>();
            if(table.getTableName() != null && !table.getTableName().equals("")){
                FilterModel filterModel = new FilterModel("tableName", table.getTableName(), "string", "=", "and");
                filters.add(filterModel);
            }
            queryModel.setFilter(filters);
        }
        List<Map> tableList = mongoDBUtil.query(queryModel);
        return tableList;
    }

    @Override
    public Set<String> getCollectionNames() {
        return mongoDBUtil.getCollectionNames().stream().filter(item -> { return !item.contains("sys_"); }).collect(Collectors.toSet());
    }

}
