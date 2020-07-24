package com.haitai.template.controller.auth.request;

/**
 * @author bin.wang
 * @version 1.0 2020/7/22
 */
public class LoginRequest {

    private String userName;

    private String password;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
