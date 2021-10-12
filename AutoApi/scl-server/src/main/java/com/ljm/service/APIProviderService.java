package com.ljm.service;

import com.ljm.model.API;
import org.omg.CORBA.OBJ_ADAPTER;

import java.util.Map;

public interface APIProviderService {
    /**
     * 解析API对象
     * @param apiRequire Map类型的API接口服务要求
     * @return
     * @author Jim
     */
    Object parseAPI(Map<String, Object> apiRequire, String data);
}
