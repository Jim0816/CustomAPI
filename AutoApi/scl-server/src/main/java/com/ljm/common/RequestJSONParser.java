package com.ljm.common;

import com.alibaba.fastjson.JSONObject;
import com.ljm.model.API;
import com.ljm.model.RequestTemplate;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 解析前端json
 * */
public class RequestJSONParser {

    /**
     * 将前端传来的json业务报文进行解析，解析为请求对象
     * @return
     * @author Jim
     */
    public static RequestTemplate parseRequest(String request){
        RequestTemplate requestTemplate = JSONObject.parseObject(request, RequestTemplate.class);
        //加入基础字段
        /*SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        requestTemplate.getData().put("createTime", df.format(new Date()));
        requestTemplate.getData().put("isDelete", 0);*/
        return requestTemplate;
    }

    /**
     * 将前端传来的json转换为API对象 (废弃)
     * @return request 请求json  type api创建类型 0：新对象  1：映射对象
     * @author Jim
     */
    public static API parseToAPI(String request, int type){
        API api = JSONObject.parseObject(request, API.class);
        if(type == 0){
            api.generateInfo();
        }
        return api;
    }


}
