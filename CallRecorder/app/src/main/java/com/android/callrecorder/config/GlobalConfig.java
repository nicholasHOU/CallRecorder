package com.android.callrecorder.config;

import android.os.Build;

import com.android.callrecorder.BuildConfig;
import com.android.callrecorder.utils.DeviceUtil;

public class GlobalConfig {
    public static long runTime = 5;//5s 循环调用接口，获取是否有需要拨号的任务

    public static String url;//录音地址目录
    public static String SecurityToken;
    public static String AccessKeyId;
    public static String AccessKeySecret;
    public static String Expiration;
    public static String username = "";

    public static String token = "";
    public static String appVersion = BuildConfig.VERSION_NAME;
    public static String type = Build.BRAND + "-" + Build.MODEL;
    public static String extra = DeviceUtil.getDeviceInfo().toString();

//    static Map extraParams = new HashMap();

//    static {
//        extraParams.put("type", GlobalConfig.type);
//        extraParams.put("extra", GlobalConfig.extra);
//    }
}
