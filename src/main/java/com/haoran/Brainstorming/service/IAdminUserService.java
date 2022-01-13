package com.haoran.Brainstorming.service;

import com.haoran.Brainstorming.model.AdminUser;

import java.util.List;
import java.util.Map;


public interface IAdminUserService {
    AdminUser selectByUsername(String username);

    List<Map<String, Object>> selectAll();

    void update(AdminUser adminUser);

    void insert(AdminUser adminUser);

    void delete(Integer id);

    AdminUser selectById(Integer id);

    List<AdminUser> selectByRoleId(Integer roleId);
}
