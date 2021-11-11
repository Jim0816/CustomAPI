package com.ljm.service;

import com.ljm.entity.Table;
import com.ljm.entity.User;

import javax.servlet.http.HttpServletRequest;

public interface CommonService {
    /**
     * 判断表是否存在 （表和表的描述信息要同时存在，才能说明表存在）
     * @param tableName
     * @return
     * @author Jim
     */
    boolean tableIsExist(String tableName);



}
