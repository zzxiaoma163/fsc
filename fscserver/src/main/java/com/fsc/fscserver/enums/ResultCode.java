package com.fsc.fscserver.enums;

public enum ResultCode {
    SUCCESS("success", "1000"), ERROR("error", "9999"),
    NOPOST("allow post method", "1001"), NOVALIDURL("not valid url", "1002"),
    NOFOLD("not exist fold", "1003"),NOFILE("not exist file", "1004"),
    NOVALID("not author", "1005");
    private String name;
    private String code;

    private ResultCode(String name, String code) {
        this.name = name;
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
