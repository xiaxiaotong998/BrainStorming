package com.haoran.Brainstorming.service;

import com.haoran.Brainstorming.model.Permission;

import java.util.List;
import java.util.Map;


public interface IPermissionService {

    void clearRolePermissionCache();

    List<Permission> selectByRoleId(Integer roleId);

    List<Permission> selectByPid(Integer pid);

    Map<String, List<Permission>> selectAll();

    Permission insert(Permission permission);

    Permission update(Permission permission);

    void delete(Integer id);
}
