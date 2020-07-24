package com.haitai.template.shiro.config;

import org.apache.shiro.spring.web.ShiroFilterFactoryBean;

import java.util.Map;

/**
 * @author bin.wang
 * @version 1.0 2020/7/22
 */
public interface ICustomFilterChainDefinitions {
    Map<String, String> loadFilterChainDefinitions();

    void updatePermission(ShiroFilterFactoryBean shiroFilterFactoryBean, String roleId);
}
