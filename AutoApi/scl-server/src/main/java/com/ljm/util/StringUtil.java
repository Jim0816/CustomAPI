package com.ljm.util;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Random;
import java.util.UUID;

public class StringUtil {

    public static void main(String[] args) {
        String res = generateByRandom(6);
        System.out.println(res);
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
