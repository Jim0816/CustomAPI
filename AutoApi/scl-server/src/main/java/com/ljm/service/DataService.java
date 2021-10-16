package com.ljm.service;

import com.alibaba.fastjson.JSONObject;
import com.ljm.model.RequestTemplate;
import com.ljm.model.Table;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface DataService {

    /**
     * 查询当前数据库下所有集合（只查询集合本身，没有关联数据）
     * @param
     * @return
     * @author Jim
     */
    List<Map> getCollections(Table table);

    /**
     * 查询当前数据库下所有集合（只查询集合本身，没有关联数据）
     * @param
     * @return
     * @author Jim
     */
    Set<String> getCollectionNames();

    /**
     * 创建集合 ①创建空表 ②存储该表的描述信息
     * @param table 集合基本信息
     * @return
     * @author Jim
     */
    boolean createCollection(Table table);

    /**
     * 删除集合(慎用) ①删除表 ②删除该表的描述信息
     * @param tableName 集合名称
     * @return
     * @author Jim
     */
    boolean dropCollection(String tableName);

}
