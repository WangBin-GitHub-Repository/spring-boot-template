package com.haitai.template.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.haitai.template.common.Response.R;
import com.haitai.template.controller.auth.request.LoginRequest;
import com.haitai.template.dao.*;
import com.haitai.template.entity.*;
import com.haitai.template.service.ITAuthUserService;
import com.haitai.template.shiro.util.PasswordHelper;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.haitai.template.common.Response.ResponseEnum.ERROR;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author bin.wang
 * @since 2020-07-20
 */
@Service
public class TAuthUserServiceImpl extends ServiceImpl<TAuthUserMapper, TAuthUser> implements ITAuthUserService {

    @Autowired
    private TAuthUserMapper authUserMapper;
    @Autowired
    private TAuthRoleMapper authRoleMapper;
    @Autowired
    private TAuthResourceMapper authResourceMapper;
    @Autowired
    private TAuthUserRoleMapper authUserRoleMapper;
    @Autowired
    private TAuthRoleResourceMapper authRoleResourceMapper;

    @Override
    public R login(LoginRequest request) {
        //验证登录信息是否为空
        if (StringUtils.isBlank(request.getUserName()) || StringUtils.isBlank(request.getPassword())) {
            return R.result(ERROR, "用户名或密码为空");
        }

        //验证用户是否存在且状态可用
        TAuthUser authUser = authUserMapper.selectOne(new QueryWrapper<TAuthUser>().eq("user_name", request.getUserName()).eq("check_status", 0));
        if (authUser == null) {
            return R.result(ERROR, "用户不存在或用户名错误");
        }

        //验证用户密码是否正确
        String encryptPassword = PasswordHelper.encryptPassword(request.getUserName(), request.getPassword());
        if (!authUser.getPassword().equals(encryptPassword)) {
            return R.result(ERROR, "密码错误");
        }

        //验证用户是否有可用角色
        List<TAuthRole> authRoles = authRoleMapper.selectList(new QueryWrapper<TAuthRole>().eq("check_status", 0));
        List<TAuthUserRole> authUserRoleList = authUserRoleMapper.selectList(new QueryWrapper<TAuthUserRole>().eq("user_id", authUser.getId()).eq("check_status", 0));
        List<TAuthRole> authRoleSet = authRoles.stream().filter(authRole -> authUserRoleList.stream().anyMatch(authUserRole -> authRole.getId().equals(authUserRole.getRoleId()))).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(authRoleSet)) {
            return R.result(ERROR, "用户角色异常,请联系管理员");
        }

        //验证用户权限
        Set<TAuthResource> permissions = new HashSet<>();
        List<TAuthResource> authResourceList = authResourceMapper.selectList(new QueryWrapper<TAuthResource>().eq("check_status", 0));
        authRoleSet.forEach(element -> {
            List<TAuthRoleResource> authRoleResourceList = authRoleResourceMapper.selectList(new QueryWrapper<TAuthRoleResource>().eq("role_id", element.getId()).eq("check_status", 0));
            Set<TAuthResource> collect = authResourceList.stream().filter(authResource -> authRoleResourceList.stream().anyMatch(authRoleResource -> authResource.getId().equals(authRoleResource.getResourceId()))).collect(Collectors.toSet());
            permissions.addAll(collect);
        });
        if (CollectionUtils.isEmpty(permissions)) {
            return R.result(ERROR, "用户权限异常,请联系管理员");
        }

        //shiro设置
        Subject subject = SecurityUtils.getSubject();
        UsernamePasswordToken token = new UsernamePasswordToken(request.getUserName(), PasswordHelper.encryptPassword(request.getUserName(), request.getPassword()));
        subject.login(token);
        String sessionId = subject.getSession().getId().toString();

        HashMap<String, String> map = new HashMap<>();
        map.put("id", authUser.getId());
        map.put("userName", authUser.getUserName());
        map.put("fullName", authUser.getFullName());
        map.put("token", sessionId);

        return R.success(map);
    }

    @Override
    public R logout(HttpServletRequest request) {
        String requestedSessionId = request.getRequestedSessionId();
        if (StringUtils.isNotEmpty(requestedSessionId)) {
            Subject subject = SecurityUtils.getSubject();
            subject.logout();
        }
        return R.success();
    }
}
