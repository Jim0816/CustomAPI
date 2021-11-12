package com.ljm.controller;


import com.ljm.entity.Role;
import com.ljm.service.RoleService;
import com.ljm.vo.Res;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping("role")
public class RoleController {

    private RoleService roleService;

    /**
     * 获取权限列表
     * @param
     * @return
     * @author Jim
     */
    @GetMapping(value = "/list")
    public Res list(){
        return Res.ok(roleService.list(new Role()));
    }
}
