package com.ljm.util;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;

public class StringUtil {

    /**
     * 从字符串中随机组合出几个字符
     *
     * @param length 随机字符串的长度(长度小于原字符串)
     * @return
     */
    public static String getRandomCharFromStr(String str, int length){
        int strLength = str.length();
        if(length >= strLength){
            return str;
        }

        Set<Integer> randomSet = new HashSet<>();
        Random random = new Random();
        StringBuffer sb = new StringBuffer();
        while(sb.length() < length){
            int num = random.nextInt(strLength);
            if(!randomSet.contains(num)){
                sb.append(str.charAt(num));
                randomSet.add(num);
            }
        }
        return sb.toString();
    }
    /**
     * 生成数字和字母组合，字母区分大小写
     *
     * @param length 随机字符串的长度
     * @return
     */
    public static String generateByRandom(final int length) {
        StringBuilder randomSb = new StringBuilder(length);
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            // 输出字母还是数字
            String charOrNum = random.nextInt(2) % 2 == 0 ? "char" : "num";
            // 字符串
            if ("char".equals(charOrNum)) {
                // 判断字母大小写
                int choice = random.nextInt(2) % 2 == 0 ? 65 : 97;
                randomSb = randomSb.append((char) (choice + random.nextInt(26)));
            } else {
                randomSb = randomSb.append(random.nextInt(10));
            }
        }
        return randomSb.toString();
    }

    /**
     * 随机生成32位uuid
     *
     * @param
     * @return
     */
    public static String generateUUID(){
        return UUID.randomUUID().toString().replace("-", "").toLowerCase();
    }


    /**
     * 读取本地json文件
     * @return
     */
    public static String readJsonFile(String fileName) {
        String jsonStr = "";
        try {
            File jsonFile = new File(fileName);
            FileReader fileReader = new FileReader(jsonFile);
            Reader reader = new InputStreamReader(new FileInputStream(jsonFile), StandardCharsets.UTF_8);
            int ch = 0;
            StringBuilder sb = new StringBuilder();
            while ((ch = reader.read()) != -1) {
                sb.append((char) ch);
            }
            fileReader.close();
            reader.close();
            jsonStr = sb.toString();
            return jsonStr;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


}
