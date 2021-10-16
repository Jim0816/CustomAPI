package com.ljm.controller;

import com.alibaba.fastjson.JSONObject;
import com.ljm.model.API;
import com.ljm.model.Table;
import com.ljm.service.APIService;
import com.ljm.service.DataService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@AllArgsConstructor
@RestController
@RequestMapping("table")
public class DataController {

    private DataService dataService;
    private APIService apiService;

    /**
     * 创建集合 (创建集合是默认创建基础增删改查接口)
     * @param request 请求数据（表对象数据）
     * @return
     * @author Jim
     */
    @PostMapping(value = "/create")
    public boolean create(@RequestBody String request) throws IOException {
        Table table = JSONObject.parseObject(request, Table.class);
        if(dataService.createCollection(table.addBaseInfo())){
            //表（集合）创建成功，开始创建基础接口
            return apiService.createBaseApis(table);
        }
        return false;
    }

    /**
     * 查询当前数据库所有集合
     * @param
     * @return
     * @author Jim
     */
    @PostMapping(value = "/tables")
    public List<Map> listCollection() {
        List<Map> tables = dataService.getCollections(null);
        if(tables != null && tables.size() > 0){
            return tables.stream().filter(item -> {
                return !item.get("tableName").toString().contains("sys_");
            }).collect(Collectors.toList());
        }
        return tables;
    }

    /**
     * 查询当前数据库所有集合
     * @param
     * @return
     * @author Jim
     */
    @PostMapping(value = "/get")
    public List<Map> get(@RequestBody String request) {
        Table table = JSONObject.parseObject(request, Table.class);
        return dataService.getCollections(table);
    }

    /**
     * 删除集合
     * @param request 请求数据
     * @return
     * @author Jim
     */
    @PostMapping(value = "/drop")
    public boolean drop(@RequestBody String request) {
        Table table = JSONObject.parseObject(request, Table.class);
        if(dataService.dropCollection(table.getTableName())){
            //删除当前表下的所有接口
            API api = new API();
            api.setModel(table.getTableName());
            return apiService.removeApi(api);
        }
        return false;
    }
    
}
