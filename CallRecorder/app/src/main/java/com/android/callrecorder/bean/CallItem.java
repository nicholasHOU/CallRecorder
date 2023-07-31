package com.android.callrecorder.bean;

public class CallItem {

    public static final int CALLTYPE_OUT = 1;
    public static final int CALLTYPE_IN = 1;
    public static final int CALLTYPE_NO = 3;
    public static final int CALLTYPE_REJECT = 5;

    public int callType;//0:呼出；1：呼入;2 未接
    public long time;//通话时间
    public String timeStr;//通话时间
    public String duringStr;//通话时长
    public long during;//通话时长
    public String name;//电话号码
    public String phone;//电话号码
    public String recordPath;//录音文件
}
