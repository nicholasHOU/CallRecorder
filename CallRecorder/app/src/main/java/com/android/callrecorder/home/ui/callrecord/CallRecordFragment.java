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
import com.android.callrecorder.bean.response.ConfigResponse;
import com.android.callrecorder.config.Constant;
import com.android.callrecorder.config.GlobalConfig;
import com.android.callrecorder.databinding.FragmentCallRecordBinding;
import com.android.callrecorder.http.MyHttpManager;
import com.android.callrecorder.manager.RecordPlayerManager;
import com.android.callrecorder.utils.StringUtil;
import com.android.callrecorder.widget.MyRecycleViewDecoration;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CallRecordFragment extends Fragment {

    private FragmentCallRecordBinding binding;
    private CallRecordAdapter callRecordAdapter;
    private List<CallItem> callLogs = new ArrayList<>();
    private SimpleDateFormat dateFormat;

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }


    /**
     * 创建一个Fragment实例
     *
     * @return
     */
    public static CallRecordFragment createInstance() {
        return new CallRecordFragment();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
//        setContentView(R.layout.fragment_home);
        binding = FragmentCallRecordBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        initView();
        initData();
        dateFormat = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");

        return root;//返回值必须为view
    }

    public void initView() {//必须在onCreateView方法内调用
        callRecordAdapter = new CallRecordAdapter((Activity) getContext());
        binding.recycleView.setAdapter(callRecordAdapter);

        LinearLayoutManager manager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
        binding.recycleView.setLayoutManager(manager);
        binding.recycleView.addItemDecoration(new MyRecycleViewDecoration(getContext(), manager.getOrientation()));
    }


    @Override
    public void onResume() {
        super.onResume();
        initData();
    }

    /**
     * 页面可见再次刷新调用，获取最新的通话记录情况
     */
    public void initData() {//必须在onCreateView方法内调用
        loadConfigData();

    }


    private void loadConfigData() {
        Map params = new HashMap();
        MyHttpManager.getInstance().post(params, Constant.URL_CONFIG, 125,
                new MyHttpManager.ResponseListener<ConfigResponse>() {
                    @Override
                    public void onHttpResponse(int requestCode, boolean isSuccess, ConfigResponse resultJson) {
                        if (isSuccess) {
                            if (!TextUtils.isEmpty(resultJson.data.url)) {
                                GlobalConfig.url = resultJson.data.url;
                            }
                            if (resultJson.data.runTime > 2000) {
                                GlobalConfig.runTime = resultJson.data.runTime;
                            }
                            loadRecordFile();
                        } else {
                            if (resultJson != null && Constant.HttpCode.HTTP_NEED_LOGIN == resultJson.code) {
                                ((BaseActivity)getActivity()).goLogin();
                            } else {
                            }
                        }
                    }

                    @Override
                    public Class getTClass() {
                        return ConfigResponse.class;
                    }
                });
    }

    private void loadRecordFile() {
        if (TextUtils.isEmpty(GlobalConfig.url)) {
            binding.tvEmpty.setVisibility(View.VISIBLE);
            binding.recycleView.setVisibility(View.GONE);
        } else {
            loadLocalRecordFile();
            if (callLogs == null || callLogs.size() == 0) {
                binding.tvEmpty.setVisibility(View.VISIBLE);
            } else {
                callRecordAdapter.refreshData(callLogs);
                binding.tvEmpty.setVisibility(View.GONE);
            }
        }
    }

    private void loadLocalRecordFile() {
        callLogs.clear();
        File file = new File(GlobalConfig.url);
        if (file.isDirectory()) {
            File[] filesChilds = file.listFiles();
            for (File recordFile : filesChilds) {
                CallItem callItem = null;
                if (file.isDirectory()) {
                    File[] files = file.listFiles();
                    for (File recordItem : files) {
                        callItem = getRecordInfo(recordItem);
                        callItem.name = recordItem.getName();
                        callLogs.add(callItem);
                    }
                } else {
                    callItem = getRecordInfo(recordFile);
                    callLogs.add(callItem);
                }
            }
        }
    }

    private CallItem getRecordInfo(File recordFile) {
        String callRecordPath = recordFile.getAbsolutePath();
        int duration = RecordPlayerManager.getInstance().getDuration(callRecordPath);
        CallItem callItem = new CallItem();
        callItem.phone = StringUtil.checkNum(callRecordPath);
        callItem.name = "";
        callItem.time = recordFile.lastModified();
        String date = dateFormat.format(callItem.time);
        callItem.timeStr = date;

        callItem.during = duration;
        int minutes = (duration / 60);
        int seconds = (duration % 60);
        String minute = minutes == 0 ? "" : minutes + "分";
        String second = seconds + "秒";
        callItem.duringStr = minute + second;
//                callItem.callType = callType;
        callItem.recordPath = callRecordPath;
        return callItem;
    }

    //Data数据区(存在数据获取或处理代码，但不存在事件监听代码)>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>


}