package com.ljm.util;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;

public class DateUtil {

    public static void main(String[] args) {

    }

    /**
     * Date 转 String
     * @return
     */
    public static LocalDateTime getNowDate(){
        return LocalDateTime.now();
    }
    /**
     * Date 转 String
     * @return
     */
    public static String date2String(Date date) {
        try {
            if (date == null)
                return null;
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String str = sdf.format(date);
            return str;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }


}
