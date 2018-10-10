package com.mall_boot.Exception;

import com.mall_boot.common.ResultEnum;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by jiangxiao on 2018/10/9.
 */
@Getter
@Setter
public class RegisterException extends RuntimeException{


    private Integer code;

    public RegisterException(ResultEnum resultEnum) {
        super(resultEnum.getMessage());
        this.code = resultEnum.getCode();
    }

    public RegisterException(Integer code, String defaultMessage) {
        super(defaultMessage);
        this.code = code;

    }
}

