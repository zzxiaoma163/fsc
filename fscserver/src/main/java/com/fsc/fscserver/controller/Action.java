package com.fsc.fscserver.controller;

import com.fsc.fscserver.enums.ResultCode;
import com.fsc.fscserver.model.GeneralResponse;

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