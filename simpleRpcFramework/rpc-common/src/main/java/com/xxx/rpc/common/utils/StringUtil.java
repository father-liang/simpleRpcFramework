package com.xxx.rpc.common.utils;

import org.apache.commons.lang3.StringUtils;

/**
 * 字符串工具类
 */
public class StringUtil {

    /**
     * 判断字符串是否为空
     * @param str
     * @return
     */
    public static boolean isEmpty(String str){
        if(str != null){
            str = str.trim();
        }

        return StringUtils.isEmpty(str);
    }

    /**
     * 判断字符串是否非空
     * @param str
     * @return
     */
    public static boolean isNotEmpty(String str){
        return !isEmpty(str);
    }

    /**
     *分割固定格式的字符串
     * @param str
     * @param separator
     * @return
     */
    public static String[] split(String str, String separator){
        return StringUtils.splitByWholeSeparator(str, separator);
    }
}
