package com.android.callrecorder.utils;

public class Config {
    public static final boolean isDebug =true;

    public static String URL_BASE = "";
    private static final String URL_BASE_TEST = "";
    private static final String URL_BASE_PRD = "";

    static {
        if (isDebug){
            URL_BASE = URL_BASE_TEST;
        }else {
            URL_BASE = URL_BASE_PRD;
        }
    }

}
