package com.android.callrecorder.home.ui.callrecord;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.callrecorder.databinding.FragmentCallRecordBinding;
import com.android.callrecorder.bean.CallItem;
import com.android.callrecorder.widget.MyRecycleViewDecoration;

import java.util.List;

public class CallRecordFragment extends Fragment {

    private FragmentCallRecordBinding binding;
    private CallRecordAdapter callRecordAdapter;

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

        CallRecordViewModel homeViewModel =
                new ViewModelProvider(this).get(CallRecordViewModel.class);

        binding = FragmentCallRecordBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

//        final TextView textView = binding.textHome;
//        homeViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        //功能归类分区方法，必须调用<<<<<<<<<<
        initView();
        initData();
        //功能归类分区方法，必须调用>>>>>>>>>>

        return root;//返回值必须为view
    }


    //UI显示区(操作UI，但不存在数据获取或处理代码，也不存在事件监听代码)<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<

    public void initView() {//必须在onCreateView方法内调用
        callRecordAdapter = new CallRecordAdapter((Activity) getContext());
        binding.recycleView.setAdapter(callRecordAdapter);

        LinearLayoutManager manager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
        binding.recycleView.setLayoutManager(manager);
        binding.recycleView.addItemDecoration(new MyRecycleViewDecoration(getContext(), manager.getOrientation()));
//        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
//        transaction.setMaxLifecycle(this, Lifecycle.State.RESUMED).commit();
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

        List<CallItem> callItems = CallHistoryUtil.getInstance().getDataList(getContext());
//        List<CallItem> callItems = CallHistoryUtil.getInstance().getTestDataList();
        callRecordAdapter.refreshData(callItems);
        if (callItems == null || callItems.size() == 0) {
            binding.tvEmpty.setVisibility(View.VISIBLE);
        } else {
            binding.tvEmpty.setVisibility(View.GONE);
        }
    }



    //Data数据区(存在数据获取或处理代码，但不存在事件监听代码)>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>


}