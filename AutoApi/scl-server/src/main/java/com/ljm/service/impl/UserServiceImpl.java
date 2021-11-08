package com.ljm.service.impl;

import com.ljm.entity.User;
import com.ljm.parseMongo.model.FilterModel;
import com.ljm.parseMongo.model.QueryModel;
import com.ljm.service.UserService;
import com.ljm.util.MD5Util;
import com.ljm.util.MongoDBUtil;
import com.ljm.util.TokenUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private MongoDBUtil mongoDBUtil;


    private static final Integer tokenKeepAliveTime = 6;

    private static final String TABLE_NAME ="sys_user";


    @Override
    public Map<String, Object> login(User user) {
        Map<String, Object> res = new HashMap();
        if(user == null){
            res.put("info", "后端接收到的用户为空");
            res.put("result", false);
            log.info("后端接收到的用户为空");
            return res;
        }
        log.info("登录用户: " + user.getUsername());
        //1.查询当前用户是否存在
        String userId = user.getUsername();
        String password = user.getPassword();
        QueryModel queryModel = new QueryModel(TABLE_NAME, "*", 0, 10);
        List<FilterModel> filters = new ArrayList<>();
        FilterModel filterModel = new FilterModel("userId", userId, "string", "=", "and");
        filters.add(filterModel);
        queryModel.setFilter(filters);
        List<Map> userList = mongoDBUtil.query(queryModel);
        //1.1 用户不存在
        if(userList == null || userList.size() == 0){
            log.info("数据库中不存在当前用户信息："+user.getUsername());
            res.put("info", "数据库中不存在当前用户信息");
            res.put("result", false);
            return res;
        }
        //1.2 用户存在
        Map map = userList.get(0);
        String dbPassword = map.get("password").toString();
        String dbSalt = map.get("salt").toString();
        if(MD5Util.encryptFromWebSecretToDB(password, dbSalt).equals(dbPassword)){
            //密文一致 -> 密码验证成功 -> 登录成功
            String token = updateToken(userId);
            res.put("info", "登录成功");
            res.put("token", token);
            res.put("result", true);
            log.info("用户登录成功："+user.getUsername());
            return res;
        }
        log.info("输入密码有误："+user.getUsername());
        res.put("info", "密码错误");
        res.put("result", false);
        return res;
    }

    @Override
    public boolean logout(String token, User user) {
        log.info("用户下线: " + user.getUsername());
        return true;
    }

    @Override
    public String updateToken(String userId) {
        Map<String, Object> tokenData = new HashMap<>();
        tokenData.put("userId", userId);//用户id
        Float keepAlive = Float.valueOf(tokenKeepAliveTime); //单位:小时
        Date date = new Date();
        long startTime = date.getTime();
        long aliveTime = (long) (keepAlive * (60 * 60 * 1000)); //存活时间 keepAlive 个小时 ; 1h = 60 * 60 * 1000 ms
        log.info("用户: " + userId + " token开始时间: " + new Date(startTime) + ",   失效时间: " + new Date(startTime + aliveTime));
        tokenData.put("iat", startTime);//生成时间
        tokenData.put("ext",startTime + aliveTime);//过期时间
        String token = TokenUtil.createToken(tokenData);
        return token;
    }

    @Override
    public User get(User user) {
        QueryModel queryModel = new QueryModel(TABLE_NAME, "*", 0, 10);
        if(user != null){
            List<FilterModel> filters = new ArrayList<>();
            if(user.getUsername() != null && !user.getUsername().equals("")){
                FilterModel filterModel = new FilterModel("userId", user.getUsername(), "string", "=", "and");
                filters.add(filterModel);
            }
            queryModel.setFilter(filters);
        }
        List<Map> userList = mongoDBUtil.query(queryModel);
        if(userList.size() > 0){
            try {
                BeanUtils.populate(user, userList.get(0));
            }catch (Exception e){
                e.printStackTrace();
                log.info("Map转User失败");
            }
            return user;
        }
        return null;
    }
}
