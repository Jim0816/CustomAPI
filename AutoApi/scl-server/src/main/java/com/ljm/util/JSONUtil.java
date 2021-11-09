package com.ljm.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.ljm.entity.Table;

import java.util.Map;

public class JSONUtil {

    public static <T> T parseMapToBean(Class<T> c, Map map){
        return JSON.parseObject(JSON.toJSONString(map), c);
    }

    public static <T> T parseStringToBean(Class<T> c, String str){
        return JSONObject.parseObject(str, c);
    }

    public static JSONObject parseBeanToJSONObject(Object bean){
        return JSONObject.parseObject(JSONObject.toJSON(bean).toString());
    }
}
