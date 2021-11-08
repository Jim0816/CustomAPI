package com.ljm.controller;

import com.ljm.entity.User;
import com.ljm.service.UserService;
import com.ljm.vo.Res;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping("user")
public class UserController {


    private UserService userService;


    /**
     * 用户登录
     * @param
     * @return
     * @author Jim
     */
    @PostMapping(value = "/login")
    public Map<String, Object> login(@RequestBody User user){
        return userService.login(user);
    }

    /**
     * 用户下线
     * @param
     * @return
     * @author Jim
     */
    @PostMapping(value = "/logout")
    public Res logout(String token, HttpServletRequest request){
        User accessUser = (User) request.getAttribute("user");
        return Res.data(userService.logout(token, accessUser));
    }

}
