package com.ljm.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.ljm.parseMongo.SqlMongoDBParser;
import com.ljm.parseMongo.model.FilterModel;
import com.ljm.service.APIProviderService;
import com.ljm.util.MongoDBUtil;
import lombok.AllArgsConstructor;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
@Service
public class APIProviderServiceImpl implements APIProviderService {
    private MongoDBUtil mongoDBUtil;
    @Override
    public Object parseAPI(Map<String, Object> apiRequire, String data) {
        String tableName = apiRequire.get("model").toString();
        String operate = apiRequire.get("operate").toString();
        Map<String, Object> condition = (Map<String, Object>) apiRequire.get("condition");
        switch (operate){
            case "get":
                //查询类型

                for (String key : condition.keySet()) {
                    System.out.println(condition.get(key));
                }
                break;
            case "post":
                //新增类型
                return mongoDBUtil.insertDocument(data, tableName);
            case "put":
                //修改类型
                break;
            case "delete":
                //1.封装查询条件
                Query query = new Query();
                //1.2 query添加过滤条件
                Map<String, Object> requestFilter = (Map<String, Object>) condition.get("filter");
                return mongoDBUtil.removeDoc(tableName, SqlMongoDBParser.addFilters(query, SqlMongoDBParser.parseRequestFilter(requestFilter, JSONObject.parseObject(data))));
            default:
                break;
        }
        return null;
    }
}
