package com.ljm.controller;

import com.alibaba.fastjson.JSONObject;
import com.ljm.entity.API;
import com.ljm.entity.Table;
import com.ljm.entity.User;
import com.ljm.parseMongo.model.FilterModel;
import com.ljm.service.APIService;
import com.ljm.service.TableService;
import com.ljm.vo.AccessUser;
import com.ljm.vo.Res;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping("table")
public class TableController {

    private TableService tableService;
    private APIService apiService;

    /**
     * 创建集合 (创建集合是默认创建基础增删改查接口)
     * @param  table 表对象数据
     * @return
     * @author Jim
     */
    @PostMapping(value = "/create")
    public Res create(@RequestBody Table table, AccessUser accessUser) {
        log.info(accessUser.toString());
        /*if(tableService.createTable(table.format(0, accessUser.getUuid()))){
            //表（集合）创建成功，开始创建基础接口
            return apiService.createBaseApis(table);
        }*/
        return Res.ok("");
    }

    /**
     * 创建集合 (创建集合是默认创建基础增删改查接口)
     * @param request 请求数据（表对象数据）
     * @return
     * @author Jim
     */
    @PostMapping(value = "/create1")
    public boolean create1(@RequestBody String request) throws IOException {
        Table table = JSONObject.parseObject(request, Table.class);
        /*if(dataService.createCollection(table.addBaseInfo(null))){
            //表（集合）创建成功，开始创建基础接口
            return apiService.createBaseApis(table);
        }*/
        return false;
    }

    /**
     * 修改集合
     * @param request 请求数据（表对象数据）
     * @return
     * @author Jim
     */
    @PostMapping(value = "/update")
    public boolean update(@RequestBody String request) throws IOException {
        Table table = JSONObject.parseObject(request, Table.class);
        //table.setUpdateTime(LocalDateTime.now());
        table.setUpdateUser("user_uuid");
        //转换实体类型
        String jsonStr = JSONObject.toJSONString(table);
        JSONObject tableJson = JSONObject.parseObject(jsonStr);
        List<FilterModel> filters = new ArrayList<>();
        String uuid = table.getUuid();
        if(uuid != null && !uuid.equals("")){
            FilterModel filterModel = new FilterModel("uuid", uuid, "string", "=", "and");
            filters.add(filterModel);
            return tableService.updateCollection(filters, "*", tableJson);
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
        List<Map> tables = tableService.getCollections(null);
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
        return tableService.getCollections(table);
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
        if(tableService.dropCollection(table.getTableName())){
            //删除当前表下的所有接口
            API api = new API();
            api.setModel(table.getTableName());
            return apiService.removeApi(api);
        }
        return false;
    }
    
}
