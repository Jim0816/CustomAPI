package com.ljm.service;

import com.ljm.entity.Table;
import com.ljm.entity.User;

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
}
