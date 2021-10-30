package com.ljm.util;


import org.apache.commons.codec.digest.DigestUtils;

public class MD5Util {

    public static String md5(String str){
        return DigestUtils.md5Hex(str);
    }

    //加密前端明文：明文 + 前端固定盐  (前端使用这个逻辑，后端管理员默认账号需要用到)
    public static String encryptFromUserToPass(String str){
        String salt = "1a2b3c";
        return md5(salt.charAt(0) + str + salt.charAt(1) + salt.charAt(salt.length()-1)); //后端盐长度取6  取出3粒盐
    }

    //str为前端（前端加密 明文+ 前端盐）传来的密文，salt为用户注册时生成的随机盐（与前端盐不一样）
    public static String encryptFromWebSecretToDB(String webSecret, String salt){
        return md5(salt.charAt(0) + webSecret + salt.charAt(1) + salt.charAt(salt.length()-1)); //后端盐长度取6  取出3粒盐
    }
}
