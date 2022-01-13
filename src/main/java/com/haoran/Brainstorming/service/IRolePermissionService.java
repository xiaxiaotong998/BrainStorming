package com.haoran.Brainstorming.service;

import com.haoran.Brainstorming.model.RolePermission;

import java.util.List;


public interface IRolePermissionService {
    List<RolePermission> selectByRoleId(Integer roleId);

    void deleteByRoleId(Integer roleId);

    void deleteByPermissionId(Integer permissionId);

    void insert(RolePermission rolePermission);
}
