package com.haitai.template.controller.auth;

import com.haitai.template.common.Response.R;
import com.haitai.template.controller.auth.request.LoginRequest;
import com.haitai.template.service.ITAuthUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * @author bin.wang
 * @version 1.0 2020/7/22
 */
@RestController
@RequestMapping("auth")
public class LoginController {

    @Autowired
    private ITAuthUserService authUserService;

    @PostMapping("login")
    public R login(@RequestBody LoginRequest request) {
        R login = authUserService.login(request);
        return login;
    }

    @GetMapping("logout")
    public R logout(HttpServletRequest request) {
        return authUserService.logout(request);
    }
}
