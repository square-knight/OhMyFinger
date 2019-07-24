package com.frontcamera.zhousong.util;

public class StringUtil {
    /**
     * 字符串非空判断
     *
     * @param str
     *            字符串
     * @return boolean
     */
    public static boolean isNotEmpty(String str) {
        return ((str != null) && (str.trim().length() > 0));
    }

    /**
     * 字符串判空
     *
     * @param str
     *            字符串
     * @return boolean
     */
    public static boolean isEmpty(String str) {
        return ((str == null) || (str.trim().length() == 0));
    }
}
