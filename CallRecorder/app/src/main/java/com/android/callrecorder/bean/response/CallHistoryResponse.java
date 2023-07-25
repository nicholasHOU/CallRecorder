package com.android.callrecorder.bean.response;

import java.util.List;

public class CallHistoryResponse extends BaseResponse {
    public List<CallLogDay> son;

    public class CallLogDay {
        /**
         * {
         * “year”:”2023”
         * “month”:”6”,
         * “total_number”:0, // 通话记录数
         * “total_time”:0// 通话时长  单位秒
         * “son”: [
         * {
         * “day”:”今天”,
         * “total_number”:123, // 通话记录数
         * “total_time”:123123 //通话时长  单位秒
         * },
         */
        public int year;
        public int month;
        public int total_number;
        public int total_time;

        public List<CallLog> son;

    }

    public class CallLog {
        /**
         * “day”:”7月1日”,
         * “total_number”:123, // 通话记录数
         * “total_time”:123123 //通话时长  单位秒
         * },
         */

        public String day;
        public int total_number;
        public int total_time;
        public long time;
    }
}
