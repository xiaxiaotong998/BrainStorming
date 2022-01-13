package com.haoran.Brainstorming.config;

import com.haoran.Brainstorming.config.realm.MyCredentialsMatcher;
import com.haoran.Brainstorming.config.realm.MyShiroRealm;
import com.haoran.Brainstorming.service.ISystemConfigService;
import org.apache.shiro.codec.Base64;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.filter.authc.FormAuthenticationFilter;
import org.apache.shiro.web.mgt.CookieRememberMeManager;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.servlet.SimpleCookie;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class ShiroConfig {

    private final Logger log = LoggerFactory.getLogger(ShiroConfig.class);

    @Resource
    private MyShiroRealm myShiroRealm;
    @Resource
    private ISystemConfigService systemConfigService;

    @Bean
    public ShiroFilterFactoryBean shiroFilterFactoryBean(DefaultWebSecurityManager defaultWebSecurityManager) {
        log.info("Start...");
        ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();
        shiroFilterFactoryBean.setSecurityManager(defaultWebSecurityManager);

        Map<String, String> map = new HashMap<>();

        map.put("/static/**", "anon");
        map.put("/admin/login", "anon");
        map.put("/admin/logout", "anon");
        map.put("/admin/no_auth", "anon");

        map.put("/admin/permission/**", "perms");
        map.put("/admin/role/**", "perms");
        map.put("/admin/system/**", "perms");
        map.put("/admin/admin_user/**", "perms");

        map.put("/admin/**", "user");

        shiroFilterFactoryBean.setFilterChainDefinitionMap(map);
        shiroFilterFactoryBean.setLoginUrl("/adminlogin");
        shiroFilterFactoryBean.setSuccessUrl("/admin/index");
        shiroFilterFactoryBean.setUnauthorizedUrl("/admin/no_auth");

        return shiroFilterFactoryBean;
    }

    @Bean
    public MyCredentialsMatcher myCredentialsMatcher() {
        return new MyCredentialsMatcher();
    }

    @Bean
    public DefaultWebSecurityManager defaultWebSecurityManager() {
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        myShiroRealm.setCredentialsMatcher(myCredentialsMatcher());
        securityManager.setRealm(myShiroRealm);
        securityManager.setRememberMeManager(rememberMeManager());
        return securityManager;
    }

    @Bean
    public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(DefaultWebSecurityManager defaultWebSecurityManager) {
        AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor = new AuthorizationAttributeSourceAdvisor();
        authorizationAttributeSourceAdvisor.setSecurityManager(defaultWebSecurityManager);
        return authorizationAttributeSourceAdvisor;
    }

    @Bean
    @DependsOn("mybatisPlusConfig")
    public SimpleCookie rememberMeCookie() {
        SimpleCookie simpleCookie = new SimpleCookie("rememberMe");
        int adminRememberMeMaxAge = Integer.parseInt(systemConfigService.selectAllConfig().get("admin_remember_me_max_age").toString());
        simpleCookie.setMaxAge(adminRememberMeMaxAge * 24 * 60 * 60);
        return simpleCookie;
    }

    @Bean
    @DependsOn("mybatisPlusConfig")
    public CookieRememberMeManager rememberMeManager() {
        CookieRememberMeManager cookieRememberMeManager = new CookieRememberMeManager();
        cookieRememberMeManager.setCookie(rememberMeCookie());
        cookieRememberMeManager.setCipherKey(Base64.encode("pybbs is the best!".getBytes()));
        return cookieRememberMeManager;
    }

    @Bean
    public FormAuthenticationFilter formAuthenticationFilter() {
        FormAuthenticationFilter formAuthenticationFilter = new FormAuthenticationFilter();
        formAuthenticationFilter.setRememberMeParam("rememberMe");
        return formAuthenticationFilter;
    }

    @Bean
    @ConditionalOnMissingBean
    public DefaultAdvisorAutoProxyCreator defaultAdvisorAutoProxyCreator() {
        DefaultAdvisorAutoProxyCreator defaultAAP = new DefaultAdvisorAutoProxyCreator();
        defaultAAP.setProxyTargetClass(true);
        return defaultAAP;
    }

}
