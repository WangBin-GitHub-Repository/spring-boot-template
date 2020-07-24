package com.haitai.template.shiro.realm;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.haitai.template.dao.TAuthRoleResourceMapper;
import com.haitai.template.dao.TAuthUserRoleMapper;
import com.haitai.template.entity.*;
import com.haitai.template.service.ITAuthResourceService;
import com.haitai.template.service.ITAuthRoleService;
import com.haitai.template.service.ITAuthUserService;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.SimplePrincipalCollection;
import org.apache.shiro.subject.support.DefaultSubjectContext;
import org.apache.shiro.util.ByteSource;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author bin.wang
 * @version 1.0 2020/7/22
 */
public class CustomAuthorizingRealm extends AuthorizingRealm {
    private static Logger logger = LoggerFactory.getLogger(CustomAuthorizingRealm.class);

    @Autowired
    private ITAuthUserService authUserService;
    @Autowired
    private ITAuthRoleService authRoleService;
    @Autowired
    private ITAuthResourceService authResourceService;
    @Autowired
    private TAuthUserRoleMapper authUserRoleMapper;
    @Autowired
    private TAuthRoleResourceMapper authRoleResourceMapper;

    /**
     * 授权查询回调函数, 进行鉴权但缓存中无用户的授权信息时调用,负责在应用程序中决定用户的访问控制的方法
     *
     * @param [principalCollection]
     * @return org.apache.shiro.authz.AuthorizationInfo
     * @Date 2020/7/22 16:33
     **/
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        TAuthUser authUser = (TAuthUser) principalCollection.getPrimaryPrincipal();
        logger.info(authUser.getUserName() + "进行授权操作");
        SimpleAuthorizationInfo authorizationInfo = new SimpleAuthorizationInfo();

        //获取用户角色
        List<TAuthRole> authRoles = authRoleService.list(new QueryWrapper<TAuthRole>().eq("check_status", 0));
        List<TAuthUserRole> authUserRoleList = authUserRoleMapper.selectList(new QueryWrapper<TAuthUserRole>().eq("user_id", authUser.getId()).eq("check_status", 0));
        List<String> roles = authRoles.stream().filter(authRole -> authUserRoleList.stream().anyMatch(authUserRole -> authRole.getId().equals(authUserRole.getRoleId()))).map(TAuthRole::getId).collect(Collectors.toList());

        //获取用户权限
        Set<String> permissions = new HashSet<>();
        List<TAuthResource> authResourceList = authResourceService.list(new QueryWrapper<TAuthResource>().eq("check_status", 0));
        roles.forEach(element -> {
            List<TAuthRoleResource> authRoleResourceList = authRoleResourceMapper.selectList(new QueryWrapper<TAuthRoleResource>().eq("role_id", element).eq("check_status", 0));
            Set<String> collect = authResourceList.stream().filter(authResource -> authRoleResourceList.stream().anyMatch(authRoleResource -> authResource.getId().equals(authRoleResource.getResourceId()))).map(TAuthResource::getId).collect(Collectors.toSet());
            permissions.addAll(collect);
        });

        //添加roles
        authorizationInfo.addRoles(roles);
        //添加permissions
        authorizationInfo.addStringPermissions(permissions);
        return authorizationInfo;
    }

    /**
     * 认证回调函数，登录信息和用户验证信息验证
     *
     * @param [authenticationToken]
     * @return org.apache.shiro.authc.AuthenticationInfo
     * @Date 2020/7/22 16:29
     **/
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        UsernamePasswordToken usernamePasswordToken = (UsernamePasswordToken) authenticationToken;
        String userName = usernamePasswordToken.getUsername();
        //根据用户名查询密码，由安全管理器负责对比查询出的数据库中的密码和页面输入的密码是否一致
        TAuthUser authUser = authUserService.getOne(new QueryWrapper<TAuthUser>().eq("user_name", userName).eq("check_status", 0));
        if (authUser == null) {
            throw new UnknownAccountException();
        }

        //单用户登录
        //处理session
        DefaultWebSecurityManager securityManager = (DefaultWebSecurityManager) SecurityUtils.getSecurityManager();
        DefaultWebSessionManager sessionManager = (DefaultWebSessionManager) securityManager.getSessionManager();

        //获取当前已登录的用户session列表
        Collection<Session> sessions = sessionManager.getSessionDAO().getActiveSessions();
        for (Session session : sessions) {
            //清除该用户以前登录时保存的session，强制退出
            Object attribute = session.getAttribute(DefaultSubjectContext.PRINCIPALS_SESSION_KEY);
            if (attribute == null) {
                continue;
            }
            TAuthUser primaryPrincipal = (TAuthUser) ((SimplePrincipalCollection) attribute).getPrimaryPrincipal();
            if (userName.equals(primaryPrincipal.getUserName())) {
                sessionManager.getSessionDAO().delete(session);
            }
        }

        String password = authUser.getPassword();
        ByteSource credentialsSalt = ByteSource.Util.bytes(authUser.getUserName() + password);
        //最后的比对需要交给安全管理器,三个参数进行初步的简单认证信息对象的包装,由安全管理器进行包装运行
        return new SimpleAuthenticationInfo(authUser, password, credentialsSalt, getName());
    }
}
