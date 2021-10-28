package com.ljm.entity;

import com.ljm.entity.common.BaseEntity;
import com.ljm.util.StringUtil;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Accessors(chain = true)
public class User extends BaseEntity implements Serializable {

    private String uuid;

    /**
     * 用户名 (唯一)
     * */
    private String username;

    /**
     * 密码密文
     * */
    private String password;

    /**
     * 用户角色 manager、user
     * */
    private String role;

    public User(String username, String password, String role) {
        this.uuid = StringUtil.generateUUID();
        this.username = username;
        this.password = password;
        this.role = role;
        this.setCreateTime(LocalDateTime.now());
        this.setCreateUser("000000");
        this.setUpdateTime(LocalDateTime.now());
        this.setUpdateUser("000000");
        this.setIsDelete(0);
    }
}
