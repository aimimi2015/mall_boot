package com.mall_boot.handle;

import com.mall_boot.Exception.RegisterException;
import com.mall_boot.common.ServerResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Created by jiangxiao on 2018/10/9.
 */
@Slf4j
@ControllerAdvice
public class ExceptionHandle {


    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.NOT_FOUND)  //指定该异常返回的状态码，本身我们定义这个异常正常返回，返回给前端的内容是我们定的，但状态码是200，可以通过这个全局异常的拦截器把状态码改了。
    public ServerResponse<String> handle(Exception e) {
        if (e instanceof RegisterException) {
            RegisterException registerException = (RegisterException) e;
            return ServerResponse.createByErrorCodeMessage(registerException.getCode(),registerException.getMessage());
        } else {
            log.error("【系统异常】{}", e);
            return ServerResponse.createByErrorCodeMessage(-1,"未知错误");
        }
    }
}




