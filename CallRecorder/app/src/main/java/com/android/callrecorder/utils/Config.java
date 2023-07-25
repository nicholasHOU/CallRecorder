package com.android.callrecorder.utils;

public class Config {
    public static final boolean isDebug = true;

    private static final String URL_BASE_TEST = "http://www.baidu.com/";
    private static final String URL_BASE_PRD = "http://www.baidu.com/";
    public static String URL_BASE;

    static {
        if (isDebug) {
            URL_BASE = URL_BASE_TEST;
        } else {
            URL_BASE = URL_BASE_PRD;
        }
    }


}
