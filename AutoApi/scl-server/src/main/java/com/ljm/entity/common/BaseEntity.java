package com.ljm.entity.common;

import lombok.Data;
import java.io.Serializable;
import java.util.Date;

@Data
public class BaseEntity implements Serializable {

    /**
     * 创建用户id
     * */
    private String createUser;

    /**
     * 修改用户id
     * */
    private String updateUser;

    /**
     * 创建时间
     * */
    private String createTime;

    /**
     * 修改时间
     * */
    private String updateTime;

    /**
     * 是否删除（默认 0 不删除）
     * */
    private Integer isDelete;

}
