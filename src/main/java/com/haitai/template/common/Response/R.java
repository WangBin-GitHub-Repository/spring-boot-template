package com.haitai.template.common.Response;

import java.io.Serializable;

import static com.haitai.template.common.Response.ResponseEnum.ERROR;
import static com.haitai.template.common.Response.ResponseEnum.SUCCESS;

/**
 * 返回结果
 *
 * @author bin.wang
 * @version 1.0 2020/7/22
 */
public class R<T> implements Serializable {
    private static final long serialVersionUID = -3728972970564481909L;
    private Integer result;
    private String message;
    private T data;

    public R() {
    }

    public R(Integer result, String message) {
        this.result = result;
        this.message = message;
    }

    public R(Integer result, String message, T data) {
        this.result = result;
        this.message = message;
        this.data = data;
    }

    public static R success() {
        return new R(SUCCESS.getCode(), SUCCESS.getMsg());
    }

    public static R success(Object data) {
        return new R(SUCCESS.getCode(), SUCCESS.getMsg(), data);
    }

    public static R result(ResponseEnum responseEnum) {
        return new R(responseEnum.getCode(), responseEnum.getMsg());
    }

    public static R result(ResponseEnum responseEnum, Object data) {
        return new R(responseEnum.getCode(), responseEnum.getMsg(), data);
    }

    public static R error() {
        return new R(ERROR.getCode(), ERROR.getMsg());
    }

    public Integer getResult() {
        return result;
    }

    public void setResult(Integer result) {
        this.result = result;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
