package com.haitai.template.shiro.util;

import com.haitai.template.entity.TAuthUser;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.Authenticator;
import org.apache.shiro.authc.LogoutAware;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.mgt.eis.SessionDAO;
import org.apache.shiro.subject.SimplePrincipalCollection;
import org.apache.shiro.subject.support.DefaultSubjectContext;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.Objects;

/**
 * @author bin.wang
 * @version 1.0 2020/7/24
 */
public class ShiroUtil {
    private static SessionDAO sessionDAO = (SessionDAO) SpringUtil.getBean(SessionDAO.class);

    private ShiroUtil() {
    }

    /**
     * 获取指定用户名的Session
     *
     * @param [userName]
     * @return org.apache.shiro.session.Session
     * @Date 2020/7/24 10:07
     **/

    private static Session getSessionByUsername(String userName) {
        Collection<Session> sessions = sessionDAO.getActiveSessions();
        Object attribute;
        for (Session session : sessions) {
            attribute = session.getAttribute(DefaultSubjectContext.PRINCIPALS_SESSION_KEY);
            if (attribute == null) {
                continue;
            }
            Object primaryPrincipal = ((SimplePrincipalCollection) attribute).getPrimaryPrincipal();
            TAuthUser user = new TAuthUser();
            try {
                BeanUtils.copyProperties(user, primaryPrincipal);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
            if (user == null) {
                continue;
            }
            if (Objects.equals(user.getUserName(), userName)) {
                return session;
            }
        }
        return null;
    }

    /**
     * 删除用户缓存信息
     *
     * @param [userName, isRemoveSession]
     * @return void
     * @Date 2020/7/24 10:08
     **/
    public static void kickOutUser(String userName, boolean isRemoveSession) {
        Session session = getSessionByUsername(userName);
        if (session == null) {
            return;
        }

        Object attribute = session.getAttribute(DefaultSubjectContext.PRINCIPALS_SESSION_KEY);
        if (attribute == null) {
            return;
        }
        Object primaryPrincipal = ((SimplePrincipalCollection) attribute).getPrimaryPrincipal();
        TAuthUser user = new TAuthUser();
        try {
            BeanUtils.copyProperties(user, primaryPrincipal);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        if (!userName.equals(user.getUserName())) {
            return;
        }
        //删除session
        if (isRemoveSession) {
            sessionDAO.delete(session);
        }
        DefaultWebSecurityManager securityManager = (DefaultWebSecurityManager) SecurityUtils.getSecurityManager();
        Authenticator authc = securityManager.getAuthenticator();
        //删除cache，在访问受限接口时会重新授权
        ((LogoutAware) authc).onLogout((SimplePrincipalCollection) attribute);
    }
}
