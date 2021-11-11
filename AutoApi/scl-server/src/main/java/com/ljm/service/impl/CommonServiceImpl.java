package com.ljm.service.impl;

import com.ljm.entity.Table;
import com.ljm.entity.User;
import com.ljm.service.CommonService;
import com.ljm.service.UserService;
import com.ljm.util.MongoDBUtil;
import com.ljm.util.TokenUtil;
import com.nimbusds.jose.shaded.json.JSONObject;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@Slf4j
@AllArgsConstructor
@Service
public class CommonServiceImpl implements CommonService {

    private MongoDBUtil mongoDBUtil;

    private static final String TABLE_NAME ="sys_table";

    @Override
    public boolean tableIsExist(String tableName) {
        if(mongoDBUtil.isExistCollection(tableName)){
            //当前表已经存在数据库,还需要判断该表的元数据信息是否存在sys_table
            Table queryTable = new Table();
            List<Table> tableList = mongoDBUtil.query(queryTable.setTableName(tableName), TABLE_NAME, 0, 10);
            if(tableList != null && tableList.size() > 0){
                //存在
                return true;
            }
        }
        //不存在
        return false;
    }
}
