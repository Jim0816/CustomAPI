package com.ljm.controller;

import com.alibaba.fastjson.JSONObject;
import com.ljm.entity.API;
import com.ljm.parseMongo.model.FilterModel;
import com.ljm.service.APIProviderService;
import com.ljm.service.APIService;
import com.ljm.vo.AccessUser;
import com.ljm.vo.Res;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@Slf4j
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
     * 当前策略：查询接口信息，立即解析接口，再根据解析结果去执行  （每次使用都需要解析一次接口，效率很低）
     */
    @PostMapping(value = "/data")
    public Res create(@RequestBody String data, String tag, AccessUser accessUser) {
        log.info("=====================用户"+accessUser.getUsername()+"访问接口==================");
        JSONObject acceptData = JSONObject.parseObject(data);
        //1.根据tag查找出对应api对象数据
        API queryApi = new API();
        queryApi.setTag(tag);
        API api = apiService.get(queryApi);
        if(api == null){
            return Res.failed("接口不存在，无法操作!");
        }

        //接口使用权限判断
        boolean isAllow = false;
        List<String> permissionUsers = api.getPermission();
        if(permissionUsers == null || permissionUsers.size() == 0 || (permissionUsers.size() == 1 && permissionUsers.get(0).equals("*"))){
            //所有用户有权限使用该接口
            isAllow = true;
        }else{
            for(String uid : permissionUsers){
                if(uid.equals(accessUser.getId())){
                    //当前用户在权限列表中
                    isAllow = true;
                    break;
                }
            }
        }

        if(!isAllow){
            //没有使用权限
            return Res.failed("您没有该接口的访问权限！");
        }

        log.info("接口地址: " + api.getUrl());
        log.info("数据: " + data);

        //解析当前接口
        Map<String,Object> parseResult = apiProviderService.analysisApi(api);

        List<FilterModel> filters = (List<FilterModel>) parseResult.get("filter");
        try{
            if(filters != null && filters.size() > 0){
                //用户传入的变量
                for (FilterModel filterModel : filters) {
                    //value为 “$var”  表示取变量
                    if("$var".equals(filterModel.getValue().toString())){
                        filterModel.setValue(acceptData.get(filterModel.getKey()).toString());
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
            return Res.failed("过滤条件绑定变量失败!");
        }


        if(parseResult.get("operateType").toString().equals("get")){
            //查询
            return Res.ok(apiProviderService.get(parseResult));
        }else if(parseResult.get("operateType").toString().equals("add")){
            //新增
            return Res.ok(apiProviderService.add(parseResult, acceptData));
        }else if(parseResult.get("operateType").toString().equals("update")){
            //修改
            return Res.ok(apiProviderService.update(parseResult, acceptData));
        }else{
            //删除
            return Res.ok(apiProviderService.delete(parseResult));
        }
    }
}
