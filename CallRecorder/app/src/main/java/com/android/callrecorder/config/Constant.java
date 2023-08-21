package com.android.callrecorder.config;

import java.io.File;

public interface Constant {
    String DIR = "";
//    String APP_FILE_PATH = Environment.getExternalStorageDirectory().getPath() + File.separator + "ZDT_Record";

    String CRASH_FILE_PATH = Config.APP_FILE_PATH + File.separator + "crash";
    String RECORD_FILE_PATH = Config.APP_FILE_PATH + File.separator + "record";



    class HttpCode {
        public static int HTTP_SUCCESS = 200;
        public static int HTTP_NEED_LOGIN = 4003;//登录已过期，请重新登录~
        public static int HTTP_NEED_LOGIN_FAIL = 4001;//登录失败，请重新登录~！
        public static int HTTP_NEED_LOGIN_ERRORFAIL = 4002;//账户或密码不正确，请核对后重新登录~！
        public static int HTTP_NEED_LOGIN_DEVICE_ERROR = 4004;//禁止多设备登录~！
    }

    String K_TOKEN = "token";
    String K_EXTRA = "extra";
    String K_TYPE = "type";
    String K_APP_VERSION = "appVersion";

    /**
     * 登录
     */
    String URL_LOGIN = Config.URL_BASE + "login/login_do.html";
    /**
     * 获取用户信息
     */
    String URL_USERINFO = Config.URL_BASE + "operation/user_info.html";
    /**
     * 上传录音
     */
    String URL_UPLOAD_RECORD = Config.URL_BASE + "operation/record_upload.html";
    /**
     * 上传日志
     */
    String URL_UPLOAD_LOG = Config.URL_BASE + "log/index.html";
    /**
     * 升级接口
     */
    String URL_UPDATE = Config.URL_BASE + "log/upgrade.html";
    /**
     * 意见反馈
     */
    String URL_FEEDBACK = Config.URL_BASE + "operation/feedback.html";
    /**
     * 通话记录上传
     */
    String URL_CALLLOG_UPLOAD = Config.URL_BASE + "operation/call_log_upload.html";
    /**
     * 获取通话记录
     */
    String URL_CALLLOG_LIST = Config.URL_BASE + "operation/call_log.html";
    /**
     * 获取通话记录-一天的
     */
    String URL_CALLLOG_DAY_LIST = Config.URL_BASE + "operation/record_list.html";
    /**
     * 轮训拨打电话
     */
    String URL_CALLPHONE = Config.URL_BASE + "operation/ring_up.html";
    /**
     * 获取本地录音地址目录及刷新轮训时间间隔
     */
    String URL_CONFIG = Config.URL_BASE + "operation/record_url.html";

}
