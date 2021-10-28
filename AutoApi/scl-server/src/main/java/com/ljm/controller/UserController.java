package com.ljm.controller;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@AllArgsConstructor
@RestController
@RequestMapping("user")
public class UserController {




    /**
     * 用户登录
     * @param
     * @return
     * @author Jim
     */
    @PostMapping(value = "/login")
    public boolean login(){
        //System.out.println(managerUserName);
        //System.out.println(managerPassword);
        return false;
    }

}
