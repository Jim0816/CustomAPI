package com.ljm.controller;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.api.R;
import com.ljm.model.API;
import com.ljm.common.RequestJSONParser;
import com.ljm.parseMongo.model.FilterModel;
import com.ljm.service.APIService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@AllArgsConstructor
@RestController
@RequestMapping("interface")
public class APIController {
    private APIService apiService;

    /**
     * 创建API对象
     * @param request 请求数据
     * @return
     * @author Jim
     */
    @PostMapping(value = "/create")
    public R create(@RequestBody String request) {
        API api = JSONObject.parseObject(request, API.class);
        api.generateInfo();
        Integer result = apiService.addApi(api);
        if(result == 1){
            return R.ok("操作成功");
        }else{
            return R.failed(result == 0 ? "当前实体不存在，请先创建该实体!" : "操作失败");
        }
    }

    /**
     * 查询API对象
     * @param request 请求数据  有效查询字段：tag 、createUser
     * @return
     * @author Jim
     */
    @PostMapping(value = "/get")
    public R get(@RequestBody String request) {
        API api = JSONObject.parseObject(request, API.class);
        List<Map> list = apiService.getApi(api.getTag(),api.getCreateUser());

        Map<String,List<Map>> maps = list.stream().filter(item -> {
            return !item.get("model").toString().contains("sys_");
        }).collect(Collectors.groupingBy(item -> {
            return item.get("model").toString();
        }));

        List<Map> res = new ArrayList<>();
        for (String key : maps.keySet()) {
            Map<String, Object> map = new HashMap<>();
            map.put("tableName", key);
            map.put("apis", maps.get(key));
            res.add(map);
        }
        return R.ok(res);
    }

    /**
     * 删除API对象
     * @param request 请求数据  有效查询字段：tag 、createUser
     * @return
     * @author Jim
     */
    @PostMapping(value = "/remove")
    public boolean remove(@RequestBody String request) {
        API api = JSONObject.parseObject(request, API.class);
        return apiService.removeApi(api.getTag());
    }

    /**
     * 修改API对象
     * @param request 请求数据
     * @return
     * @author Jim
     */
    @PostMapping(value = "/update")
    public boolean update(@RequestBody String request) {
        JSONObject api = JSONObject.parseObject(request);
        String uuid = api.get("uuid").toString();
        List<FilterModel> filters = new ArrayList<>();
        if(uuid != null && !uuid.equals("")){
            FilterModel filterModel = new FilterModel("uuid", uuid, "string", "=", "and");
            filters.add(filterModel);
        }
        return apiService.updateApi(filters, "*", api);
    }
}
