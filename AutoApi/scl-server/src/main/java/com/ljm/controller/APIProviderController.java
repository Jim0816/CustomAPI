package com.ljm.controller;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.api.R;
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
    public R create(@RequestBody String data, String tag) {
        //1.根据tag查找出对应api对象数据
        API api = new API();
        api.setTag(tag);
        //因为tag唯一，所以apis最多只有一个
        List<Map> apis = apiService.getApi(api);
        if(apis.size() > 0){
            //解析当前接口
            Map apiMap = apis.get(0);


        }

        return R.failed("接口不存在");
    }
}
