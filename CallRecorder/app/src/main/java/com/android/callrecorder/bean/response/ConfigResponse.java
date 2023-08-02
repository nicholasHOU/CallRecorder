package com.android.callrecorder.bean.response;

public class ConfigResponse extends BaseResponse{

    public DataInfo data;
    public class DataInfo{
        public String url;// 可能为空做好兼容
        public int runTime;// 轮询调用电话拨打接口的时间间隔
    }

}
