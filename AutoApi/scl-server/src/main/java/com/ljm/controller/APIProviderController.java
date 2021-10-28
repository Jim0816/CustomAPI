package com.ljm.controller;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.api.R;
import com.ljm.entity.API;
import com.ljm.parseMongo.model.FilterModel;
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
     * 当前策略：查询接口信息，立即解析接口，再根据解析结果去执行  （每次使用都需要解析一次接口，效率很低）
     * 改进策略：在接口注册或者修改时，将接口解析结果存在redis中，使用接口时只需要临时查询出当前接口对应的解析结果，拿去执行即可
     */
    @PostMapping(value = "/data")
    public R create(@RequestBody String data, String tag) {
        JSONObject acceptData = JSONObject.parseObject(data);
        //1.根据tag查找出对应api对象数据
        API api = new API();
        api.setTag(tag);
        //因为tag唯一，所以apis最多只有一个
        List<Map> apis = apiService.getApi(api);
        if(apis.size() > 0){
            //解析当前接口
            Map apiMap = apis.get(0);
            Map<String,Object> parseResult = apiProviderService.analysisApi(apiMap);

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
                return R.failed("过滤条件绑定变量失败!");
            }


            if(parseResult.get("operateType").toString().equals("get")){
                //查询
                return R.ok(apiProviderService.get(parseResult));
            }else if(parseResult.get("operateType").toString().equals("put")){
                //新增
                return R.ok(apiProviderService.add(parseResult, acceptData));
            }else if(parseResult.get("operateType").toString().equals("post")){
                //修改
                return R.ok(apiProviderService.update(parseResult, acceptData));
            }else{
                //删除
                return R.ok(apiProviderService.delete(parseResult));
            }
        }
        return R.failed("接口不存在，无法操作!");
    }
}
