package com.ljm.controller;

import com.alibaba.fastjson.JSONObject;
import com.ljm.entity.API;
import com.ljm.parseMongo.model.FilterModel;
import com.ljm.service.APIService;
import com.ljm.service.DataService;
import com.ljm.vo.Res;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@AllArgsConstructor
@RestController
@RequestMapping("interface")
public class APIController {
    private APIService apiService;
    private DataService dataService;

    /**
     * 创建API对象
     * @param request 请求数据
     * @return
     * @author Jim
     */
    @PostMapping(value = "/create")
    public Res create(@RequestBody String request) throws IOException {
        API api = JSONObject.parseObject(request, API.class);
        api.generateInfo();
        boolean result = apiService.addApi(api);
        if(result){
            return Res.ok("操作成功");
        }else{
            return Res.failed("操作失败");
        }
    }

    /**
     * 查询API对象
     * @param request 请求数据  有效查询字段：tag 、createUser
     * @return
     * @author Jim
     */
    @PostMapping(value = "/get")
    public Res get(@RequestBody String request) {
        API api = JSONObject.parseObject(request, API.class);
        //查询出所有满足条件的api
        List<Map> res = new ArrayList<>();
        List<Map> list = apiService.getApi(api);
        Map<String,List<Map>> maps = null;
        if(list != null && list.size() > 0){
            //1.先过滤掉系统级别表（系统表sys开头，不参与api操作） 2.按照表名对api进行分类
            maps = list.stream().filter(item -> {
                return !item.get("model").toString().contains("sys_");
            }).collect(Collectors.groupingBy(item -> {
                return item.get("model").toString();
            }));
        }

        //此时maps为 <表名，当前表下满足条件的所有api>
        Set<String> tableNames = dataService.getCollectionNames();
        for (String key : tableNames) {
            Map<String, Object> map = new HashMap<>();
            map.put("tableName", key);
            if(maps != null && maps.containsKey(key)){
                map.put("apis", maps.get(key));
            }else{
                map.put("apis", new ArrayList<>());
            }
            res.add(map);
        }
        return Res.ok(res);
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
        return apiService.removeApi(api);
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
