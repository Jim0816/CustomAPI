package com.ljm.controller;

import com.alibaba.fastjson.JSONObject;
import com.ljm.common.RequestJSONParser;
import com.ljm.model.API;
import com.ljm.model.RequestTemplate;
import com.ljm.model.Table;
import com.ljm.service.DataService;
import javafx.scene.control.Tab;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@AllArgsConstructor
@RestController
@RequestMapping("table")
public class DataController {

    private DataService dataService;

    /**
     * 创建集合
     * @param request 请求数据（表对象数据）
     * @return
     * @author Jim
     */
    @PostMapping(value = "/create")
    public boolean create(@RequestBody String request) {
        Table table = JSONObject.parseObject(request, Table.class);
        return dataService.createCollection(table.addBaseInfo());
    }

    /**
     * 查询当前数据库所有集合
     * @param
     * @return
     * @author Jim
     */
    @PostMapping(value = "/tables")
    public List<Map> listCollection() {
        return dataService.getCollections(null);
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
        return dataService.dropCollection(table.getTableName());
    }
    
}
