package com.nxmu.common.utils;

public class NumberUtil {
    public static String getRoleNum(int n) {
        return String.format(" %02d", n);
    }

    public static String getUserNum(int n) {
        return String.format(" %05d", n);
    }

    public static void main(String[] args) {
        System.out.println(getUserNum(1));
    }
}
