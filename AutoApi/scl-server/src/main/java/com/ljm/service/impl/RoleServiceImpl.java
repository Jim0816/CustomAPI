package com.ljm.service.impl;

import com.ljm.entity.Role;
import com.ljm.parseMongo.model.FilterModel;
import com.ljm.parseMongo.model.QueryModel;
import com.ljm.service.RoleService;
import com.ljm.util.MongoDBUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@AllArgsConstructor
@Service
public class RoleServiceImpl implements RoleService {

    private MongoDBUtil mongoDBUtil;
    private static final String TABLE_NAME ="sys_role";

    @Override
    public boolean add(Role role) {
        return false;
    }

    @Override
    public boolean addWithCheck(Role role, String checkFields) {
        return false;
    }

    @Override
    public List<Role> get(Role role) {
        QueryModel queryModel = new QueryModel(TABLE_NAME, "*", 0, 10);
        if(role != null){
            List<FilterModel> filters = new ArrayList<>();

            BeanUtils.
            for(){

            }
            if(table.getTableName() != null && !table.getTableName().equals("")){
                FilterModel filterModel = new FilterModel("tableName", table.getTableName(), "string", "=", "and");
                filters.add(filterModel);
            }
            queryModel.setFilter(filters);
        }
        List<Map> tableList = mongoDBUtil.query(queryModel);
        return tableList;
    }

    @Override
    public Integer count(Role role) {
        return null;
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
