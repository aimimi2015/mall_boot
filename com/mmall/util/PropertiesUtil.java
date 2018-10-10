package com.mmall.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;

/**
 * Created by geely
 */
@Slf4j
public class PropertiesUtil {

//    private static Logger logger = LoggerFactory.getLogger(PropertiesUtil.class);
    //使用指定类初始化日志对象,可以打印指定类的异常信息。在日志输出的时候，可以打印出日志信息所在类


    private static Properties props;

    static {
        String fileName = "mmall.properties";
        props = new Properties();
        try {
            props.load(new InputStreamReader(PropertiesUtil.class.getClassLoader().getResourceAsStream(fileName),"UTF-8"));
        } catch (IOException e) {
            log.error("配置文件读取异常",e);  //前面是报错信息,我们定义的。后面的e是堆栈信息  会被打印到日志
        }
    }

    public static String getProperty(String key){
        String value = props.getProperty(key.trim());
        if(StringUtils.isBlank(value)){
            return null;
        }
        return value.trim();
    }

    public static String getProperty(String key,String defaultValue){

        String value = props.getProperty(key.trim());
        if(StringUtils.isBlank(value)){
            value = defaultValue;    //如果查值是空的,那么久赋值defaultValue上去
        }
        return value.trim();
    }



}
