package com.android.callrecorder.utils;

import android.os.Build;

public class DeviceUtil {
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
}
