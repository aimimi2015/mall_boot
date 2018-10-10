package com.mall_boot.util;

import org.springframework.util.StringUtils;

import java.security.MessageDigest;

/**
 * Created by geely
 */
public class MD5Util {

    private static String byteArrayToHexString(byte b[]) {
        StringBuilder resultSb = new StringBuilder();
        for (byte aB : b) resultSb.append(byteToHexString(aB));

        return resultSb.toString();
    }

    private static String byteToHexString(byte b) {
        int n = b;
        if (n < 0)
            n += 256;
        int d1 = n / 16;
        int d2 = n % 16;
        return hexDigits[d1] + hexDigits[d2];
    }

    /**
     * 返回大写MD5
     *
     * @param origin
     * @param charsetname 字符编码
     * @return
     */
    private static String MD5Encode(String origin, String charsetname) {
        String resultString = null;
        try {
            resultString = origin;
            MessageDigest md = MessageDigest.getInstance("MD5");
            if (charsetname == null || "".equals(charsetname))
                resultString = byteArrayToHexString(md.digest(resultString.getBytes()));
            else
                resultString = byteArrayToHexString(md.digest(resultString.getBytes(charsetname)));
        } catch (Exception ignored) {
        }
        return resultString.toUpperCase();
    }

    /**
     * Md 5 encode utf 8 string.
     *
     * @param origin the origin
     * @return the string
     */
    public static String MD5EncodeUtf8(String origin) {
        //原文加上盐值,如果盐值没有就默认为后面定义的空
        origin = origin + PropertiesUtil.getProperty("password.salt", "aimimi2015");
        return MD5Encode(origin, "utf-8");
    }


    private static final String hexDigits[] = {"0", "1", "2", "3", "4", "5",
            "6", "7", "8", "9", "a", "b", "c", "d", "e", "f"};

}
