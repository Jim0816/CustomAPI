package com.ljm.service;

import com.ljm.entity.Table;
import com.ljm.entity.User;

public interface UserService {
    /**
     * 登录验证
     * @param
     * @return
     * @author Jim
     */
    boolean login(User user);
}
