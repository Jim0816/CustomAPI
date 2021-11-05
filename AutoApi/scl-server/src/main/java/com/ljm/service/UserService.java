package com.ljm.service;

import com.ljm.entity.Table;
import com.ljm.entity.User;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

public interface UserService {
    /**
     * 登录验证
     * @param
     * @return
     * @author Jim
     */
    Map<String, Object> login(User user);

    /**
     * 用户离线
     * @param
     * @return
     * @author Jim
     */
    boolean logout(String token, User user);

    /**
     * 将token存入redis,并且更新在线用户列表
     * @param
     * @return
     * @author Jim
     */
    String updateToken(String userId);

    /**
     * 查询指定用户所有信息
     * @param
     * @return
     * @author Jim
     */
    User get(User user);
}
