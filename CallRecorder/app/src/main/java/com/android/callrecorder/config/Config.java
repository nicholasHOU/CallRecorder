package com.android.callrecorder.config;

public class Config {
    public static final boolean isDebug = true;

    private static final String URL_BASE_TEST = "https://crm.zhongyaoqg.com/zdt/";
    private static final String URL_BASE_PRD = "https://crm.zhongyaoqg.com/zdt/";
//    private static final String URL_BASE_PRD = "https://gw.youzhu999.com/zdt/";
    public static String URL_BASE;
    public static String APP_FILE_PATH;//获取外部存储目录地址

    static {
        if (isDebug) {
            URL_BASE = URL_BASE_TEST;
        } else {
            URL_BASE = URL_BASE_PRD;
        }
    }


}
