package com.ljm.entity;

import com.ljm.entity.common.BaseEntity;
import com.ljm.util.DateUtil;
import com.ljm.util.StringUtil;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;
import java.io.Serializable;
import java.util.Date;

@Data
@ToString(callSuper = true)
@Accessors(chain = true)
public class User extends BaseEntity implements Serializable {

    private String uuid;

    /**
     * 用户账号 (唯一)
     * */
    private String userId;

    /**
     * 用户名
     * */
    private String nickname;

    /**
     * 密码密文
     * */
    private String password;

    /**
     * 加密盐（注册账号时随机生成）
     * */
    private String salt;

    /**
     * 密码密文
     * */
    private String email;

    /**
     * 登录状态
     * */
    private Integer state;

    /**
     * 用户角色 manager、user
     * */
    private String role;
    public User(){
    }

    public User(String userId, String nickname, String password, String salt, String role) {
        this.uuid = StringUtil.generateUUID();
        this.userId = userId;
        this.nickname = nickname;
        this.password = password;
        this.salt = salt;
        this.role = role;
        setBaseInfo();
    }

    private void setBaseInfo(){
        this.setCreateTime(DateUtil.getDateString());
        this.setCreateUser("000000");
        this.setUpdateTime(DateUtil.getDateString());
        this.setUpdateUser("000000");
        this.setIsDelete(0);
    }
}
