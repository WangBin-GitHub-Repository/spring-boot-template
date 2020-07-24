package com.haitai.template.service;

import com.haitai.template.common.Response.R;
import com.haitai.template.controller.auth.request.LoginRequest;
import com.haitai.template.entity.TAuthUser;
import com.baomidou.mybatisplus.extension.service.IService;

import javax.servlet.http.HttpServletRequest;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author bin.wang
 * @since 2020-07-20
 */
public interface ITAuthUserService extends IService<TAuthUser> {

    R login(LoginRequest request);

    R logout(HttpServletRequest request);

}
