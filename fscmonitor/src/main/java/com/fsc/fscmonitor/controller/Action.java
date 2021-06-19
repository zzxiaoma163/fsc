package com.fsc.fscmonitor.controller;

import com.fsc.fscmonitor.enums.ResultCode;
import com.fsc.fscmonitor.model.GeneralResponse;
import io.netty.channel.ChannelHandlerContext;

import java.lang.reflect.Method;


public class Action {
    private Object object;
    private Method method;

    public Action(Object object, Method method) {
        this.object = object;
        this.method = method;
    }

    public GeneralResponse call(Object... args) {
        try {
            return (GeneralResponse) method.invoke(object, args);
        } catch (Exception e) {
            return new GeneralResponse(ResultCode.ERROR.getCode(), e.getMessage());
        }
    }

    public Method getMethod() {
        return method;
    }
}