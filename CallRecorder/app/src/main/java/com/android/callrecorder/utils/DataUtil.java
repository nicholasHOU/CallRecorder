package com.android.callrecorder.utils;

import android.text.TextUtils;

import com.android.callrecorder.base.AppApplication;
import com.android.callrecorder.bean.CallItem;
import com.android.callrecorder.bean.response.BaseResponse;
import com.android.callrecorder.bean.response.ConfigResponse;
import com.android.callrecorder.config.Constant;
import com.android.callrecorder.config.GlobalConfig;
import com.android.callrecorder.file.FileUploader;
import com.android.callrecorder.http.MyHttpManager;
import com.android.callrecorder.listener.Callback;

import java.util.HashMap;
import java.util.Map;

import zuo.biao.library.util.thread.pool.ThreadPoolProxyFactory;

public class DataUtil {

    public static void uploadRecord(CallItem recordFile) {
        ThreadPoolProxyFactory.getDefaultThreadPool().execute(new Runnable() {
            @Override
            public void run() {
//                String name = TextUtils.isEmpty(recordFile.name)?recordFile.phone:recordFile.name;
                String name = recordFile.recordFileName;
                FileUploader.getInstance().uploadFile(name,recordFile.recordPath, new FileUploader.CallBack() {
                    @Override
                    public void onSuccess(String eTag, String requstId) {
                        SharedPreferenceUtil.getInstance().setRecordUploadTime(recordFile.time);

//                        Map params = new HashMap();
//                        params.put("time", recordFile.time);
//                        params.put("during", recordFile.during);
//                        params.put("phone", recordFile.phone);
//                        params.put("name", recordFile.name);
//                        params.put("callType", recordFile.callType);
////                        params.put("video", FileUtil.getRecordFile(recordFile.recordPath));
//                        params.put("eTag",eTag);
//                        params.put("requstId",requstId);
//                        params.put("fileName",name);
//                        DataUtil.uploadFile(params);
                    }

                    @Override
                    public void onFail(String errorCode, String requstId) {

                    }
                });
            }
        });
    }


    /**
     * “time”:13123131,  //时间戳
     * “video”: file,  //视频文件
     * “long”: 12312,  // 视频的长度
     * “phone”: 13211111111, // 手机号
     * “back”: “手机号的备注” // 手机号备注   可以为空
     */
//    public static  void uploadFile(Map params) {
//        MyHttpManager.getInstance().post(params, Constant.URL_UPLOAD_RECORD, 125,
//                new MyHttpManager.ResponseListener<BaseResponse>() {
//                    @Override
//                    public void onHttpResponse(int requestCode, boolean isSuccess, BaseResponse resultJson) {
//                        if (isSuccess) {
//                            // 已上传成功的更新上传时间戳
//                            SharedPreferenceUtil.getInstance().setRecordUploadTime((Long) params.get("time"));
//                        } else {
//                            if (resultJson != null && Constant.HttpCode.HTTP_NEED_LOGIN == resultJson.code) {
////                                goLogin();
//                            } else {
////                            ToastUtil.showToast("，请稍后重试");
//                            }
//                        }
//                    }
//
//                    @Override
//                    public Class getTClass() {
//                        return BaseResponse.class;
//                    }
//                });
//    }


    /**
     * 获取全局配置信息
     * @param callback
     */
    public static void loadConfigData(Callback callback) {
        Map params = new HashMap();
        MyHttpManager.getInstance().post(params, Constant.URL_CONFIG, 125,
                new MyHttpManager.ResponseListener<ConfigResponse>() {
                    @Override
                    public void onHttpResponse(int requestCode, boolean isSuccess, ConfigResponse resultJson) {
                        if (isSuccess) {
                            if (!TextUtils.isEmpty(resultJson.data.url)) {
                                GlobalConfig.url = resultJson.data.url;
                                SharedPreferenceUtil.getInstance().setRecordFilepath(GlobalConfig.url);
                            }else {
                                String filePath = SharedPreferenceUtil.getInstance().getRecordFilepath();
                                if (!TextUtils.isEmpty(filePath)) {
                                    GlobalConfig.url = filePath;
                                }
                            }
                            if (resultJson.data.runTime > 2000) {
                                GlobalConfig.runTime = resultJson.data.runTime;
                            }
                            if (callback!=null){
                                callback.call(!TextUtils.isEmpty(GlobalConfig.url));
                            }
                            if (resultJson.data.aliyun!=null) {
                                GlobalConfig.SecurityToken = resultJson.data.aliyun.SecurityToken;
                                GlobalConfig.AccessKeyId = resultJson.data.aliyun.AccessKeyId;
                                GlobalConfig.AccessKeySecret = resultJson.data.aliyun.AccessKeySecret;
                                GlobalConfig.Expiration = resultJson.data.aliyun.Expiration;
                                FileUploader.getInstance().init(AppApplication.app);
                            }
                        } else {
                            if (resultJson != null && Constant.HttpCode.HTTP_NEED_LOGIN == resultJson.code) {
//                                ((BaseActivity)getActivity()).goLogin();
                            } else {
                            }
                            if (callback!=null){
                                callback.call(false);
                            }
                            String filePath = SharedPreferenceUtil.getInstance().getRecordFilepath();
                            if (!TextUtils.isEmpty(filePath)) {
                                GlobalConfig.url = filePath;
                            }
                        }
                    }

                    @Override
                    public Class getTClass() {
                        return ConfigResponse.class;
                    }
                });
    }

}
