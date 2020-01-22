package com.nxmu.common.utils;

import org.springframework.util.DigestUtils;

/**
 * 密码安全工具类
 */
public class SecurityUtil {

    //盐，用于混交md5
    //private static final String slat = "&%1A2Asc*&%$$#@";
    private static final String slat = "&%NX1AMU2Asc*&%$$#@";

    public static void main(String[] args) {
        /**
         * 需要加密的密码尽可能的要复杂，包含大小写字母和数字，
         * 长度不小于8为，加密必须加盐，如果需要更高要求，可以多次加密，多次加盐
         */
        System.out.println(getMD5("12ASD23klk935"));
    }

    /**
     * spring框架里面的加密方法
     *
     * @param value 需要加密的字符串
     * @return 加密后的字符串
     */
    public static String getMD5(String value) {
        String base = value + "/s/" + slat;
        return DigestUtils.md5DigestAsHex(base.getBytes());
    }
}
