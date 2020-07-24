package com.haitai.template.common.Response;

/**
 * @author bin.wang
 * @version 1.0 2020/7/22
 */
public enum ResponseEnum {
    SUCCESS(200,"成功"),
    USER_NOTLOGIN(300,"用户未登录"),
    USER_NOAUTHORITY(403,"用户没有权限"),
    ERROR(500,"访问异常");

    private Integer code;

    private String msg;

    ResponseEnum(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public Integer getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
