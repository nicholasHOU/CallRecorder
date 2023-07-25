package com.android.callrecorder.utils;

import android.os.Build;

import java.lang.reflect.Field;
import java.util.HashMap;

public class DeviceUtil {
    private static HashMap info;

    public static boolean isHW5A() {
        return Build.DEVICE.equalsIgnoreCase("HWCAM-Q");
    }

    public static boolean isHW6A() {
        return Build.DEVICE.equalsIgnoreCase("HWDLI-Q");
    }

    public static boolean isHWHornorLite() {
        return Build.DEVICE.equalsIgnoreCase("HWPRA-H");
    }

    public static boolean isHongMi4A() {
        return Build.DEVICE.equalsIgnoreCase("rolex");
    }

    public static boolean isZTExx5() {
        return Build.DEVICE.equalsIgnoreCase("P817S01");
    }

    public static boolean isZTExxx() {
        return Build.DEVICE.equalsIgnoreCase("P650A30");
    }

    public static HashMap getDeviceInfo() {
        if (info == null || info.isEmpty()) {
            info = new HashMap();
            Field[] fields = Build.class.getDeclaredFields();
            for (Field field : fields) {
                try {
                    field.setAccessible(true);
                    info.put(field.getName(), field.get("").toString());
                    Logs.d("DeviceUtil", field.getName() + ":" + field.get(""));
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        } else {

        }
        return info;
    }
}
