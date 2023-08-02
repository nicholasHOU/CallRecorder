package com.android.callrecorder.home.ui.callrecord;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.callrecorder.base.BaseActivity;
import com.android.callrecorder.bean.CallItem;
import com.android.callrecorder.bean.CrashLog;
import com.android.callrecorder.bean.response.BaseResponse;
import com.android.callrecorder.bean.response.CallHistoryDayResponse;
import com.android.callrecorder.bean.response.ConfigResponse;
import com.android.callrecorder.config.Constant;
import com.android.callrecorder.config.GlobalConfig;
import com.android.callrecorder.databinding.FragmentCallRecordBinding;
import com.android.callrecorder.http.MyHttpManager;
import com.android.callrecorder.manager.RecordPlayerManager;
import com.android.callrecorder.utils.CrashHandler;
import com.android.callrecorder.utils.StringUtil;
import com.android.callrecorder.widget.MyRecycleViewDecoration;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DayRecordFragment extends CallRecordFragment {

    /**
     * 创建一个Fragment实例
     *
     * @return
     */
    public static DayRecordFragment createInstance(long time,String timeStr) {
        DayRecordFragment fragment = new DayRecordFragment();
        Bundle bundle = new Bundle();
        bundle.putLong("time", time);
        bundle.putString("timeStr", timeStr);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected void initData() {
//        super.initData();

       long time= getArguments().getLong("time");
       String  timeStr= getArguments().getString("timeStr");
       if (!TextUtils.isEmpty(timeStr)){
           binding.tvTitle.setText(timeStr);
       }
        MyHttpManager.ResponseListener responseListener = new MyHttpManager.ResponseListener<CallHistoryDayResponse>() {
            @Override
            public void onHttpResponse(int requestCode, boolean isSuccess, CallHistoryDayResponse resultJson) {
                if (isSuccess && resultJson.son != null && resultJson.son.size() > 0) {//日志上传成功，删除本地存储文件
                    for (CallHistoryDayResponse.CallLogDay day : resultJson.son) {
                        CallItem item = new CallItem();
                        item.phone = day.phone;
                        item.callType = day.callType;
                        item.during = day.during;
                        item.recordPath = day.fileName;
                        item.time = day.time;
                        item.name = day.fileName;
                        callLogs.add(item);
                    }
                    if (callLogs == null || callLogs.size() == 0) {
                        binding.tvEmpty.setVisibility(View.VISIBLE);
                        binding.recycleView.setVisibility(View.GONE);
                    } else {
                        callRecordAdapter.refreshData(callLogs);
                        binding.tvEmpty.setVisibility(View.GONE);
                    }
                } else {
                    binding.tvEmpty.setVisibility(View.VISIBLE);
                    binding.recycleView.setVisibility(View.GONE);
                }
            }

            @Override
            public Class getTClass() {
                return CallHistoryDayResponse.class;
            }
        };
        Map<String, Object> request = new HashMap<>(8);
        request.put("time", time);
        MyHttpManager.getInstance().post(request, Constant.URL_CALLLOG_DAY_LIST, 123, responseListener);
    }
}