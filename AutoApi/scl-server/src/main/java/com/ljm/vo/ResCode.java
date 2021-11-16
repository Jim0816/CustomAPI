package com.ljm.vo;

import lombok.AllArgsConstructor;
import lombok.ToString;

/**
 * 公共返回对象枚举
 * */
public enum ResCode {
    //通用
    SUCCESS(200, "SUCCESS", 1),
    ERROR(500, "服务器异常", 0),
    OTHER_ERROR(500100, "其他未知异常，错误信息由用户自定义", 0),

    //用户模块 5002xx
    LOGIN_SUCCESS(500200, "登录成功", 1),
    LOGIN_ERROR_1(500201, "系统不存在该账号", 0),
    LOGIN_ERROR_2(500202, "密码错误", 0),
    LOGIN_ERROR_3(500203, "接受登录用户校验数据失败", 0),
    LOGIN_ERROR_4(500204, "用户校验成功，服务器异常导致token无效", 0),
    CREATE_USER_FAILED(500205, "用户创建失败", 0),
    UPDATE_USER_FAILED(500206, "用户修改失败", 0),
    DELETE_USER_FAILED(500207, "用户删除失败", 0),
    TOKEN_ERROR_INVALID(500202, "token不存在或者token格式有误", 0),
    TOKEN_ERROR_EXPIRE(500203, "token已经过期", 0),
    TOKEN_ERROR_EXIT(500204, "用户已经离线，token已被清理", 0),

    //API模块 5003XX
    CREATE_API_FAILED(500301, "接口创建失败", 0),
    UPDATE_API_FAILED(500302, "接口修改失败", 0);

    private final long code;
    private final String msg;
    private final Integer result;



    ResCode(final long code, final String msg, final Integer result) {
        this.code = code;
        this.msg = msg;
        this.result = result;
    }

    public long getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    public Integer getResult() {
        return result;
    }

}
