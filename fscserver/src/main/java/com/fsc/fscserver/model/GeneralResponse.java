package com.fsc.fscserver.model;

import com.fsc.fscserver.enums.ResultCode;

import java.io.Serializable;


public class GeneralResponse implements Serializable {
    private String code;
    private String msg;
    private Object data;

    public GeneralResponse() {

    }

    public GeneralResponse(Object data) {
        this.code = ResultCode.SUCCESS.getCode();
        this.msg = ResultCode.SUCCESS.getName();
        this.data = data;
    }

    public GeneralResponse(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

}
