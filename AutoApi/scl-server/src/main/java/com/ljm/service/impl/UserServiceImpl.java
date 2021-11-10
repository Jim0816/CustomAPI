package com.ljm.service.impl;

import com.ljm.entity.Token;
import com.ljm.entity.User;
import com.ljm.parseMongo.SqlMongoDBParser;
import com.ljm.parseMongo.model.FilterModel;
import com.ljm.service.CommonService;
import com.ljm.service.UserService;
import com.ljm.util.*;
import com.ljm.vo.BeanField;
import com.ljm.vo.Res;
import com.ljm.vo.ResCode;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@AllArgsConstructor
@Service
public class UserServiceImpl implements UserService {

    private MongoDBUtil mongoDBUtil;

    private CommonService commonService;

    private static final Integer tokenKeepAliveTime = 6;

    private static final String TABLE_NAME ="sys_user";

    private static final String TABLE_NAME_TOKEN ="sys_token";


    @Override
    public boolean add(User user) {
        if(commonService.tableIsExist(TABLE_NAME)){
            //表存在 -> 判断唯一键是否已经存在
            User queryUser = new User();
            //构建查询条件
            queryUser.setUsername(user.getUsername());
            if(get(queryUser) == null){
                //唯一字段不存在存在别的数据中 -> 允许插入
                return mongoDBUtil.insertDocumentNeedCheckData(user,TABLE_NAME);
            }else{
                //插入数据的前提必须表的元数据信息存在，才能满足后期数据插入的一系列校验、格式化操作
                log.info("表："+TABLE_NAME + "中存在当前数据,拒绝重复插入!");
                return false;
            }
        }else{
            //插入数据的前提必须表的元数据信息存在，才能满足后期数据插入的一系列校验、格式化操作
            log.info("表："+TABLE_NAME + "不存在,拒绝插入数据！");
            return false;
        }
    }

    @Override
    public Res login(User user) {
        if(user == null){
            log.info("接收用户数据失败");
            return Res.failed(ResCode.LOGIN_ERROR_3);
            //return new LoginResult(ResCode.LOGIN_ERROR_3, false, "");
        }
        log.info("登录验证,用户: " + user.getUsername());
        //1.查询当前用户是否存在
        User queryUser = new User();
        //构建查询条件
        queryUser.setUsername(user.getUsername());
        User dbUser = null;
        if((dbUser = get(queryUser)) == null){
            //当前用户不存在
            log.info("系统中不存在当前用户");
            return Res.failed(ResCode.LOGIN_ERROR_1);
        }
        //系统存在当前用户，密码校验
        //user中password是前端第一次加密的密文，需要进行二次加密得到
        String platToDBPassword = MD5Util.encryptFromWebSecretToDB(user.getPassword(),dbUser.getSalt());
        //校验密码
        if(platToDBPassword.equals(dbUser.getPassword())){
           //校验成功，准备token
            String token = createToken(dbUser.getId());
            if(token!=null){
                log.info("用户登录成功："+user.getUsername());
                //return new LoginResult(ResCode.LOGIN_SUCCESS, true, token);
                Map<String,Object> data = new HashMap<>();
                data.put("token", token);
                return Res.ok(data, ResCode.LOGIN_SUCCESS);
            }else{
                return Res.failed(ResCode.LOGIN_ERROR_4);
            }
        }
        //校验失败
        return Res.failed(ResCode.LOGIN_ERROR_2);
    }

    @Override
    public boolean logout(String token, User user) {
        log.info("用户下线: " + user.getUsername());
        return true;
    }

    @Override
    public String createToken(String uid) {
        Map<String, Object> tokenData = new HashMap<>();
        tokenData.put("uid", uid);//用户id
        Float keepAlive = Float.valueOf(tokenKeepAliveTime); //单位:小时
        Date date = new Date();
        long startTime = date.getTime();
        long aliveTime = (long) (0.01 * (60 * 60 * 1000)); //存活时间 keepAlive 个小时 ; 1h = 60 * 60 * 1000 ms
        log.info("用户ID: " + uid + " token开始时间: " + new Date(startTime) + ",   失效时间: " + new Date(startTime + aliveTime));
        tokenData.put("iat", startTime);//生成时间
        tokenData.put("ext",startTime + aliveTime);//过期时间
        String token = TokenUtil.createToken(tokenData);
        //保存token到数据库(并发量不是很大，暂时不考虑redis提高速度)
        Token tokenObj = new Token();
        tokenObj.setId(StringUtil.generateUUID()).setUid(uid).setToken(token);
        if(!saveToken(tokenObj)){
            log.info("token已经生成，但是存储失败！");
            return null;
        }
        return token;
    }

