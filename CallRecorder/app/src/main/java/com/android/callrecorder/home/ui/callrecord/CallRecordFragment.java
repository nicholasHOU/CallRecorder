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

import com.android.callrecorder.bean.CallItem;
import com.android.callrecorder.config.GlobalConfig;
import com.android.callrecorder.databinding.FragmentCallRecordBinding;
import com.android.callrecorder.listener.Callback;
import com.android.callrecorder.utils.DataUtil;
import com.android.callrecorder.utils.FileUtil;
import com.android.callrecorder.widget.MyRecycleViewDecoration;

import java.util.ArrayList;
import java.util.List;

public class CallRecordFragment extends Fragment {

    protected FragmentCallRecordBinding binding;
    protected CallRecordAdapter callRecordAdapter;
    protected List<CallItem> callLogs = new ArrayList<>();

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
        CallRecordFragment fragment = new CallRecordFragment();
//        Bundle bundle = new Bundle();
//        bundle.putLong("time", time);
//        fragment.setArguments(bundle);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
//        setContentView(R.layout.fragment_home);
        binding = FragmentCallRecordBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        initView();
        initData();

        return root;//返回值必须为view
    }

    public void initView() {//必须在onCreateView方法内调用
        callRecordAdapter = new CallRecordAdapter((Activity) getContext());
        binding.recycleView.setAdapter(callRecordAdapter);

        LinearLayoutManager manager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
        binding.recycleView.setLayoutManager(manager);
        binding.recycleView.addItemDecoration(new MyRecycleViewDecoration(getContext(), manager.getOrientation()));
    }


//    @Override
//    public void onResume() {
//        super.onResume();
//        initData();
//    }

    /**
     * 页面可见再次刷新调用，获取最新的通话记录情况
     */
    protected void initData() {//必须在onCreateView方法内调用
        DataUtil.loadConfigData(new Callback() {
            @Override
            public void call(boolean isSuccess) {
                if (isSuccess){
                    loadRecordFile();
                }
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
        callLogs.addAll(FileUtil.loadLocalRecordFile());
    }

}