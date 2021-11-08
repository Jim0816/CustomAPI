package com.ljm.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.ljm.entity.API;
import com.ljm.entity.Table;
import com.ljm.parseMongo.SqlMongoDBParser;
import com.ljm.parseMongo.model.FilterModel;
import com.ljm.parseMongo.model.QueryModel;
import com.ljm.service.APIService;
import com.ljm.util.MongoDBUtil;
import com.ljm.util.StringUtil;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.UpdateDefinition;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

@Service
public class APIServiceImpl implements APIService {

    @Autowired
    private MongoDBUtil mongoDBUtil;

    @Value("${system.load.path}")
    private String loadPath;

    private static final String TABLE_NAME ="sys_api";

    @Override
    public boolean addApi(API api) throws IOException {
        //先查看当前表是否存在，若不存在，则返回提示先创建表结构数据
        if(mongoDBUtil.isExistCollection(TABLE_NAME)){
            //当前表存在数据库中
            return mongoDBUtil.insertDocument(api, TABLE_NAME);
        }else{
            //当前表不存在数据库中 -> 1.需要创建表2.并且将表的结构信息注册到sys_table
            if(mongoDBUtil.registerAndCreateCollectionFromProperties(loadPath, TABLE_NAME)){
                //表创建成功，插入数据
                return mongoDBUtil.insertDocument(api, TABLE_NAME);
            }else{
                //表创建失败
                return false;
            }
        }
    }

    @Override
    public List<Map> getApi(API api) {
        QueryModel queryModel = new QueryModel(TABLE_NAME, "*", 0, 10);
        List<FilterModel> filters = new ArrayList<>();
        if(api.getTag() != null && !api.getTag().equals("")){
            FilterModel filterModel = new FilterModel("tag", api.getTag(), "string", "=", "and");
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
    public boolean removeApi(API api) {
        //query添加过滤条件
        List<FilterModel> filters = new ArrayList<>();
        if(api.getTableName() != null && !api.getTableName().equals("")){
            FilterModel filterModel = new FilterModel("model", api.getTableName(), "string", "=", "and");
            filters.add(filterModel);
        }

        if(api.getTag() != null && !api.getTag().equals("")){
            FilterModel filterModel = new FilterModel("tag", api.getTag(), "string", "=", "and");
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

    @Override
    public boolean createBaseApis(Table table) throws IOException {
        ClassPathResource classPathResource = new ClassPathResource("properties/api.properties");
        Properties properties=new Properties();
        BufferedReader bf = new BufferedReader(new InputStreamReader(classPathResource.getInputStream(),"UTF-8"));//解决读取properties文件中产生中文乱码的问题
        properties.load(bf);

        API api = new API();
        api.setTableName(table.getTableName());
        api.setPermission(JSONObject.parseObject(properties.getProperty("permission")).getInnerMap());
        //1.基础新增
        api.setRequire(JSONObject.parseObject(properties.getProperty("put_require")).getInnerMap());
        api.setName("基础-新增");
        api.setDesc("基础新增接口");
        api.setId(StringUtil.generateUUID());
        addApi(api);

        //2.基础查询
        api.setRequire(JSONObject.parseObject(properties.getProperty("get_require")).getInnerMap());
        api.setName("基础-查询");
        api.setDesc("基础查询接口,默认分页");
        api.setId(StringUtil.generateUUID());
        addApi(api);

        //3.基础修改
        api.setRequire(JSONObject.parseObject(properties.getProperty("post_require")).getInnerMap());
        api.setName("基础-修改");
        api.setDesc("基础修改接口");
        api.setId(StringUtil.generateUUID());
        addApi(api);

        //4.基础删除
        api.setRequire(JSONObject.parseObject(properties.getProperty("delete_require")).getInnerMap());
        api.setName("基础-删除");
        api.setDesc("基础删除接口");
        api.setId(StringUtil.generateUUID());
        addApi(api);

        return true;
    }

}
