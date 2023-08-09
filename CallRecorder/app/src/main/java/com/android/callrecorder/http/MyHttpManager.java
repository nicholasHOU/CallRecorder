package com.android.callrecorder.http;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.T;

import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.android.callrecorder.bean.response.BaseResponse;
import com.android.callrecorder.bean.response.CallHistoryResponse;
import com.android.callrecorder.bean.response.CallPhoneResponse;
import com.android.callrecorder.bean.response.LoginResponse;
import com.android.callrecorder.bean.response.UserInfoResponse;
import com.android.callrecorder.config.Constant;
import com.android.callrecorder.config.GlobalConfig;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import zuo.biao.library.interfaces.OnHttpResponseListener;
import zuo.biao.library.manager.HttpManager;

public class MyHttpManager<T> {

    private static MyHttpManager instance;// 单例

    public static MyHttpManager getInstance() {
        if (instance == null) {
            synchronized (MyHttpManager.class) {
                if (instance == null) {
                    instance = new MyHttpManager();
                }
            }
        }
        return instance;
    }

    private MyHttpManager() {
    }

    public void post(final Map<String, Object> request, final String url
            , final int requestCode, final ResponseListener<T> listener) {
        Map<String, String> headers = new HashMap<>();
        headers.put(Constant.K_TOKEN, GlobalConfig.token);
        headers.put(Constant.K_TYPE, GlobalConfig.type);
        headers.put(Constant.K_EXTRA, GlobalConfig.extra);
        headers.put(Constant.K_APP_VERSION, GlobalConfig.appVersion);
        HttpManager.getInstance().post(request, headers, url,
                true, requestCode, new OnHttpResponseListener() {
                    @Override
                    public void onHttpResponse(int requestCode, String resultJson, Exception e) {
                        if (!TextUtils.isEmpty(resultJson)) {
                            try {
                                BaseResponse data = JSON.parseObject(resultJson, BaseResponse.class);
                                Class<T> tClass = listener.getTClass();
                                if (tClass == null) {
                                    listener.onHttpResponse(requestCode, false, null);
                                } else {
                                    T data3 = JSON.parseObject(resultJson, tClass);
                                    if (Constant.HttpCode.HTTP_SUCCESS == data.code) {
                                        listener.onHttpResponse(requestCode, true, data3);
                                    } else {
                                        listener.onHttpResponse(requestCode, false, data3);
                                    }
                                }
                            } catch (Exception e1) {
                                e1.printStackTrace();
                                listener.onHttpResponse(requestCode, false, null);
                            }
                        } else {
                            listener.onHttpResponse(requestCode, false, null);
                        }
                    }
                });
//        loadTestData(url, requestCode, listener);
    }

    private void loadTestData(final String url
            , final int requestCode, ResponseListener listener) {
        if (Constant.URL_USERINFO.equals(url)) {
            UserInfoResponse response = new UserInfoResponse();
            response.data = new UserInfoResponse.DataInfo();
            response.data.name = "张三";
            response.data.city = "北京";
            response.data.company = "company";
            response.data.department_big = "big";
            response.data.department = "department";

            listener.onHttpResponse(requestCode, true, response);
        } else if (Constant.URL_CALLLOG_LIST.equals(url)) {
            CallHistoryResponse response = new CallHistoryResponse();
            List<CallHistoryResponse.CallLogDay> son = new ArrayList<>();
            for (int i = 0; i < 4; i++) {
                CallHistoryResponse.CallLogDay day = new CallHistoryResponse.CallLogDay();
                day.year = 2023;
                day.month = 3 + i;
                day.total_number = 123;
                day.total_time = 6123;
                List<CallHistoryResponse.CallLog> days = new ArrayList<>();
                for (int j = 0; j < 4; j++) {
                    CallHistoryResponse.CallLog callLog = new CallHistoryResponse.CallLog();
                    callLog.day = day.month + "月" + (21 + j);
                    callLog.total_number = j + 4;
                    callLog.total_time = 623;
                    days.add(callLog);
                }
                day.son = days;
                son.add(day);
            }
            CallHistoryResponse.DataInfo data = new CallHistoryResponse.DataInfo();
            response.data = data;
            response.data.son = son;
            listener.onHttpResponse(requestCode, true, response);

        } else if (Constant.URL_CALLPHONE.equals(url)) {
            CallPhoneResponse response = new CallPhoneResponse();
            response.phone = "18701636595";
            listener.onHttpResponse(requestCode, true, response);
        } else if (Constant.URL_LOGIN.equals(url)) {
            LoginResponse response = new LoginResponse();
            LoginResponse.Data data = new LoginResponse.Data();
            data.token = "2u392rjcu8394cnn82934y1238ncjeh182358";
            response.data = data;
            listener.onHttpResponse(requestCode, true, response);
        } else {
//            UserInfoResponse response = new UserInfoResponse();
//            response.name = "张三";
        }

    }

    public interface ResponseListener<T> {
        /**
         * @param requestCode 请求码，自定义，在发起请求的类中可以用requestCode来区分各个请求
         * @param resultJson  服务器返回的Json串
         */
        void onHttpResponse(int requestCode, boolean isSuccess, T resultJson);

        Class getTClass();
    }
}
