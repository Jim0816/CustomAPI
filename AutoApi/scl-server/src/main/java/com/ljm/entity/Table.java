package com.ljm.entity;

import com.alibaba.fastjson.JSONObject;
import com.ljm.entity.common.BaseEntity;
import com.ljm.util.DateUtil;
import com.ljm.util.StringUtil;
import com.ljm.vo.Field;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Data
@ToString(callSuper = true)
@Accessors(chain = true)
public class Table extends BaseEntity implements Serializable {

    private String uuid;
    /**
     * 数据对象表名
     * */
    private String tableName;

    /**
     * 数据库存储类型 (MongoDB Mysql)
     * */
    private String dbType;

    /**
     * 数据对象描述
     * */
    private String desc;

    /**
     * 集合字段
     * */
    private List<Field> fields;


    /**
     * 新创建的对象
     * @param type 0表示新创建对象 ， 1表示接受修改对象
     * @param operateUserUUID 操作用户的uuid
     * @return
     * @author Jim
     */
    public Table format(int type, String operateUserUUID){
        if(type == 0){
            this.uuid = StringUtil.generateUUID();
            this.setIsDelete(0);
            this.setCreateTime(DateUtil.getDateString());
            this.setCreateUser(operateUserUUID);
        }else{
            this.setUpdateTime(DateUtil.getDateString());
            this.setUpdateUser(operateUserUUID);
        }
        return this;
    }

    /**
     * 修改的对象
     * @param
     * @return
     * @author Jim
     */
    public Table updateTable(String userId){
        this.setIsDelete(0);
        this.setCreateTime(DateUtil.getDateString());
        this.setCreateUser(userId);
        this.setUpdateTime(null);
        this.setUpdateUser("");
        return this;
    }

    /**
     * 格式化table对象数据
     * @param
     * @return
     * @author Jim
     */
    public Table formatTableObj(JSONObject obj){
        Table table = obj.toJavaObject(Table.class);
        return table;
    }

}
