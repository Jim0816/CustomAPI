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
public class User implements Serializable {

    private String id;

    /**
     * 用户账号 (唯一)
     * */
    private String username;

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
     * 用户角色
     * */
    private String roleId;

    /**
     * 最后一次登录时间
     * */
    private String lastLoginTime;

    /**
     * 是否删除 (默认0 表示不删除)
     * */
    private Integer isDelete;

    /**
     * 是否启用 (默认1 表示启用)
     * */
    private Integer state;

}
