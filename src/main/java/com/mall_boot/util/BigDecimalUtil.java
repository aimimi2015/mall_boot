package com.mall_boot.util;

import java.math.BigDecimal;

/**
 * Created by ${aimimi2015} on 2017/6/19.
 */
public class BigDecimalUtil {

    private BigDecimalUtil(){

    }   //放一个私有构造器,让该类不能再外部被实例化

    public static BigDecimal add(double v1,double v2){
        BigDecimal b1 = new BigDecimal(v1);
        BigDecimal b2 = new BigDecimal(v2);
        return b1.add(b2);

    }
    public static BigDecimal sub(double v1,double v2){
        BigDecimal b1 = new BigDecimal(v1);
        BigDecimal b2 = new BigDecimal(v2);
        return b1.subtract(b2);

    }
    public static BigDecimal mul(double v1,double v2){
        BigDecimal b1 = new BigDecimal(v1);
        BigDecimal b2 = new BigDecimal(v2);
        return b1.multiply(b2);

    }
    public static BigDecimal div(double v1,double v2){
        BigDecimal b1 = new BigDecimal(v1);
        BigDecimal b2 = new BigDecimal(v2);
        return b1.divide(b2,2,BigDecimal.ROUND_HALF_UP);  //保留两位小数,四舍五入

    }


}

