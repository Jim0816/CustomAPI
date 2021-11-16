package com.ljm.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.ljm.entity.API;
import com.ljm.entity.Table;
import com.ljm.parseMongo.SqlMongoDBParser;
import com.ljm.parseMongo.model.FilterModel;
import com.ljm.parseMongo.model.QueryModel;
import com.ljm.service.APIService;
import com.ljm.service.CommonService;
import com.ljm.util.BeanUtil;
import com.ljm.util.MongoDBUtil;
import com.ljm.util.StringUtil;
import com.ljm.vo.BeanField;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.mongodb.core.query.UpdateDefinition;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

@Slf4j
@Service
public class APIServiceImpl implements APIService {

    @Autowired
    private MongoDBUtil mongoDBUtil;

    @Autowired
    private CommonService commonService;

    private static final String TABLE_NAME ="sys_api";

    @Override
    public boolean add(API api) {
        //1.注册接口对象
        String operateType = (String) api.getRequire().get("operate");
        String tag = api.getTableName() + ":" + operateType + "-" + StringUtil.generateByRandom(6);
        //接口地址
        StringBuffer sb = new StringBuffer("http://127.0.0.1:8081/service/data?tag=");
        sb.append(tag);
        api.setId(StringUtil.generateUUID()).setTag(tag).setUrl(sb.toString()).setIsDelete(0);

        if(commonService.tableIsExist(TABLE_NAME)){
            //表存在 -> 判断唯一键是否已经存在
            API queryAPI = new API();
            //构建查询条件
            queryAPI.setTag(api.getTag());
            if(get(queryAPI) == null){
                //唯一字段不存在存在别的数据中 -> 允许插入
                return mongoDBUtil.insertDocumentNeedCheckData(api,TABLE_NAME);
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
    public API get(API api) {
        List<API> res = mongoDBUtil.query(api, TABLE_NAME, 0, 10);
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
    public List<API> list(API api) {
        List<API> res = mongoDBUtil.query(api, TABLE_NAME, 0, 100000);
        if(res != null && res.size() > 0){
            return res;
        }
        log.info("没有查询到任何数据，查询失败！");
        return null;
    }

    @Override
    public boolean update(API api) {
        if(api != null){
            //筛选条件
            List<FilterModel> filters = new ArrayList<>();
            FilterModel filterModel = new FilterModel("id", api.getId(), "string", "=", "and");
            filters.add(filterModel);
            //修改条件
            Update update = new Update();
            //获取数据对象的非空键值对
            Map<String, BeanField> notNullPropertyNames = BeanUtil.getNotNullPropertyNames(api);
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
    public boolean remove(API api) {
        if(api != null){
            //筛选条件
            List<FilterModel> filters = new ArrayList<>();
            FilterModel filterModel = new FilterModel("id", api.getId(), "string", "=", "and");
            filters.add(filterModel);
            DeleteResult deleteResult = mongoDBUtil.removeDoc(TABLE_NAME, SqlMongoDBParser.addFilters(new Query(), filters));
            if(deleteResult.getDeletedCount() > 0){
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean addApi(API api) throws IOException {
        //先查看当前表是否存在，若不存在，则返回提示先创建表结构数据
        if(mongoDBUtil.isExistCollection(TABLE_NAME)){
            //当前表存在数据库中
            return mongoDBUtil.insertDocument(api, TABLE_NAME);
        }else{
            //当前表不存在数据库中 -> 1.需要创建表2.并且将表的结构信息注册到sys_table
            if(mongoDBUtil.registerAndCreateCollectionFromProperties(TABLE_NAME)){
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
        List<String> permission = new ArrayList<>();
        permission.add("*");
        api.setPermission(permission);
        //1.基础新增
        api.setRequire(JSONObject.parseObject(properties.getProperty("add_require")).getInnerMap());
        api.setName("基础-新增");
        api.setDesc("基础新增接口");
        add(api);

        //2.基础查询
        api.setRequire(JSONObject.parseObject(properties.getProperty("get_require")).getInnerMap());
        api.setName("基础-查询");
        api.setDesc("基础查询接口,默认分页");
        add(api);

        //3.基础修改
        api.setRequire(JSONObject.parseObject(properties.getProperty("update_require")).getInnerMap());
        api.setName("基础-修改");
        api.setDesc("基础修改接口");
        add(api);

        //4.基础删除
        api.setRequire(JSONObject.parseObject(properties.getProperty("delete_require")).getInnerMap());
        api.setName("基础-删除");
        api.setDesc("基础删除接口");
        add(api);

        return true;
    }

}
