package com.ljm.service;

import com.ljm.entity.Role;

import java.util.List;

public interface RoleService {

    boolean add(Role role);

    boolean addWithCheck(Role role, String checkFields);

    List<Role> get(Role role);

    Integer count(Role role);

    boolean remove(Role role);

    boolean update(Role role);
}
