package com.ljm.util;

import org.bson.types.ObjectId;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class Test {
    public static void main(String[] args) {
        String uuid = UUID.randomUUID().toString().replace("-", "").toLowerCase();
        System.out.println(uuid);
    }

    /**
     * 将时间转化成mongo的objectId
     * @param dateStr
     * @return
     * @throws ParseException
     */
    private static String dateToObjectId(String dateStr) throws ParseException {
        StringBuffer objectId = new StringBuffer("");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date date = sdf.parse(dateStr);
        //转换为16进制的字符串
        objectId.append(Long.toHexString(date.getTime() / 1000));
        //bson-3.6.4.jar 版本校验ObjectId的长度为24位，不足24位补0
        while(objectId.length() < 24) {
            objectId.append("0");
        }
        return objectId.toString();
    }
}
