package com.ljm.controller;

import com.ljm.entity.User;
import com.ljm.service.UserService;
import com.ljm.util.TokenUtil;
import com.ljm.vo.Res;
import com.ljm.vo.ResCode;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.omg.CORBA.OBJ_ADAPTER;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping("user")
public class UserController {

    private RedisTemplate redisTemplate;
    private UserService userService;


    /**
     * 用户登录
     * @param
     * @return
     * @author Jim
     */
    @PostMapping(value = "/login")
    public Res login(@RequestBody User user){
        log.info("登录用户: " + user);
        //log.info("登录用户:"+user.getUsername());
        //将用户信息存入redis
        if(user != null){
            if(userService.login(user)){
                //登录成功
                log.info("登录成功");
                //创建token
                Map<String, Object> tokenData = new HashMap<>();
                tokenData.put("userId", user.getUserId());//用户id
                Date date = new Date();
                tokenData.put("iat", date.getTime());//生成时间
                tokenData.put("ext",date.getTime()+1000*60*60);//过期时间1小时
                String token = TokenUtil.createToken(tokenData);
                //token存入redis
                redisTemplate.opsForValue().set("user:" + token, user);
                Map<String, Object> data = new HashMap();
                data.put("token", token);
                return Res.ok(data);
            }else{
                log.info("登录失败");
                return Res.failed("登录失败");
            }
        }else{
            return Res.failed("后端接受用户登录信息失败！");
        }
    }

}
