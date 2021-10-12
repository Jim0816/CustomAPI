package com.ljm.model;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;

@Data
public class RequestTemplate {

    private String url;
    private String service;//业务类型
    private JSONObject data;


}
