package com.android.callrecorder.bean.response;

import java.util.List;

public class CallHistoryDayResponse extends BaseResponse {
    public DataInfo data;

    public static class DataInfo {
        public List<CallLogDay> son;
    }

    public static class CallLogDay {
        /**
         * "fileName":"通话录音的名称",
         * "callType":1,//1、呼入、2.呼出、3未接 、5拒接  、0未知
         * "time":122313123,// 通话录音记录的时间戳，与通话记录上传接口中的time一致
         * "during":2131, // 视频长度  返回单位为秒
         * "url": "https://xxxxx.com/xxxx.mp3",  //通话录音网络地址，点击可进行播放
         */
        public String fileName;
        public String url;
        public String phone;
        public int callType;
        public int during;
        public long time;
    }

}
