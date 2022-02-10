package com.acezhhh.common.vo;

import com.acezhhh.common.enums.RequestEnum;

/**
 * @author acezhhh
 * @date 2022/1/30
 */
public class RequestVo<T> {

    public RequestVo(RequestEnum type, T data) {
        this.type = type;
        Data = data;
    }

    private RequestEnum type;

    private T Data;

    public RequestEnum getType() {
        return type;
    }

    public void setType(RequestEnum type) {
        this.type = type;
    }

    public T getData() {
        return Data;
    }

    public void setData(T data) {
        Data = data;
    }
}
