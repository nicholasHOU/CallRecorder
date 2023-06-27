package com.android.callrecorder.utils;

import android.util.Log;

public class Logs {
    public static final int VERBOSE = 2;
    public static final int DEBUG = 3;
    public static final int INFO = 4;
    public static final int WARN = 5;
    public static final int ERROR = 6;
    public static final int NONE = 7;
    public static int LOGLEVEL = 7;

    public Logs() {
    }

    public static void closeLogs() {
        LOGLEVEL = 7;
    }

    public static void setLogLevel(int var0) {
        LOGLEVEL = var0;
    }

    public static boolean isLoggable(int var0) {
        return var0 >= LOGLEVEL;
    }

    public static void v(String var0, String var1) {
        if (2 >= LOGLEVEL) {
            Log.v(var0, var1);
        }

    }

    public static void d(String var0, String var1) {
        if (3 >= LOGLEVEL) {
            Log.d(var0, var1);
        }

    }

    public static void i(String var0, String var1) {
        if (4 >= LOGLEVEL) {
            Log.i(var0, var1);
        }

    }

    public static void w(String var0, String var1) {
        if (5 >= LOGLEVEL) {
            Log.w(var0, var1);
        }

    }

    public static void e(String var0, String var1) {
        if (6 >= LOGLEVEL) {
            Log.e(var0, var1);
        }

    }

    public static void v(String var0, String var1, Throwable var2) {
        if (2 >= LOGLEVEL) {
            Log.v(var0, var1, var2);
        }

    }

    public static void d(String var0, String var1, Throwable var2) {
        if (3 >= LOGLEVEL) {
            Log.d(var0, var1, var2);
        }

    }

    public static void i(String var0, String var1, Throwable var2) {
        if (4 >= LOGLEVEL) {
            Log.i(var0, var1, var2);
        }

    }

    public static void w(String var0, String var1, Throwable var2) {
        if (5 >= LOGLEVEL) {
            Log.w(var0, var1, var2);
        }

    }

    public static void e(String var0, String var1, Throwable var2) {
        if (6 >= LOGLEVEL) {
            Log.e(var0, var1, var2);
        }

    }

    public static void v(String var0, String var1, Object... var2) {
        if (2 >= LOGLEVEL) {
            Log.v(var0, String.format(var1, var2));
        }

    }

    public static void d(String var0, String var1, Object... var2) {
        if (3 >= LOGLEVEL) {
            Log.d(var0, String.format(var1, var2));
        }

    }

    public static void i(String var0, String var1, Object... var2) {
        if (4 >= LOGLEVEL) {
            Log.i(var0, String.format(var1, var2));
        }

    }

    public static void w(String var0, String var1, Object... var2) {
        if (5 >= LOGLEVEL) {
            Log.w(var0, String.format(var1, var2));
        }

    }

    public static void e(String var0, String var1, Object... var2) {
        if (6 >= LOGLEVEL) {
            Log.e(var0, String.format(var1, var2));
        }

    }

    public static String getStackTraceString(Throwable var0) {
        return Log.getStackTraceString(var0);
    }

    public static void println(int var0, String var1, String var2) {
        if (var0 >= LOGLEVEL) {
            Log.println(var0, var1, var2);
        }

    }

}
