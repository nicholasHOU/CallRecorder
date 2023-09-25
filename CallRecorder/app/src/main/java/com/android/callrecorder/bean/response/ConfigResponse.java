package com.android.callrecorder.bean.response;

public class ConfigResponse extends BaseResponse {

    public DataInfo data;

    public class DataInfo {
        public String url;// 可能为空做好兼容
        public int runTime;// 轮询调用电话拨打接口的时间间隔
        public AliyunInfo aliyun;//
    }

    public static class AliyunInfo {
        public String AccessKeyId;//
        public String SecurityToken;//
        public String AccessKeySecret;//
        public String Expiration;//
        //"aliyun":{"SecurityToken":"CAIS7wF1q6Ft63","AccessKeySecret":"FF7xrLpkzZNZEjNhQDnss52AxXRa1BGvRHM1FhQL3Kyn",
        // "Expiration":"2023-09-01T03:22:32Z"}
    }

}
