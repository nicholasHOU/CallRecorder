package com.android.callrecorder.bean.response;

public class UpdateResponse extends BaseResponse {
    public DataInfo data;

    public static final int UPDATETYPE_NONE = 0;
    public static final int UPDATETYPE_NORMAL = 1;
    public static final int UPDATETYPE_FORCE = 2;
    public static class DataInfo {
        public String downloadUrl;
        public String appVersion;
        public String tip;//升级提示语
        public int updateType;//0：无需升级；1：普通升级；2：强制升级
    }

}
