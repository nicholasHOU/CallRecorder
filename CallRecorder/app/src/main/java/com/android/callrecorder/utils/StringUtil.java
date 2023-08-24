package com.android.callrecorder.utils;

import android.text.TextUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtil {

    /**
     * (?<!\d):该位置前面不允许为数字
     * (?!\d):该位置后面不允许为数字
     * 大陆手机号码11位数，匹配格式：前三位固定格式+后8位任意数
     * 此方法中前三位格式有：
     * 13+任意数
     * 145,147,149
     * 15+除4的任意数(不要写^4，这样的话字母也会被认为是正确的)
     * 166
     * 17+3,5,6,7,8
     * 18+任意数
     * 198,199
     */
    public static String getPhoneNum(String paramValue) {
        if (paramValue == null || paramValue.length() == 0) {
            return "";
        }
        Pattern pattern = Pattern.compile("(?<!\\d)((13[0-9])|(14[0,1,4-9])|(15[0-3,5-9])|(16[2,5,6,7])|(17[0-8])|(18[0-9])|(19[0-3,5-9]))\\d{8}(?!\\d)");
        Matcher matcher = pattern.matcher(paramValue);
        StringBuffer bf = new StringBuffer(64);
        if (matcher.find()) {
            bf.append(matcher.group());
        }
        if (bf.length()==0){
            String phone = paramValue.split("\\(")[0];
            if (TextUtils.isEmpty(phone)){
                bf.append(paramValue);
            }else {
                bf.append(phone);
            }
        }
        return bf.toString();
    }

    public static String getChinese(String paramValue) {
        if (paramValue == null || paramValue.length() == 0) {
            return "";
        }
        String regex = "([\u4e00-\u9fa5]+)";
        StringBuffer bf = new StringBuffer(64);
        Matcher matcher = Pattern.compile(regex).matcher(paramValue);
        while (matcher.find()) {
            bf.append(matcher.group(0));
        }
        return bf.toString();
    }

}
