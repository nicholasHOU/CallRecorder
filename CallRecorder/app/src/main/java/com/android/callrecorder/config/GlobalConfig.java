package com.android.callrecorder.config;

import android.os.Build;

import com.android.callrecorder.utils.DeviceUtil;

import java.util.HashMap;
import java.util.Map;

public class GlobalConfig {
    public static String username = "";

    public static String token = "";
    public static String type = Build.BRAND+"-"+ Build.MODEL;
    public static String extra = DeviceUtil.getDeviceInfo().toString();

    static Map extraParams = new HashMap();

    static {
        extraParams.put("type",GlobalConfig.type);
        extraParams.put("extra",GlobalConfig.extra);
    }
}
