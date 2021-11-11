package com.ljm.vo;

import com.ljm.entity.User;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;
import java.io.Serializable;

@Data
@Accessors(chain = true)
@ToString(callSuper = true)
public class UserVO extends User implements Serializable {

    /**
     * 角色名称
     * */
    private String roleName;
    /**
     * 菜单权限列表
     * */
    private String[] permissionList;

    private Integer state;

    public UserVO copyParent(User user){
        this.setId(user.getId());
        this.setUsername(user.getUsername());
        this.setNickname(user.getNickname());
        this.setPassword(user.getPassword());
        this.setRoleId(user.getRoleId());
        this.setEmail(user.getEmail());
        this.setState(user.getState());
        this.setIsDelete(user.getIsDelete());
        this.setLastLoginTime(user.getLastLoginTime());
        this.setSalt(user.getSalt());
        return this;
    }

}
