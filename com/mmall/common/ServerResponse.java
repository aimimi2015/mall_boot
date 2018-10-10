package com.mmall.common;


import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import java.io.Serializable;

/**
 * Created by ${aimimi2015} on 2017/6/9.
 */

@JsonSerialize(include =  JsonSerialize.Inclusion.NON_NULL)
//保证序列化json的时候,如果是null的对象,key也会消失  ,比如有事传到前端的数据中,不需要传data,data的值为空就不会被传过去
public class ServerResponse<T> implements Serializable {

    private int status;
    private String msg;
    private T data;

    private ServerResponse(int status) {
        this.status = status;
    }

    private ServerResponse(int status, T data) {
        this.status = status;
        this.data = data;
    }

    private ServerResponse(int status, String msg, T data) {
        this.status = status;
        this.msg = msg;
        this.data = data;
    }

    private ServerResponse(int status, String msg) {
        this.status = status;
        this.msg = msg;
    }


    @JsonIgnore  //因为是public所以如果json序列化的时候也会和status;msg;data;一起显示到前端.使之不在json序列化中
//    如果没有的话前端会有一个success:true
    public boolean isSuccess() {
        return this.status == ResponseCode.SUCCCESS.getCode();

    }

    public int getStatus() {
        return status;
    }

    public T getData() {
        return data;
    }

    public String getMsg() {
        return msg;
    }

    //成功的响应
    public static <T> ServerResponse<T> creatBySuccess() {
        return new ServerResponse<T>(ResponseCode.SUCCCESS.getCode());
    }

    public static <T> ServerResponse<T> creatBySuccessMessage(String msg) {
        return new ServerResponse<T>(ResponseCode.SUCCCESS.getCode(), msg);
    }

    public static <T> ServerResponse<T> creatBySuccess(T data) {
        return new ServerResponse<T>(ResponseCode.SUCCCESS.getCode(), data);
    }
    //这里如果传过来一个string如何区分是data还是string呢，通过creatBySuccessMessage(String msg)和creatBySuccess(T data)区分


    public static <T> ServerResponse<T> creatBySuccessMessage(String msg, T data) {
        return new ServerResponse<T>(ResponseCode.SUCCCESS.getCode(), msg, data);
    }

    //失败的响应
    public static <T> ServerResponse<T> creatByError() {
        return new ServerResponse<T>(ResponseCode.ERROR.getCode(), ResponseCode.ERROR.getDesc());

    }
    public static <T> ServerResponse<T> creatByErrorMessage(String errorMessage){
        return new ServerResponse<T>(ResponseCode.ERROR.getCode(),errorMessage);
    }

    public static <T> ServerResponse<T> createByErrorCodeMessage(int errorCode,String errorMessage){
        return new ServerResponse<T>(errorCode,errorMessage);

    }



}


