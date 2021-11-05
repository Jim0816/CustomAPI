package com.ljm.util;

import com.ljm.vo.TokenState;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jose.shaded.json.JSONObject;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class TokenUtil {
    /**
     * 秘钥
     */
    private static final byte[] SECRET="DrangoneYu@163fac04467df11fff26d".getBytes();

    /**
     * 初始化head部分的数据为
     * {
     *      "alg":"HS256",
     *      "type":"JWT"
     * }
     */
    private static final JWSHeader header = new JWSHeader(JWSAlgorithm.HS256, JOSEObjectType.JWT, null, null, null, null, null, null, null, null, null, null, null);


    public static void main(String[] args) {
        Map<String , Object> payload=new HashMap<String, Object>();
        Date date=new Date();
        payload.put("uid", "291969452");//用户id
        payload.put("iat", date.getTime());//生成时间
        payload.put("ext",date.getTime()+1000*60*60);//过期时间1小时
        // String token=null;
        String token= TokenUtil.createToken(payload);
        System.out.println(token);

        Map<String, Object> map = validToken("");
        for(String key : map.keySet()){
            System.out.println("key: "+ key + " value: "+map.get(key));
        }
    }

   /**
    * 生成token，该方法只在用户登录成功后调用
    *
    * @param ，可以存储用户id，token生成时间，token过期时间等自定义字段
    * @return token字符串,若失败则返回null
    *
    * */
    public static String createToken(Map<String, Object> payload) {
        String tokenString=null;
        // 创建一个 JWS object
        JWSObject jwsObject = new JWSObject(header, new Payload(new JSONObject(payload)));
        try {
            // 将jwsObject 进行HMAC签名
            jwsObject.sign(new MACSigner(SECRET));
            tokenString=jwsObject.serialize();
        } catch (JOSEException e) {
              System.err.println("签名失败:" + e.getMessage());
              e.printStackTrace();
        }
        return tokenString;
    }

    /**
     * 校验token是否合法，返回Map集合,集合中主要包含    state状态码   data鉴权成功后从token中提取的数据
     * 该方法在过滤器中调用，每次请求API时都校验
     * @param token
     * @return  Map<String, Object>
     */
    public static Map<String, Object> validToken(String token) {
        Map<String, Object> resultMap = new HashMap<String, Object>();
        try {
            JWSObject jwsObject = JWSObject.parse(token);
            Payload payload = jwsObject.getPayload();
            JWSVerifier verifier = new MACVerifier(SECRET);
            if (jwsObject.verify(verifier)) {
                JSONObject jsonOBj = (JSONObject) payload.toJSONObject();
                // token校验成功（此时没有校验是否过期）
                resultMap.put("state", TokenState.VALID.toString());
                // 若payload包含ext字段，则校验是否过期
                if (jsonOBj.containsKey("ext")) {
                    long extTime = Long.valueOf(jsonOBj.get("ext").toString());
                    long curTime = new Date().getTime();
                    // 过期了
                    if (curTime > extTime) {
                        resultMap.clear();
                        resultMap.put("state", TokenState.EXPIRED.toString());
                    }
                }
                resultMap.put("data", jsonOBj);
            } else {
                // 校验失败
                resultMap.put("state", TokenState.INVALID.toString());
            }
        } catch (Exception e) {
            //e.printStackTrace();
            // token格式不合法导致的异常
            resultMap.clear();
            resultMap.put("state", TokenState.INVALID.toString());
            }
        return resultMap;
        }

}
