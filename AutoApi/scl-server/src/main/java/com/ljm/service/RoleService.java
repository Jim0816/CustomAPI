package com.ljm.service;

import com.ljm.entity.Role;

import java.io.IOException;
import java.util.List;
import java.util.Set;

public interface RoleService {

    /**
     * 直接插入 (插入前必须满足表的元数据信息已经存在   表和元数据绑定一致，表被删了，对应元数据也必须清空)
     * @param
     * @return
     * @author Jim
     */
    boolean add(Role role) throws IOException;

    /**
     * 带检查的插入（先检查是否有数据中出现过规定字段，要保持字段唯一性）
     * @param
     * @return
     * @author Jim
     */
    boolean addWithCheck(Role role, String uniqueFields);

    Role get(Role role);

    List<Role> list(Role role);

    List<Role> page(Role role, int pageNow, int pageSize);

    Integer count(Role role);

    /**
     * 删除
     * @param
     * @return
     * @author Jim
     */
    boolean remove(Role role);

    boolean update(Role role);
}
