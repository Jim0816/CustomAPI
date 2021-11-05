package com.ljm.vo;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.ljm.entity.User;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.bson.json.JsonObject;

@Data
@Accessors(chain = true)
@ToString(callSuper = true)
public class AccessUser extends User {


    public AccessUser transform(User user){
        String json = JSON.toJSONString(user);
        return JSONObject.parseObject(json, AccessUser.class);
    }
}
