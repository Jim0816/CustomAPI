package com.ljm.service.impl;

import com.ljm.entity.User;
import com.ljm.parseMongo.model.FilterModel;
import com.ljm.parseMongo.model.QueryModel;
import com.ljm.service.UserService;
import com.ljm.util.MD5Util;
import com.ljm.util.MongoDBUtil;
import com.ljm.util.TokenUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@AllArgsConstructor
@Service
public class UserServiceImpl implements UserService {

    private MongoDBUtil mongoDBUtil;
    private RedisTemplate redisTemplate;
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
        log.info("登录用户: " + user.getUserId());
        //1.查询当前用户是否存在
        String userId = user.getUserId();
        String password = user.getPassword();
        QueryModel queryModel = new QueryModel(TABLE_NAME, "*", 0, 10);
        List<FilterModel> filters = new ArrayList<>();
        FilterModel filterModel = new FilterModel("userId", userId, "string", "=", "and");
        filters.add(filterModel);
        queryModel.setFilter(filters);
        List<Map> userList = mongoDBUtil.query(queryModel);
        //1.1 用户不存在
        if(userList == null || userList.size() == 0){
            log.info("数据库中不存在当前用户信息："+user.getUserId());
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
            //为用户创建token,存入redis
            Map<String, Object> tokenData = new HashMap<>();
            tokenData.put("userId", userId);//用户id
            Date date = new Date();
            tokenData.put("iat", date.getTime());//生成时间
            tokenData.put("ext",date.getTime()+1000*60*60);//过期时间1小时
            String token = TokenUtil.createToken(tokenData);
            redisTemplate.opsForValue().set("user:" + token, user);
            res.put("info", "登录成功");
            res.put("token", token);
            res.put("result", true);
            log.info("用户登录成功："+user.getUserId());
            return res;
        }

        log.info("输入密码有误："+user.getUserId());
        res.put("info", "密码错误");
        res.put("result", false);

        return res;
    }

    @Override
    public boolean logout(String token, User user) {
        log.info("用户下线: " + user.getUserId());
        return redisTemplate.delete("user:" + token);
    }
}
