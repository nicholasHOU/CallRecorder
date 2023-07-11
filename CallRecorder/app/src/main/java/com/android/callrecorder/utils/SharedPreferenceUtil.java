package com.android.callrecorder.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

public class SharedPreferenceUtil {
    private static volatile SharedPreferenceUtil instance;
    private SharedPreferences prefs;
    private static Context context;
    private String SP_NAME = "callrecorder";
    private String K_USERNAME = "usernameU";
    private String K_PASSWORD = "passwordP";
    private String K_HOST = "serverHost";

    private SharedPreferenceUtil() {
        prefs = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
    }

    public static void init(Context ctx) {
        context = ctx;
        Logs.e("SP init", "111111111111111111111111");
    }

    public static SharedPreferenceUtil getInstance() {
        Logs.e("SP init", "2222222222222222");
        if (instance == null) {
            synchronized (SharedPreferenceUtil.class) {
                if (instance == null) {
                    instance = new SharedPreferenceUtil();
                }
            }
        }
        return instance;
    }


    public String getString(String key, String defaultStr) {
        return prefs.getString(key, defaultStr);
    }

    public String getString(String key) {
        return getString(key, "");
    }

    public boolean putString(String key, String value) {
        SharedPreferences.Editor editor = prefs.edit();
        return editor.putString(key, value).commit();
    }

    /**
     * 重置host
     *
     * @param value
     * @return
     */
    public boolean setHost(String value) {
        SharedPreferences.Editor editor = prefs.edit();
        return editor.putString(K_HOST, value).commit();
    }

    /**
     * 获取服务端host
     *
     * @param value
     * @return
     */
    public String getHost(String value) {
        return getString(K_HOST);
    }

    /**
     * 设置登录信息
     *
     * @param username
     * @param password
     * @return
     */
    public boolean setLoginInfo(String username, String password) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(K_USERNAME, username)
                .putString(K_PASSWORD, password);
        return editor.commit();
    }


    public boolean hasLoginInfo() {
        String username = getString(K_USERNAME, "");
        String password = getString(K_PASSWORD, "");
        return !TextUtils.isEmpty(username) && !TextUtils.isEmpty(password);
    }

    public String getUsername() {
        return getString(K_USERNAME, "");
    }

    public String getPassword() {
        return getString(K_PASSWORD, "");
    }
}
