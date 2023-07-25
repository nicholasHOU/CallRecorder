package com.android.callrecorder.bean.response;

import java.io.Serializable;

public class BaseResponse<T> implements Serializable {
    /**
     *  “code”:500,
     *  “message”:”failed reason”,
     *  “data”:[]
     */
    public int code;
    public String message;
    public T data;




}
