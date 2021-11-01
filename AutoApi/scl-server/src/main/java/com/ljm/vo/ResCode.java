package com.ljm.vo;

import lombok.AllArgsConstructor;
import lombok.ToString;

/**
 * 公共返回对象枚举
 * */
public enum ResCode {
    //通用
    SUCCESS(200, "SUCCESS"),
    ERROR(500, "服务器异常"),
    OTHER_ERROR(500100, "其他未知异常，错误信息由用户自定义"),

    //登录模块 5002xx
    LOGIN_ERROR(500201, "密码错误"),
    TOKEN_ERROR_INVALID(500202, "token不存在或者token格式有误"),
    TOKEN_ERROR_EXPIRE(500203, "token已经过期"),
    TOKEN_ERROR_EXIT(500204, "用户已经离线，token已被清理");
    private final long code;
    private final String msg;



    private ResCode(final long code, final String msg) {
        this.code = code;
        this.msg = msg;
    }

    public long getCode() {
        return this.code;
    }

    public String getMsg() {
        return this.msg;
    }

    public String toString() {
        return String.format(" ErrorCode:{code=%s, msg=%s} ", this.code, this.msg);
    }
}
