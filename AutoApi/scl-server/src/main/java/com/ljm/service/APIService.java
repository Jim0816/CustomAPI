package com.ljm.service;

import com.alibaba.fastjson.JSONObject;
import com.ljm.entity.API;
import com.ljm.entity.Table;
import com.ljm.parseMongo.model.FilterModel;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface APIService {

    boolean add(API api);

    API get(API api);

    List<API> list(API api);

    boolean update(API api);

    boolean remove(API api);

    /**
     * 根据请求对象,为前端产生一个接口数据
     * @param api 请求对象
     * @return 返回操作结果状态
     * @author Jim
     */
    boolean addApi(API api) throws IOException;

    /**
     * 删除api接口
     * @param
     * @return
     * @author Jim
     */
    boolean removeApi(API api);

    /**
     * 查询API信息
     * @param
     * @return
     * @author Jim
     */
    List<Map> getApi(API api);

    /**
     * 修改api接口
     * @param filters 筛选条件 update 更新字段
     * @return
     * @author Jim
     */
    boolean updateApi(List<FilterModel> filters, String updateFields, JSONObject data);

    /**
     * 删除api接口
     * @param filters 筛选条件
     * @return
     * @author Jim
     */
    boolean removeApi(List<FilterModel> filters);

    /**
     * 统一创建基础接口
     * @param table 需要创建的接口基于哪张表
     * @return
     * @author Jim
     */
    boolean createBaseApis(Table table) throws IOException;


}