    @Override
    public boolean saveToken(Token token) {
        if(commonService.tableIsExist(TABLE_NAME_TOKEN)){
            //表存在 -> 判断唯一键是否已经存在
            Token queryToken = new Token();
            //构建查询条件
            queryToken.setUid(token.getUid());
            if(getToken(queryToken) == null){
                //唯一字段不存在存在别的数据中 -> 允许插入
                return mongoDBUtil.insertDocumentNeedCheckData(token,TABLE_NAME_TOKEN);
            }else{
                //当前用户已经登录，并且token存在数据库 -> 再次登录，进行覆盖
                log.info("当前用户token对象已经存在,准备用最新token覆盖！");
                //update操作
                Set<String> filterFields = new HashSet<>();
                filterFields.add("uid");
                Set<String> updateFields = new HashSet<>();
                updateFields.add("id");
                updateFields.add("token");
                return updateToken(token, filterFields, updateFields);
            }
        }else{
            //插入数据的前提必须表的元数据信息存在，才能满足后期数据插入的一系列校验、格式化操作
            log.info("表："+TABLE_NAME_TOKEN + "不存在,拒绝插入数据！");
            return false;
        }
    }

    @Override
    public Token getToken(Token token) {
        List<Token> res = mongoDBUtil.query(token, TABLE_NAME_TOKEN, 0, 10);
        if(res != null && res.size() > 0) {
            //查询成功，有数据
            if(res.size() == 1){
                //查询成功
                return res.get(0);
            }else if(res.size() > 1){
                log.info("查询到多个数据！");
                return null;
            }
        }
        log.info("没有查询到任何数据！");
        return null;
    }

    @Override
    public boolean updateToken(Token token, Set<String> filterFields, Set<String> updateFields) {
        if(token == null || filterFields == null || filterFields.size() == 0 || updateFields == null || updateFields.size() == 0){
            log.info("修改方法入参要求无效,拒绝修改!");
            return false;
        }
        //筛选条件
        List<FilterModel> filters = new ArrayList<>();
        FilterModel filterModel = new FilterModel("uid", token.getUid(), "string", "=", "and");
        filters.add(filterModel);
        //修改字段
        Update update = new Update();
        //获取数据对象的非空键值对
        Map<String, BeanField> notNullPropertyNames = BeanUtil.getNotNullPropertyNames(token);
        for(String key : updateFields){
            if(!key.equals("uid") && notNullPropertyNames.containsKey(key)){
                BeanField beanField = notNullPropertyNames.get(key);
                update.set(key, beanField.getValue());
            }else{
                log.info("字段: " + key + "的值无效,拒绝修改!");
                return false;
            }
        }
        //执行修改
        UpdateResult updateResult = mongoDBUtil.updateDoc(TABLE_NAME_TOKEN, SqlMongoDBParser.addFilters(new Query(), filters), update);
        if(updateResult.getModifiedCount() > 0){
            return true;
        }
        return false;
    }

    @Override
    public boolean removeToken(Token token) {
        //获取数据对象的非空键值对
        Map<String, BeanField> notNullPropertyNames = BeanUtil.getNotNullPropertyNames(token);
        //筛选条件
        List<FilterModel> filters = new ArrayList<>();
        for(String key : notNullPropertyNames.keySet()){
            if(notNullPropertyNames.containsKey(key)){
                BeanField beanField = notNullPropertyNames.get(key);
                FilterModel filterModel = new FilterModel(key, beanField.getValue().toString(), beanField.getType(), "=", "and");
                filters.add(filterModel);
            }else{
                log.info("字段: " + key + "的值无效,拒绝删除!");
                return false;
            }
        }
        //执行删除
        DeleteResult deleteResult = mongoDBUtil.removeDoc(TABLE_NAME_TOKEN, SqlMongoDBParser.addFilters(new Query(), filters));
        if(deleteResult.getDeletedCount() > 0){
            return true;
        }
        return false;
    }

    @Override
    public User get(User user) {
        List<User> res = mongoDBUtil.query(user, TABLE_NAME, 0, 10);
        if(res != null && res.size() > 0) {
            //查询成功，有数据
            if(res.size() == 1){
                //查询成功
                return res.get(0);
            }else if(res.size() > 1){
                log.info("查询到多个数据！");
                return null;
            }
        }
        log.info("没有查询到任何数据！");
        return null;
    }

    @Override
    public List<User> list(User user) {
        List<User> res = mongoDBUtil.query(user, TABLE_NAME, 0, 100000);
        if(res != null && res.size() > 0){
            return res;
        }
        log.info("没有查询到任何数据，查询失败！");
        return null;
    }


}
