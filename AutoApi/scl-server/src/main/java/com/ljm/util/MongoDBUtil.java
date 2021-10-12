package com.ljm.util;

import com.alibaba.fastjson.JSONObject;
import com.ljm.parseMongo.SqlMongoDBParser;
import com.ljm.parseMongo.model.QueryModel;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import lombok.AllArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.UpdateDefinition;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Set;

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
     * 创建新集合
     * @param
     * @return
     * @author Jim
     */
    public boolean createCollection(String collectionName) {
        MongoCollection mongoCollection =  mongoTemplate.createCollection(collectionName);
        if(mongoCollection != null)
            return true;
        else
            return false;
    }

    /**
     * 插入文档   （为指定集合加入一个文档）
     * @param
     * @return
     * @author Jim
     */
    public boolean insertDocument(Object data, String collectionName) {
        boolean result = true;
        try{
            mongoTemplate.insert(data, collectionName);
        }catch (Exception e){
            result = false;
            e.printStackTrace();
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
            System.out.println(query);
            //1.4 query添加分页条件
            long totalCount = mongoTemplate.count(query, tableName);
            if (totalCount == 0) {
                return null;
            }
            SqlMongoDBParser.addLimit(query, model);
            //2.执行查询操作
            result = mongoTemplate.find(query, Map.class, tableName);
        }catch (Exception e){
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 删除文档
     * @param
     * @return
     * @author Jim
     */
    public DeleteResult removeDoc(String tableName, Query query){
        return mongoTemplate.remove(query, tableName);
    }

    /**
     * 修改文档
     * @param
     * @return
     * @author Jim
     */
    public UpdateResult updateDoc(String tableName, Query query, UpdateDefinition update){
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


















    /**
     * 单表条件构造
     * @param
     * @return
     * @author Jim
     */
    public Query constructQuery(Map<String, String> conditions) {
        //1.修改条件
        Query query = new Query();
        for (String condition : conditions.keySet()) {
            //条件类型
            String conditionType = conditions.get(condition);
        }
        return null;
    }

    /**
     * 指定集合按照条件修改一个文档
     * @param collectionName 表名 、data 数据 、conditions 条件
     * @return
     * @author Jim
     */
    public boolean updateDocument(String collectionName, String data, Map<String, String> conditions) {
        //1.修改条件
        Query query = new Query();
        for (String condition : conditions.keySet()) {
            //条件类型
            String conditionType = conditions.get(condition);

        }
        return false;
    }


    /**
     * 查询所有集合（表）名称
     * @param
     * @return
     * @author Jim
     */
    public Set<String> allCollections() {
        return mongoTemplate.getCollectionNames();
    }






}
