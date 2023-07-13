package com.android.callrecorder.home.bean;

public class CallItem {

    public static final int CALLTYPE_OUT = 0;
    public static final int CALLTYPE_IN = 1;
    public static final int CALLTYPE_NO = 2;

    public int callType;//0:呼出；1：呼入;2 未接
    public String time;//通话时间
    public String during;//通话时长
    public String name;//电话号码
    public String phonenum;//电话号码
    public String recordPath;//录音文件
}
