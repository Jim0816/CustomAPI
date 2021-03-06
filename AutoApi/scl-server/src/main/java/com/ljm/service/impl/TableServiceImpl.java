package com.ljm.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.ljm.entity.Table;
import com.ljm.entity.User;
import com.ljm.parseMongo.SqlMongoDBParser;
import com.ljm.parseMongo.model.FilterModel;
import com.ljm.parseMongo.model.QueryModel;
import com.ljm.service.CommonService;
import com.ljm.service.TableService;
import com.ljm.util.BeanUtil;
import com.ljm.util.MongoDBUtil;
import com.ljm.vo.BeanField;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.mongodb.core.query.UpdateDefinition;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@AllArgsConstructor
@Service
public class TableServiceImpl implements TableService {

    private MongoDBUtil mongoDBUtil;

    private CommonService commonService;

    private static final String TABLE_NAME ="sys_table";

    public List<Table> list(Table table) {
        List<Table> res = mongoDBUtil.query(table, TABLE_NAME, 0, 100000);
        if(res != null && res.size() > 0){
            return res;
        }
        log.info("没有查询到任何数据，查询失败！");
        return null;
    }

    @Override
    public Table get(Table table) {
        List<Table> res = mongoDBUtil.query(table, TABLE_NAME, 0, 10);
        if(res != null && res.size() > 0) {
            //查询成功，有数据
            if(res.size() == 1){
                //查询成功
                return res.get(0);
            }else if(res.size() > 1){
                log.info("查询到多个数据！");
                return null;
            }
        }
        log.info("没有查询到任何数据！");
        return null;
    }

    @Override
    public boolean add(Table table) {
        if(commonService.tableIsExist(TABLE_NAME)){
            //表存在 -> 判断唯一键是否已经存在
            Table queryTable = new Table();
            //构建查询条件
            queryTable.setTableName(table.getTableName());
            if(get(queryTable) == null){
                //唯一字段不存在存在别的数据中 -> 允许插入
                return mongoDBUtil.insertDocumentNeedCheckData(table,TABLE_NAME);
            }else{
                //插入数据的前提必须表的元数据信息存在，才能满足后期数据插入的一系列校验、格式化操作
                log.info("表："+TABLE_NAME + "中存在当前数据,拒绝重复插入!");
                return false;
            }
        }else{
            //插入数据的前提必须表的元数据信息存在，才能满足后期数据插入的一系列校验、格式化操作
            log.info("表："+TABLE_NAME + "不存在,拒绝插入数据！");
            return false;
        }
    }

    @Override
    public boolean remove(Table table) {
        if(table != null){
            //筛选条件
            List<FilterModel> filters = new ArrayList<>();
            FilterModel filterModel = new FilterModel("id", table.getId(), "string", "=", "and");
            filters.add(filterModel);
            DeleteResult deleteResult = mongoDBUtil.removeDoc(TABLE_NAME, SqlMongoDBParser.addFilters(new Query(), filters));
            if(deleteResult.getDeletedCount() > 0){
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean update(Table table) {
        if(table != null){
            //筛选条件
            List<FilterModel> filters = new ArrayList<>();
            FilterModel filterModel = new FilterModel("id", table.getId(), "string", "=", "and");
            filters.add(filterModel);
            //修改条件
            Update update = new Update();
            //获取数据对象的非空键值对
            Map<String, BeanField> notNullPropertyNames = BeanUtil.getNotNullPropertyNames(table);
            for(String key : notNullPropertyNames.keySet()){
                if(!key.equals("id")){
                    BeanField beanField = notNullPropertyNames.get(key);
                    update.set(key, beanField.getValue());
                }
            }
            //执行修改
            UpdateResult updateResult = mongoDBUtil.updateDoc(TABLE_NAME, SqlMongoDBParser.addFilters(new Query(), filters), update);
            if(updateResult.getModifiedCount() > 0){
                return true;
            }
        }
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
