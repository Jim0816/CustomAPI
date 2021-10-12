package com.ljm.controller;

import com.alibaba.fastjson.JSONObject;
import com.ljm.common.RequestJSONParser;
import com.ljm.model.API;
import com.ljm.service.APIProviderService;
import com.ljm.service.APIService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@AllArgsConstructor
@RestController
@RequestMapping("service")
public class APIProviderController {
    private APIService apiService;
    private APIProviderService apiProviderService;

    /**
     * 根据API tag查询接口信息，并且为第三方提供接口服务  ====服务类型：新增数据===
     * @param data 实际数据  、 tag 接口标识
     * @return
     * @author Jim
     */
    @PostMapping(value = "/data")
    public String create(@RequestBody String data, String tag) {
        //1.根据tag查找出对应api对象数据
        List<Map> apis = apiService.getApi(tag,null);
        if(apis.size() == 0){
            //当前接口不存在，无法提供服务
            return JSONObject.toJSONString(false);
        }
        Map api = apis.get(0);
        //2.从api对象中获取业务逻辑 即解析API信息
        Map<String,Object> require = (Map<String, Object>) api.get("require");
        Object result = apiProviderService.parseAPI(require, data);
        return JSONObject.toJSONString(result);
    }
}
