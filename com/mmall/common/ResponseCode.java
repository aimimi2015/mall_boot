package com.mmall.common;

/**
 * Created by ${aimimi2015} on 2017/6/9.
 */
public enum ResponseCode {
    SUCCCESS(0, "SUCCESS"),
    ERROR(1, "ERROR"),
    NEED_LOGIN(10, "NEED_LOGIN"),
    ILLEGAl_ARGUMENT(2, "ILLEGAl_ARGUMENT");

    //设置成final不提供set方法
    private final int code;
    private final String desc;

    //相当于构造方法,比如success传进来的0,"SUCCESS"
    ResponseCode(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public int getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }


}