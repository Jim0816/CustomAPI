package com.ljm.controller;

import com.alibaba.fastjson.JSONObject;
import com.ljm.entity.API;
import com.ljm.parseMongo.model.FilterModel;
import com.ljm.service.APIService;
import com.ljm.service.TableService;
import com.ljm.util.StringUtil;
import com.ljm.vo.Res;
import com.ljm.vo.ResCode;
import lombok.AllArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.bind.annotation.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import java.util.stream.Collectors;

@AllArgsConstructor
@RestController
@RequestMapping("interface")
public class APIController {
    private APIService apiService;
    private TableService dataService;

    /**
     * 创建API对象
     * @param api
     * @return
     * @author Jim
     */
    @PostMapping(value = "/add")
    public Res add(@RequestBody API api){
        if(api != null){
            return Res.ok(apiService.add(api));
        }
        return Res.failed(ResCode.CREATE_API_FAILED);
    }

    /**
     * 查询API对象列表
     * @param
     * @return
     * @author Jim
     */
    @GetMapping(value = "/list")
    public Res list(){
        return Res.ok(apiService.list(new API()));
    }

    /**
     * 创建API对象
     * @param
     * @return
     * @author Jim
     */
    @GetMapping(value = "/templates")
    public Res getTemplate() throws IOException {
        ClassPathResource classPathResource = new ClassPathResource("properties/api.properties");
        Properties properties=new Properties();
        BufferedReader bf = new BufferedReader(new InputStreamReader(classPathResource.getInputStream(),"UTF-8"));//解决读取properties文件中产生中文乱码的问题
        properties.load(bf);


        Map<String,Object> templates = new HashMap<>();
        templates.put("get", JSONObject.parseObject(properties.getProperty("get_require")).getInnerMap());
        templates.put("add", JSONObject.parseObject(properties.getProperty("add_require")).getInnerMap());
        templates.put("update", JSONObject.parseObject(properties.getProperty("update_require")).getInnerMap());
        templates.put("delete", JSONObject.parseObject(properties.getProperty("delete_require")).getInnerMap());

        return Res.ok(templates);
    }

    /**
     * 查询API对象列表
     * @param
     * @return
     * @author Jim
     */
    @GetMapping(value = "/remove")
    public Res remove(String id){
        return Res.ok(apiService.remove(new API().setId(id)));
    }

    /**
     * 修改API对象
     * @param api
     * @return
     * @author Jim
     */
    @PostMapping(value = "/update")
    public Res update(@RequestBody API api){
        return Res.ok(apiService.update(api));
    }

}
