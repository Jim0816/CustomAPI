package com.ljm.service.impl;

import com.ljm.entity.Role;

import com.ljm.parseMongo.model.FilterModel;
import com.ljm.parseMongo.model.QueryModel;
import com.ljm.service.RoleService;
import com.ljm.util.BeanUtil;
import com.ljm.util.JSONUtil;
import com.ljm.util.MongoDBUtil;
import com.ljm.vo.BeanField;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

@Slf4j
@AllArgsConstructor
@Service
public class RoleServiceImpl implements RoleService {

    private MongoDBUtil mongoDBUtil;
    private static final String TABLE_NAME ="sys_role";


    @Override
    public boolean add(Role role) {
        //当前表已经存在 || 当前表不存在需要新建并且注册,将当前新建的表记录插入sys_table表存储  (登记新建立表的结构信息到sys_table)
        if(mongoDBUtil.isExistCollection(TABLE_NAME) || mongoDBUtil.registerAndCreateCollectionFromProperties(TABLE_NAME)){
            //表已经存在
            return mongoDBUtil.insertDocumentNeedCheckData(role,TABLE_NAME);
        }else{
            //插入数据的前提必须表的元数据信息存在，才能满足后期数据插入的一系列校验、格式化操作
            log.info("表："+TABLE_NAME + "不存在，或者创建失败，导致无法插入数据！");
            return false;
        }
    }

    @Override
    public boolean addWithCheck(Role role, String uniqueFields) {
        // 1.先查询是否有数据出现过这些唯一字段
        String[] fields = uniqueFields.split(",");
        Map<String, BeanField> notNullKVMap = BeanUtil.getNotNullPropertyNames(role);
        QueryModel queryModel = new QueryModel(TABLE_NAME, "*", 0, 10);
        List<FilterModel> filters = new ArrayList<>();
        for(String key : fields){
            BeanField beanField = null;
            if(notNullKVMap.containsKey(key) && (beanField = notNullKVMap.get(notNullKVMap)) != null){
                //当前均使用 并操作(后期优化)
                FilterModel filterModel = new FilterModel(key, beanField.getValue().toString(), beanField.getType(), "=", "and");
                filters.add(filterModel);
            }
        }
        queryModel.setFilter(filters);
        List<Map> res = mongoDBUtil.query(queryModel);
        if(res != null || res.size() > 0){
            log.info("出现包含唯一字段的数据，拒绝插入！");
            return false;
        }
        // 2.若没出现此类数据，则直接插入
        return add(role);
    }

    /*private List<Role> query(Role role, int pageNow, int pageSize){
        Map<String, BeanField> notNullKVMap = BeanUtil.getNotNullPropertyNames(role);
        QueryModel queryModel = new QueryModel(TABLE_NAME, "*", pageNow, pageSize);
        List<FilterModel> filters = new ArrayList<>();
        for (String key : notNullKVMap.keySet()) {
            BeanField beanField = notNullKVMap.get(notNullKVMap);
            //当前均使用 并操作(后期优化)
            FilterModel filterModel = new FilterModel(key, beanField.getValue().toString(), beanField.getType(), "=", "and");
            filters.add(filterModel);
        }
        queryModel.setFilter(filters);
        List<Map> res = mongoDBUtil.query(queryModel);
        //类型转换
        List<Role> roleList = new LinkedList<>();
        for (Map map : res) {
            roleList.add(JSONUtil.parseMapToBean(Role.class, map));
        }
        return roleList;
    }*/

    @Override
    public Role get(Role role) {
        List<Role> res = mongoDBUtil.query(role, TABLE_NAME, 0, 10);
        if(res != null && res.size() > 0) {
            //查询成功，有数据
            if(res.size() == 1){
                //查询成功
                return res.get(0);
            }else if(res.size() > 1){
                log.info("查询到多个数据，查询失败！");
                return null;
            }
        }
        log.info("没有查询到任何数据，查询失败！");
        return null;
    }

    @Override
    public List<Role> list(Role role) {
        List<Role> res = mongoDBUtil.query(role, TABLE_NAME, 0, 100000);
        if(res != null && res.size() > 0){
            return res;
        }
        log.info("没有查询到任何数据，查询失败！");
        return null;
    }

    @Override
    public List<Role> page(Role role, int pageNow, int pageSize) {
        List<Role> res = mongoDBUtil.query(role, TABLE_NAME, pageNow, pageSize);
        if(res != null && res.size() > 0){
            return res;
        }
        log.info("没有查询到任何数据，查询失败！");
        return null;
    }

    @Override
    public Integer count(Role role) {
        List<Role> res = list(role);
        return (res == null || res.size() == 0) ? 0 : res.size();
    }

    @Override
    public boolean remove(Role role) {
        return false;
    }

    @Override
    public boolean update(Role role) {
        return false;
    }
}
