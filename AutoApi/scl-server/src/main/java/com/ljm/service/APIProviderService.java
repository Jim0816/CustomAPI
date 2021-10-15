package com.ljm.service;

import com.alibaba.fastjson.JSONObject;
import com.ljm.model.API;
import org.omg.CORBA.OBJ_ADAPTER;

import java.util.List;
import java.util.Map;

public interface APIProviderService {
    /**
     * 解析接口
     * @param api 带解析的接口对象
     * @return
     * @author Jim
     */
    Map<String,Object> analysisApi(Map api);

    /**
     * 新增
     * @param operateCondition 操作条件
     * @return
     * @author Jim
     */
    boolean add(Map<String,Object> operateCondition, JSONObject data);

    /**
     * 更新
     * @param operateCondition 操作条件
     * @return
     * @author Jim
     */
    boolean update(Map<String,Object> operateCondition, JSONObject data);

    /**
     * 删除
     * @param operateCondition 操作条件
     * @return
     * @author Jim
     */
    boolean delete(Map<String,Object> operateCondition);

    /**
     * 查询
     * @param operateCondition 操作条件
     * @return
     * @author Jim
     */
    List<Map> get(Map<String,Object> operateCondition);
}
