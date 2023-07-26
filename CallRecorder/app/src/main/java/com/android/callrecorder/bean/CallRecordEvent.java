package com.android.callrecorder.bean;

public class CallRecordEvent {

    public static final int START = 0;
    public static final int END = 1;

    public CallRecordEvent(int type, long timestamp){
        this.type = type;
        this.timestamp = timestamp;
    }
    public int type ;//0:开始;  1:结束
    public long timestamp;//时间戳
    public String recordFile;//录音文件
    public String phone;//手机号码

}


