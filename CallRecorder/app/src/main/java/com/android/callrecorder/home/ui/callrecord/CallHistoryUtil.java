package com.android.callrecorder.home.ui.callrecord;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.CallLog;

import androidx.core.widget.ContentLoadingProgressBar;

import com.android.callrecorder.bean.CallItem;
import com.android.callrecorder.bean.response.BaseResponse;
import com.android.callrecorder.bean.response.UserInfoResponse;
import com.android.callrecorder.config.Constant;
import com.android.callrecorder.http.MyHttpManager;
import com.android.callrecorder.listener.Callback;
import com.android.callrecorder.utils.Logs;
import com.android.callrecorder.utils.SharedPreferenceUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CallHistoryUtil {
    private static volatile CallHistoryUtil instance;

    private CallHistoryUtil() {

    }

    public static CallHistoryUtil getInstance() {
        if (instance == null) {
            synchronized (CallHistoryUtil.class) {
                if (instance == null) {
                    instance = new CallHistoryUtil();
                }
            }
        }
        return instance;
    }

    public List<CallItem> getDataList(Context context) {
        return  getDataList(context,0);
    }

    public List<CallItem> getDataList(Context context,long timeStamp) {
        List<CallItem> list = new ArrayList();
        try{
        // 1.获得ContentResolver
        ContentResolver resolver = context.getContentResolver();
        // 2.利用ContentResolver的query方法查询通话记录数据库
        /**
         * @param uri 需要查询的URI，（这个URI是ContentProvider提供的）
         * @param projection 需要查询的字段
         * @param selection sql语句where之后的语句
         * @param selectionArgs ?占位符代表的数据
         * @param sortOrder 排序方式
         *
         */
//        String selection ="date > ? AND pkg_name = ? ";
        String selection ="date > ? ";
        String[] selectionArgs = new String[]{timeStamp+""};
        Cursor cursor = resolver.query(CallLog.Calls.CONTENT_URI, // 查询通话记录的URI
                new String[]{CallLog.Calls.CACHED_NAME// 通话记录的联系人
                        , CallLog.Calls.NUMBER// 通话记录的电话号码
                        , CallLog.Calls.DATE// 通话记录的日期
                        , CallLog.Calls.DURATION// 通话时长
                        , CallLog.Calls.TYPE}// 通话类型
                , selection, selectionArgs, CallLog.Calls.DEFAULT_SORT_ORDER// 按照时间逆序排列，最近打的最先显示
        );
        // 3.通过Cursor获得数据
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
        while (cursor.moveToNext()) {
            int type = cursor.getInt(cursor.getColumnIndex(CallLog.Calls.TYPE));
            String typeString = "";
            int callType = 0;
            switch (type) {
                case CallLog.Calls.INCOMING_TYPE:
                    typeString = "打入";
                    callType = CallItem.CALLTYPE_IN;
                    break;
                case CallLog.Calls.OUTGOING_TYPE:
                    typeString = "打出";
                    callType =  CallItem.CALLTYPE_OUT;
                    break;
                case CallLog.Calls.MISSED_TYPE:
                    typeString = "未接";
                    callType =  CallItem.CALLTYPE_NO;
//                    continue;//如果是未接，直接过滤掉
                    break;
                case CallLog.Calls.REJECTED_TYPE:
                    typeString = "拒接";
                    callType =  CallItem.CALLTYPE_REJECT;
//                    continue;//如果是未接，直接过滤掉
                    break;
                default:
                    break;
            }
            String name = cursor.getString(cursor.getColumnIndex(CallLog.Calls.CACHED_NAME));
            String number = cursor.getString(cursor.getColumnIndex(CallLog.Calls.NUMBER));
            long dateLong = cursor.getLong(cursor.getColumnIndex(CallLog.Calls.DATE));
            String date = format.format(new Date(dateLong));
            int duration = cursor.getInt(cursor.getColumnIndex(CallLog.Calls.DURATION));
            CallItem callItem = new CallItem();
            callItem.phone = number;
            callItem.name = name;
            callItem.time = dateLong;
            callItem.timeStr = date;
            callItem.during = duration;
            int minutes = (duration / 60);
            int seconds = (duration % 60);
            String minute = minutes == 0 ? "" : minutes + "分";
            String second =  seconds + "秒";
            callItem.duringStr = minute+second;
            callItem.callType = callType;

            callItem.recordPath = getRecordPath();
            list.add(callItem);

//            Map<String, String> map = new HashMap<String, String>();
//            map.put("name", (name == null) ? "未备注联系人" : name);
//            map.put("number", number);
//            map.put("date", date);
//            map.put("duration", (duration / 60) + "分钟");
//            map.put("type", typeString);
//            list.add(map);
        }
        }catch (Exception e ){
            e.printStackTrace();
        }
        return list;
    }

    private String getRecordPath() {
        String recordPath = "";

        return recordPath;
    }


    public List<CallItem> getTestDataList() {
        List<CallItem> list = new ArrayList<>(8);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
        String date = format.format(System.currentTimeMillis());
        int duration = 300;
        for (int i = 0; i < 18; i++) {
            CallItem callItem = new CallItem();
            callItem.phone = (18701660000l + i) + "";
            callItem.name = "张三" + i;
            callItem.timeStr = date;
            int minutes = (duration + i / 60);
            int seconds = (duration + i % 60);
            String minute = minutes == 0 ? "" : minutes + "分";
            String second = seconds + "秒";
            callItem.duringStr = minute + second;
            callItem.callType = i % 3;
            list.add(callItem);
        }
        return list;
    }

    public void uploadCallLogData(List<CallItem> callLogs, Callback callback) {
        Map params = new HashMap();
        params.put("son", callLogs);
        Logs.e("calllogs ", callLogs.toString());
        MyHttpManager.getInstance().post(params, Constant.URL_CALLLOG_UPLOAD, 125,
                new MyHttpManager.ResponseListener<BaseResponse>() {
                    @Override
                    public void onHttpResponse(int requestCode, boolean isSuccess, BaseResponse resultJson) {
                        if (isSuccess) {
                            // 已上传成功的更新上传时间戳
                            SharedPreferenceUtil.getInstance().setCallLogUploadTime(System.currentTimeMillis());
                            if (callback!=null){
                                callback.call(true);
                            }
                        } else {
                            if (resultJson != null && Constant.HttpCode.HTTP_NEED_LOGIN == resultJson.code) {
//                                goLogin();
                            } else {
//                            ToastUtil.showToast("，请稍后重试");
                            }
                            if (callback!=null){
                                callback.call(false);
                            }
                        }
                    }

                    @Override
                    public Class getTClass() {
                        return BaseResponse.class;
                    }
                });
    }

}

