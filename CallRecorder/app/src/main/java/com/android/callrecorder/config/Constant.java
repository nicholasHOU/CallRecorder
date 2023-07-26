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
    public static final String URL_USERINFO = Config.URL_BASE + "/userinfo";
    /**
     * 上传录音
     */
    public static final String URL_UPLOAD_RECORD = Config.URL_BASE + "/uploadrecord";
    /**
     * 上传录音
     */
    public static final String URL_UPLOAD_RECORD_ALL = Config.URL_BASE + "/uploadrecordall";
    /**
     * 意见反馈
     */
    public static final String URL_FEEDBACK = Config.URL_BASE + "/feedback";
    /**
     * 获取通话记录
     */
    public static final String URL_CALLLOG_LIST = Config.URL_BASE + "/calllog";
    /**
     * 轮训拨打电话
     */
    public static final String URL_CALLPHONE = Config.URL_BASE + "/callphone";
    /**
     * 获取本地录音地址目录及刷新轮训时间间隔
     */
    public static final String URL_CONFIG = Config.URL_BASE + "/config";
}
