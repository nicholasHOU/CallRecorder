package com.android.callrecorder.http;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.T;

import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.android.callrecorder.bean.response.BaseResponse;
import com.android.callrecorder.config.Constant;
import com.android.callrecorder.config.GlobalConfig;

import java.util.HashMap;
import java.util.Map;

import zuo.biao.library.interfaces.OnHttpResponseListener;
import zuo.biao.library.manager.HttpManager;

public class MyHttpManager {

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

    private MyHttpManager(){
    }

    public void post(final Map<String, Object> request, final String url
            , final int requestCode, final ResponseListener listener) {
        Map<String,String> headers = new HashMap<>();
        headers.put(Constant.K_TOKEN, GlobalConfig.token);
        headers.put(Constant.K_TYPE, GlobalConfig.type);
        headers.put(Constant.K_EXTRA, GlobalConfig.extra);
        HttpManager.getInstance().post(request, headers,url,
                true, requestCode, new OnHttpResponseListener() {
            @Override
            public void onHttpResponse(int requestCode, String resultJson, Exception e) {
                if (!TextUtils.isEmpty(resultJson)) {
                    BaseResponse data = JSON.parseObject(resultJson, BaseResponse.class);
                    if (com.android.callrecorder.config.Constant.HttpCode.HTTP_SUCCESS == data.code) {
                        listener.onHttpResponse(requestCode, true, T);
                    } else {
                        listener.onHttpResponse(requestCode, false, T);
                    }
                } else {
                    listener.onHttpResponse(requestCode, false, null);
                }
            }
        });

    }

    public interface ResponseListener<T> {
        /**
         * @param requestCode 请求码，自定义，在发起请求的类中可以用requestCode来区分各个请求
         * @param resultJson  服务器返回的Json串
         */
        void onHttpResponse(int requestCode, boolean isSuccess, T resultJson);
    }
}
