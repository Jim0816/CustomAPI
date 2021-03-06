package com.ljm.service;

import com.alibaba.fastjson.JSONObject;
import com.ljm.entity.Table;
import com.ljm.entity.User;
import com.ljm.parseMongo.model.FilterModel;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface TableService {

    /**
     * 查询当前数据库下所有集合
     * @param
     * @return
     * @author Jim
     */
    List<Table> list(Table table);

    Table get(Table table);

    /**
     * 创建集合
     * @param table 新建集合基本信息
     * @return
     * @author Jim
     */
    boolean add(Table table);

    /**
     * 删除集合
     * @param table
     * @return
     * @author Jim
     */
    boolean remove(Table table);

    /**
     * 修改集合
     * @param table
     * @return
     * @author Jim
     */
    boolean update(Table table);

    /**
     * 创建集合 ①创建空表 ②存储该表的描述信息
     * @param table 新建集合基本信息 tableMetaInfo表示sys_table表的结构信息
     * @return
     * @author Jim
     */
    boolean createTable(Table table);

    /**
     * 修改集合信息
     * @param
     * @return
     * @author Jim
     */
    boolean updateCollection(List<FilterModel> filters, String updateFields, JSONObject data);

    /**
     * 删除集合(删除时要同时删除表和表在sys_table中的元数据) ①删除表 ②删除该表的描述信息
     * @param tableName 集合名称
     * @return
     * @author Jim
     */
    boolean dropCollection(String tableName);

    /**
     * 查询当前数据库下所有集合（只查询集合本身，没有关联数据）
     * @param
     * @return
     * @author Jim
     */
    List<Map> getTables(Table table);

    /**
     * 查询当前数据库下所有集合名称（只查询集合本身，没有关联数据）
     * @param
     * @return
     * @author Jim
     */
    Set<String> getCollectionNames();


}
