package com.ljm.util;

import com.ljm.entity.User;
import com.ljm.vo.BeanField;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class BeanUtil {

    /**
     * 获取非空属性
     * @param source
     * @return 所有非空的属性键值对
     */
    public static Map<String, BeanField> getNotNullPropertyNames(Object source) {
        final BeanWrapper src = new BeanWrapperImpl(source);
        java.beans.PropertyDescriptor[] pds = src.getPropertyDescriptors();
        Map<String, BeanField> notNullValMap = new HashMap<>();
        for(java.beans.PropertyDescriptor pd : pds) {
            Object srcValue = src.getPropertyValue(pd.getName());
            String type = src.getPropertyType(pd.getName()).toString();
            if(type.contains("class")){
                //引用对象类型  class java.lang.String
                String[] splits = type.split("\\.");
                type = splits[splits.length-1];
            }

            //规范类型名称，为了适配后面的接口
            switch (type){
                case "Integer":
                    type = "int";
                    break;
                case "String":
                    type = "string";
                default:
                    break;
            }

            if (srcValue != null && !pd.getName().equals("class")){
                BeanField beanField = new BeanField(type, srcValue);
                notNullValMap.put(pd.getName(), beanField);
            }
        }
        return notNullValMap;
    }

}
