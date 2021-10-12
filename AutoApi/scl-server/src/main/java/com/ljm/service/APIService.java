package com.ljm.service;

import com.alibaba.fastjson.JSONObject;
import com.ljm.model.API;
import com.ljm.parseMongo.model.FilterModel;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.UpdateDefinition;

import java.util.List;
import java.util.Map;

public interface APIService {

    /**
     * 根据请求对象,为前端产生一个接口数据
     * @param api 请求对象
     * @return 返回操作结果状态码 1：表示成功 ; 0：表示当前实体表信息不存在，不执行该操作 ; -1：表示操作失败
     * @author Jim
     */
    Integer addApi(API api);

    /**
     * 查询API信息
     * @param
     * @return
     * @author Jim
     */
    List<Map> getApi(String tag, String createUser);

    /**
     * 修改api接口
     * @param filters 筛选条件 update 更新字段
     * @return
     * @author Jim
     */
    boolean updateApi(List<FilterModel> filters, String updateFields, JSONObject data);

    /**
     * 删除api接口
     * @param
     * @return
     * @author Jim
     */
    boolean removeApi(String tag);

    /**
     * 删除api接口
     * @param filters 筛选条件
     * @return
     * @author Jim
     */
    boolean removeApi(List<FilterModel> filters);

}
