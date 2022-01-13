package com.haoran.Brainstorming.config.realm;

import com.haoran.Brainstorming.model.AdminUser;
import com.haoran.Brainstorming.model.Permission;
import com.haoran.Brainstorming.model.Role;
import com.haoran.Brainstorming.service.IAdminUserService;
import com.haoran.Brainstorming.service.IPermissionService;
import com.haoran.Brainstorming.service.IRoleService;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;


@Component
public class MyShiroRealm extends AuthorizingRealm {

    private final Logger log = LoggerFactory.getLogger(MyShiroRealm.class);

    @Resource
    private IAdminUserService adminUserService;
    @Resource
    private IRoleService roleService;
    @Resource
    private IPermissionService permissionService;

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        SimpleAuthorizationInfo simpleAuthorizationInfo = new SimpleAuthorizationInfo();
        AdminUser adminUser = adminUserService.selectByUsername(principals.toString());
        Role role = roleService.selectById(adminUser.getRoleId());
        simpleAuthorizationInfo.addRole(role.getName());

        List<Permission> permissions = permissionService.selectByRoleId(adminUser.getRoleId());
        List<String> permissionValues = permissions.stream().map(Permission::getValue).collect(Collectors.toList());
        simpleAuthorizationInfo.addStringPermissions(permissionValues);
        return simpleAuthorizationInfo;
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        String username = (String) token.getPrincipal();
        log.info("User：{} is login...", username);
        AdminUser adminUser = adminUserService.selectByUsername(username);
        if (adminUser == null) throw new UnknownAccountException();
        return new SimpleAuthenticationInfo(username, adminUser.getPassword(), getName());
    }

}
