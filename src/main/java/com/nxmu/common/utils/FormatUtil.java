package com.nxmu.common.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FormatUtil {
    public static boolean checkEmail(String email) {
        final String pattern1 = "^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
        final Pattern pattern = Pattern.compile(pattern1);
        final Matcher mat = pattern.matcher(email);
        if (!mat.find()) {
            return false;
        }
        return true;
    }

    // 利用正则表达式校验QQ号
    public static boolean checkQQ(String str) {
        return str.matches("[1-9][0-9]{4,14}");    // matches()方法告知此字符串是否匹配给定的正则表达式。
    }

    // 利用正则表达式校验微信号
    public static boolean checkWechat(String str) {
        return str.matches("^^[a-z_\\d]+$");    // matches()方法告知此字符串是否匹配给定的正则表达式。
    }

    // 利用正则表达式校验用户名
    //由字母数字下划线组成且开头必须是字母，不能少于3位，不能超过31位
    public static boolean checkUserName(String str) {
        return str.matches("[a-zA-Z]{1}[a-zA-Z0-9_]{2,30}");
    }

    // 利用正则表达式校验密码
    //字母和数字构成，不能少于6位，不能超过31位
    public static boolean checkPassword(String str){
            return str.matches("[a-zA-Z0-9]{6,30}");
    }

    public static void main(String[] args) {
        System.out.println(checkPassword("143789"));
        System.out.println(checkPassword("h1437568098j"));
        System.out.println(checkUserName("ddf"));
        System.out.println(checkUserName("f15580807347j"));

        System.out.println(checkEmail("143756898@qq.com"));
    }
}
