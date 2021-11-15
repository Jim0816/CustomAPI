package com.ljm.controller;

import com.ljm.entity.Table;
import com.ljm.service.APIService;
import com.ljm.service.TableService;
import com.ljm.util.StringUtil;
import com.ljm.vo.Res;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
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
     * 查询当前数据库所有集合
     * @param
     * @return
     * @author Jim
     */
    @GetMapping(value = "/list")
    public Res list() {
        Table queryTable = new Table();
        return Res.ok(tableService.list(queryTable));
    }

    /**
     * 创建集合 (创建集合是默认创建基础增删改查接口)
     * @param table 表对象数据
     * @return
     * @author Jim
     */
    @PostMapping(value = "/add")
    public Res add(@RequestBody Table table){
        table.setId(StringUtil.generateUUID());
        return Res.ok(tableService.add(table));
    }

    /**
     * 删除table
     * @param id table主键
     * @return
     * @author Jim
     */
    @GetMapping(value = "/remove")
    public Res remove(String id) {
        Table queryTable = new Table();
        return Res.ok(tableService.remove(queryTable.setId(id)));
    }

    /**
     * 修改集合
     * @param table 表对象数据
     * @return
     * @author Jim
     */
    @PostMapping(value = "/update")
    public Res update(@RequestBody Table table){
        return Res.ok(tableService.update(table));
    }

    /**
     * 查询当前数据库所有集合
     * @param
     * @return
     * @author Jim
     */
    @PostMapping(value = "/tables")
    public List<Map> listCollection() {
        List<Map> tables = tableService.getTables(null);
        if(tables != null && tables.size() > 0){
            return tables.stream().filter(item -> {
                return !item.get("tableName").toString().contains("sys_");
            }).collect(Collectors.toList());
        }
        return tables;
    }

    
}
