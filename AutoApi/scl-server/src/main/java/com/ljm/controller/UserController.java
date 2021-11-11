package com.ljm.controller;

import com.ljm.entity.User;
import com.ljm.service.CommonService;
import com.ljm.service.RoleService;
import com.ljm.service.UserService;
import com.ljm.vo.Res;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;

@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping("user")
public class UserController {
    private CommonService commonService;
    private UserService userService;
    private RoleService roleService;


    /**
     * 用户登录
     * @param
     * @return
     * @author Jim
     */
    @PostMapping(value = "/login")
    public Res login(@RequestBody User user){
        return userService.login(user);
    }

    /**
     * 用户信息
     * @param
     * @return
     * @author Jim
     */
    @GetMapping(value = "/info")
    public Res info(HttpServletRequest request){
        //获取请求头中的token
        //1.获取用户个人信息
        User user = userService.getAccessUser(request);
        return Res.ok(userService.getAllUserInfo(user));
    }

    /**
     * 用户列表
     * @param
     * @return
     * @author Jim
     */
    @GetMapping(value = "/list")
    public Res list(){
        return Res.ok(userService.getAllUserInfoList());
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
        //return Res.data(userService.logout(token, accessUser));
        return Res.ok("");
    }

}
