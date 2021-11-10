package com.ljm.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class Res<T> implements Serializable{
    private static final long serialVersionUID = 1L;
    private long code;
    private T data;
    private String msg;
    private Integer result;

    public Res() {
    }

    public static <T> Res<T> ok(T data) {
        return (Res<T>) restResult(data, ResCode.SUCCESS.getCode(), ResCode.SUCCESS.getMsg(), ResCode.SUCCESS.getResult());
    }

    public static <T> Res<T> ok(T data, ResCode resCode) {
        return (Res<T>) restResult(data, resCode.getCode(), resCode.getMsg(), resCode.getResult());
    }
    //未定的异常
    public static <T> Res<T> failed(String msg) {
        return (Res<T>) restResult((Object)null, ResCode.OTHER_ERROR.getCode(), msg, ResCode.OTHER_ERROR.getResult());
    }

    //已定义的异常
    public static <T> Res<T> failed(ResCode resCode) {
        return (Res<T>) restResult((Object)null, resCode.getCode(), resCode.getMsg(), resCode.getResult());
    }


    private static <T> Res<T> restResult(T data, long code, String msg, Integer result) {
        Res<T> apiResult = new Res();
        apiResult.setCode(code);
        apiResult.setData(data);
        apiResult.setMsg(msg);
        apiResult.setResult(result);
        return apiResult;
    }

}
