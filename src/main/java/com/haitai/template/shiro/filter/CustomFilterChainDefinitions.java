package com.haitai.template.shiro.filter;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.haitai.template.dao.TAuthUserMapper;
import com.haitai.template.dao.TAuthUserRoleMapper;
import com.haitai.template.entity.TAuthUser;
import com.haitai.template.entity.TAuthUserRole;
import com.haitai.template.shiro.config.ICustomFilterChainDefinitions;
import com.haitai.template.shiro.util.ShiroUtil;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.filter.mgt.DefaultFilterChainManager;
import org.apache.shiro.web.filter.mgt.PathMatchingFilterChainResolver;
import org.apache.shiro.web.servlet.AbstractShiroFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author bin.wang
 * @version 1.0 2020/7/24
 */
@Component
public class CustomFilterChainDefinitions implements ICustomFilterChainDefinitions {
    private static Logger logger = LoggerFactory.getLogger(CustomFilterChainDefinitions.class);

    @Autowired
    private TAuthUserMapper authUserMapper;
    @Autowired
    private TAuthUserRoleMapper authUserRoleMapper;

    /**
     * 初始化权限
     *
     * @param []
     * @return java.util.Map<java.lang.String, java.lang.String>
     * @Date 2020/7/24 9:57
     **/
    @Override
    public Map<String, String> loadFilterChainDefinitions() {
        // 权限控制map.从数据库获取

        Map<String, String> filter = getAllowPermission();
        //拦截所有请求
        filter.put("/**", "authc");

        //拦截所有请求
//        filter.put("/**", "anon");
        return filter;
    }

    /**
     * 资源过滤
     *
     * @param []
     * @return java.util.Map<java.lang.String, java.lang.String>
     * @Date 2020/7/24 9:57
     **/
    private Map<String, String> getAllowPermission() {
        Map<String, String> filter = new LinkedHashMap<>();
        //放行登录
        filter.put("/auth/login", "anon");

        //配置退出 过滤器,其中的具体的退出代码Shiro已经替我们实现了
        filter.put("/auth/logout", "anon");
        //放行静态文件
        filter.put("/login.html", "anon");
        filter.put("/static/**", "anon");
        filter.put("/views/**", "anon");
        //放行swagger
        filter.put("/swagger-ui.html", "anon");
        filter.put("/swagger-resources/**", "anon");
        filter.put("/v2/api-docs", "anon");
        filter.put("/webjars/springfox-swagger-ui/**", "anon");
        return filter;
    }

    /**
     * 对角色进行增删改操作时，需要调用此方法进行动态刷新
     *
     * @param [shiroFilterFactoryBean, roleId]
     * @return void
     * @Date 2020/7/24 9:58
     **/
    @Override
    public void updatePermission(ShiroFilterFactoryBean shiroFilterFactoryBean, String roleId) {
        synchronized (this) {
            AbstractShiroFilter shiroFilter;
            try {
                shiroFilter = (AbstractShiroFilter) shiroFilterFactoryBean.getObject();
            } catch (Exception e) {
                throw new RuntimeException("get ShiroFilter from shiroFilterFactoryBean error!");
            }
            PathMatchingFilterChainResolver filterChainResolver = (PathMatchingFilterChainResolver) shiroFilter.getFilterChainResolver();
            DefaultFilterChainManager manager = (DefaultFilterChainManager) filterChainResolver.getFilterChainManager();
            // 清空老的权限控制
            manager.getFilterChains().clear();
            shiroFilterFactoryBean.getFilterChainDefinitionMap().clear();
            shiroFilterFactoryBean.setFilterChainDefinitionMap(loadFilterChainDefinitions());
            // 重新构建生成
            Map<String, String> chains = shiroFilterFactoryBean.getFilterChainDefinitionMap();
            for (Map.Entry<String, String> entry : chains.entrySet()) {
                String url = entry.getKey();
                String chainDefinition = entry.getValue().trim().replace(" ", "");
                manager.createChain(url, chainDefinition);
            }

            //根据角色id来获取用户
            List<TAuthUserRole> authUserRoleList = authUserRoleMapper.selectList(new QueryWrapper<TAuthUserRole>().eq("role_id", roleId).eq("check_status", 0));
            Set<String> collect = authUserRoleList.stream().map(TAuthUserRole::getUserId).collect(Collectors.toSet());
            List<TAuthUser> authUserList = authUserMapper.selectBatchIds(collect);
            // TODO: 2019/6/13
            if (authUserList.size() == 0) {
                return;
            }
            for (TAuthUser authUser : authUserList) {
                ShiroUtil.kickOutUser(authUser.getUserName(), false);
            }
        }
    }
}
