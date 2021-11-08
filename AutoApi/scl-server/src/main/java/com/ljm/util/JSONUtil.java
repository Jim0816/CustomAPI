package com.ljm.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.ljm.entity.Table;

import java.util.Map;

public class JSONUtil {
    public static void main(String[] args) {
        String str1 = "{\"tableName\": \"sys_table\",\"dbType\": \"MongoDB\",\"desc\": \"用于存储系统中所有表的结构信息，包括本身\",\"fields\":[{\"name\": \"id\", \"type\": \"String\", \"isRequire\": 1, \"isUnique\": 1, \"length\": 32, \"defaultVal\": \"\", \"remark\": \"sys_table主键\"},{\"name\": \"tableName\", \"type\": \"String\", \"isRequire\": 1, \"isUnique\": 1, \"length\": 20, \"defaultVal\": \"\", \"remark\": \"模型对应表名,英文\"},{\"name\": \"dbType\", \"type\": \"String\", \"isRequire\": 1, \"isUnique\": 0, \"length\": 10, \"defaultVal\": \"MongoDB\", \"remark\": \"模型对应数据库存储类型\"},{\"name\": \"desc\", \"type\": \"String\", \"isRequire\": 0, \"isUnique\": 0, \"length\": 300, \"defaultVal\": \"\", \"remark\": \"表的描述信息\"},{\"name\": \"fields\", \"type\": \"Array\", \"isRequire\": 1, \"isUnique\": 0, \"length\": -1, \"defaultVal\": \"\", \"remark\": \"表的字段列表\"},{\"name\": \"isDelete\", \"type\": \"Int\", \"isRequire\": 0, \"isUnique\": 0, \"length\": 1, \"defaultVal\": 0, \"remark\": \"是否删除\"}]}";
        String str2 = "{\"tableName\": \"sys_role\",\"dbType\": \"MongoDB\",\"desc\": \"用于存储系统中角色基本信息\",\"fields\":[{\"name\": \"id\", \"type\": \"String\", \"isRequire\": 1, \"isUnique\": 1, \"length\": 32, \"defaultVal\": \"\", \"remark\": \"sys_role主键\"},{\"name\": \"roleCode\", \"type\": \"String\", \"isRequire\": 1, \"isUnique\": 1, \"length\": 30, \"defaultVal\": \"\", \"remark\": \"角色代码\"},{\"name\": \"roleName\", \"type\": \"String\", \"isRequire\": 1, \"isUnique\": 1, \"length\": 30, \"defaultVal\": \"\", \"remark\": \"角色名称唯一\"},{\"name\": \"menuPermission\", \"type\": \"String\", \"isRequire\": 0, \"isUnique\": 0, \"length\": 300, \"defaultVal\": \"\", \"remark\": \"菜单权限\"},{\"name\": \"isDelete\", \"type\": \"Int\", \"isRequire\": 0, \"isUnique\": 0, \"length\": 1, \"defaultVal\": 0, \"remark\": \"是否删除\"},{\"name\": \"state\", \"type\": \"Int\", \"isRequire\": 0, \"isUnique\": 0, \"length\": 1, \"defaultVal\": 1, \"remark\": \"是否删除\"}]}";
        Table table1 = JSONUtil.parseStringToBean(Table.class, str1);
        Table table2 = JSONUtil.parseStringToBean(Table.class, str2);
        JSONObject jsonObject = JSON.parseObject(str1);

    }
    public static <T> T parseMapToBean(Class<T> c, Map map){
        return JSON.parseObject(JSON.toJSONString(map), c);
    }

    public static <T> T parseStringToBean(Class<T> c, String str){
        return JSONObject.parseObject(str, c);
    }

    public static JSONObject parseBeanToJSONObject(Object bean){
        return JSONObject.parseObject(JSONObject.toJSON(bean).toString());
    }
}
