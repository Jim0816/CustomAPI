package com.ljm.controller;

import com.alibaba.fastjson.JSONObject;
import com.ljm.model.RequestTemplate;
import com.ljm.common.RequestJSONParser;
import com.ljm.service.APIService;
import com.ljm.service.DataService;
import com.ljm.util.MongoDBUtil;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

/**
 * 统一处理基本业务接口   已经废弃
 * @author Jim
 */

@AllArgsConstructor
@RestController
@RequestMapping("apicopy")
public class APICopyController {

    private MongoDBUtil mongoDBUtil;
    private APIService apiService;
    private DataService baseService;

    /**
     * 统一处理基本业务接口
     * @param request 请求数据
     * @return
     * @author Jim
     */
    @PostMapping(value = "/service")
    public String service(@RequestBody String request) throws IOException {
        RequestTemplate requestTemplate = RequestJSONParser.parseRequest(request);
        if(requestTemplate.getService().equals("create")){
            //创建集合
            //boolean result = mongoDBUtil.createCollection(requestTemplate.getCollectionName());
            //System.out.println(result);
        }

        if(requestTemplate.getService().equals("drop")){
            //删除指定集合
            //boolean result = mongoDBUtil.dropCollection(requestTemplate.getCollectionName());
            //System.out.println(result);
        }

        if(requestTemplate.getService().equals("drop-all")){
            //删除所有集合
            //boolean result = mongoDBUtil.dropAllCollection();
            //System.out.println(result);
        }


        if(requestTemplate.getService().equals("apply")){
            //注册接口
            //API api = new API(requestTemplate.getCollectionName(), requestTemplate.getOperate(), requestTemplate.getDesc());
            //System.out.println(api);
            //API api = new API();
            //api.setCollectionName(requestTemplate.getCollectionName());
            //api.setOperateType();
            //apiService.addApi();
        }

        return "00000";
    }

    /**
     * 创建集合
     * @param request 请求数据
     * @return
     * @author Jim
     */
    @PostMapping(value = "/create")
    public boolean create(@RequestBody String request) {
        RequestTemplate requestTemplate = RequestJSONParser.parseRequest(request);
        //return baseService.createCollection(requestTemplate);
        return false;
    }



    /**
     * 注册接口(为指定数据集合添加接口)
     * @param request 请求数据
     * @return
     * @author Jim
     */
    @PostMapping(value = "/apply")
    public boolean apply(@RequestBody String request) {
        RequestTemplate requestTemplate = RequestJSONParser.parseRequest(request);
        return baseService.registerApi(requestTemplate);
    }

    /**
     * 删除接口
     * @param request 请求数据
     * @return
     * @author Jim
     */
    @PostMapping(value = "/remove")
    public String removeApi(@RequestBody String request) {
        return "";
    }




}
