package com.ljm.entity;

import com.ljm.entity.common.BaseEntity;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@ToString(callSuper = true)
@Accessors(chain = true)
public class Role implements Serializable {

    private String id;

    /**
     * 角色代码 (唯一)
     * */
    private String roleCode;

    /**
     * 角色名称 (唯一)
     * */
    private String roleName;

    /**
     * 角色菜单权限列表
     * */
    private String menuPermission;

    /**
     * 是否删除 (默认0 表示不删除)
     * */
    private Integer isDelete;

    /**
     * 是否启用 (默认1 表示启用)
     * */
    private Integer state;

}
