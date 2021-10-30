package com.ljm.service.impl;

import com.ljm.entity.User;
import com.ljm.parseMongo.model.FilterModel;
import com.ljm.parseMongo.model.QueryModel;
import com.ljm.service.UserService;
import com.ljm.util.MD5Util;
import com.ljm.util.MongoDBUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@AllArgsConstructor
@Service
public class UserServiceImpl implements UserService {

    private MongoDBUtil mongoDBUtil;
    private static final String TABLE_NAME ="sys_user";


    @Override
    public boolean login(User user) {
        if(user == null){
            return false;
        }
        //1.查询当前用户是否存在
        String userId = user.getUserId();
        String password = user.getPassword();
        QueryModel queryModel = new QueryModel(TABLE_NAME, "*", 0, 10);
        List<FilterModel> filters = new ArrayList<>();
        FilterModel filterModel = new FilterModel("userId", userId, "string", "=", "and");
        filters.add(filterModel);
        queryModel.setFilter(filters);
        List<Map> userList = mongoDBUtil.query(queryModel);
        if(userList == null || userList.size() == 0){
            log.info("数据库中不存在用户："+user.getUserId());
            return false;
        }
        //用户存在
        Map map = userList.get(0);
        String dbPassword = map.get("password").toString();
        String dbSalt = map.get("salt").toString();
        if(MD5Util.encryptFromWebSecretToDB(password, dbSalt).equals(dbPassword)){
            //密文一致 -> 密码验证成功 -> 登录成功
            return true;
        }
        log.info("用户密码验证失败");
        return false;
    }
}
