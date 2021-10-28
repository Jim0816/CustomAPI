package com.ljm.util;

import com.alibaba.fastjson.JSONObject;
import com.ljm.parseMongo.SqlMongoDBParser;
import com.ljm.parseMongo.model.FilterModel;
import com.ljm.parseMongo.model.QueryModel;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import lombok.AllArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.UpdateDefinition;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

@AllArgsConstructor
@Component
public class MongoDBUtil {

    private MongoTemplate mongoTemplate;

    /**
     * 判断集合是否存在mongodb
     * @param
     * @return
     * @author Jim
     */
    public boolean isExistCollection(String tableName) {
        Set<String> tableNames = mongoTemplate.getCollectionNames();
        if(tableNames.contains(tableName)){
            return true;
        }
        return false;
    }

    /**
     * 查询所有集合（表）名称
     * @param
     * @return
     * @author Jim
     */
    public Set<String> getCollectionNames() {
        return mongoTemplate.getCollectionNames();
    }

    /**
     * 创建新集合 (但是仅仅创建集合，不会将结构信息登记到sys_table中)
     * @param
     * @return
     * @author Jim
     */
    public boolean createCollection(String tableName) {
        MongoCollection mongoCollection =  mongoTemplate.createCollection(tableName);
        if(mongoCollection != null)
            return true;
        else
            return false;
    }

    /**
     * 注册model结构信息到sys_table，并且在数据库中创建当前model实体   注意：结构信息从配置文件读取
     * @param
     * @return
     * @author Jim
     */
    public boolean registerAndCreateCollectionFromProperties(String tableName) throws IOException {
        ClassPathResource classPathResource = new ClassPathResource("properties/model.properties");
        Properties properties=new Properties();
        BufferedReader bf = new BufferedReader(new InputStreamReader(classPathResource.getInputStream(),"UTF-8"));//解决读取properties文件中产生中文乱码的问题
        properties.load(bf);
        String value = properties.getProperty(tableName);
        if(value != null && !value.equals("")){
            JSONObject json = JSONObject.parseObject(value);
            if(createCollection(tableName)){
                return insertDocument(json,"sys_table");
            }
        }else{
            System.out.println("==========================表 :"+tableName+"配置信息读取失败，可能不存在配置文件中=====================");
        }
        return false;
    }

    /**
     * 插入文档   （为指定集合加入一个文档）
     * @param
     * @return
     * @author Jim
     */
    public boolean insertDocument(Object data, String tableName) {
        boolean result = true;
        //校验数据是否满足表结构  如当前插入的数据字段不满足当前版本(最新)的表结构信息
        if(SqlMongoDBParser.checkOperateByMetaData(specialQuery(tableName), data)){
            //校验通过
            try{
                mongoTemplate.insert(data, tableName);
                System.out.println("================================mongodb insert sql start======================================");
                System.out.println("表名： "+tableName);
                System.out.println("数据： "+data);
                System.out.println("================================mongodb insert sql end========================================");
            }catch (Exception e){
                result = false;
                e.printStackTrace();
            }
        }else{
            System.out.println("================================数据校验失败，无法插入数据库========================================");
            result = false;
        }
        return result;
    }

    /**
     * 查询文档（单表查询文档）
     * @param model 单表查询对象
     * @return
     * @author Jim
     */
    public List<Map> query(QueryModel model){
        List<Map> result = null;
        String tableName = model.getName();
        try{
            //检查集合是否存在
            if (!mongoTemplate.collectionExists(tableName)) {
                return null;
            }
            //1.封装查询条件
            Query query = new Query();
            //1.1 query添加返回字段
            SqlMongoDBParser.addStys(query, model.getResFields());
            //1.2 query添加过滤条件
            SqlMongoDBParser.addFilters(query, model.getFilter());
            //1.3 query添加排序条件
            SqlMongoDBParser.addSorts(query, model.getSort());
            //1.4 query添加分页条件
            long totalCount = mongoTemplate.count(query, tableName);
            if (totalCount == 0) {
                return null;
            }
            SqlMongoDBParser.addLimit(query, model);
            System.out.println("================================mongodb select sql start======================================");
            System.out.println("表名： "+tableName);
            System.out.println(query);
            System.out.println("================================mongodb select sql end========================================");
            //2.执行查询操作
            result = mongoTemplate.find(query, Map.class, tableName);
        }catch (Exception e){
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 从sys_table表中查询指定表的结构信息
     * @param tableName 指定表名
     * @return
     * @author Jim
     */
    public Map specialQuery(String tableName){
        QueryModel queryModel = new QueryModel("sys_table", "*", 0, 10);
        List<FilterModel> filters = new ArrayList<>();
        FilterModel filterModel = new FilterModel("tableName", tableName, "string", "=", "and");
        filters.add(filterModel);
        queryModel.setFilter(filters);
        List<Map> tableMapList = query(queryModel);
        if(tableMapList.size() > 0){
            Map findTableStructInfo = tableMapList.get(0);
            return findTableStructInfo;
        }
        System.out.println(tableName+ "表的结构信息不存在!!!");
        return null;
    }
    /**
     * 删除文档
     * @param
     * @return
     * @author Jim
     */
    public DeleteResult removeDoc(String tableName, Query query){
        System.out.println("================================mongodb delete sql start======================================");
        System.out.println("表名： "+tableName);
        System.out.println(query);
        System.out.println("================================mongodb delete sql end========================================");
        return mongoTemplate.remove(query, tableName);
    }

    /**
     * 修改文档
     * @param
     * @return
     * @author Jim
     */
    public UpdateResult updateDoc(String tableName, Query query, UpdateDefinition update){
        System.out.println("================================mongodb update sql start======================================");
        System.out.println("表名： "+tableName);
        System.out.println(query);
        System.out.println(update);
        System.out.println("================================mongodb update sql end========================================");
        return mongoTemplate.updateMulti(query, update, tableName);
    }

    /**
     * 清空指定集合
     * @param
     * @return
     * @author Jim
     */
    public void clearCollection(String tableName) {
        Query query = new Query();
        mongoTemplate.remove(query, tableName);
    }

    /**
     * 删除指定集合
     * @param collectionName:集合名称
     * @return
     * @author Jim
     */
    public boolean dropCollection(String collectionName) {
        mongoTemplate.dropCollection(collectionName);
        return true;
    }


}
