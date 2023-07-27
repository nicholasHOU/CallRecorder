package com.android.callrecorder.utils;

public class Config {
    public static final boolean isDebug = true;

    private static final String URL_BASE_TEST = "https://crm.zhongyaoqg.com/zdt/";
    private static final String URL_BASE_PRD = "https://crm.zhongyaoqg.com/zdt/";
    public static String URL_BASE;

    static {
        if (isDebug) {
            URL_BASE = URL_BASE_TEST;
        } else {
            URL_BASE = URL_BASE_PRD;
        }
    }


}
