package com.android.callrecorder.bean.response;

public class LoginResponse extends BaseResponse {
    public Data data;

    public static class Data {
        public String token;
    }

}
