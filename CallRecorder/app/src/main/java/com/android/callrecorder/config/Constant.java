package com.android.callrecorder.config;

import com.android.callrecorder.utils.Config;

public interface Constant {
    String DIR = "";
    String FILE = "";



    class HttpCode {
        public static int HTTP_SUCCESS = 200;
        public static int HTTP_NEED_LOGIN = 4001;

    }

    public static final String K_TOKEN = "token";
    public static final String K_EXTRA = "extra";
    public static final String K_TYPE = "type";

    /**
     * 登录
     */
    public static final String URL_LOGIN = Config.URL_BASE + "/login";
    /**
     * 获取用户信息
     */
    public static final String URL_USERINFO = Config.URL_BASE + "/login";
    /**
     * 上传录音
     */
    public static final String URL_UPLOAD_RECORD_ALL = Config.URL_BASE + "/login";
    /**
     * 意见反馈
     */
    public static final String URL_FEEDBACK = Config.URL_BASE + "/login";
    /**
     * 获取通话记录
     */
    public static final String URL_CALLLOG_LIST = Config.URL_BASE + "/login";
}
