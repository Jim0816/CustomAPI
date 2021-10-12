package com.ljm.model;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BaseInfo {
    /**
     * 创建用户id
     * */
    private String createUser;
    /**
     * 更新时间
     * */
    private LocalDateTime time;
    /**
     * 是否删除（默认 0 不删除）
     * */
    private Integer isDelete;
}
