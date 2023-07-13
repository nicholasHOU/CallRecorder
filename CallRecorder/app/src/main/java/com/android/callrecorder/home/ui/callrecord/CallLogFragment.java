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

import com.android.callrecorder.databinding.FragmentHomeBinding;
import com.android.callrecorder.home.bean.CallItem;
import com.android.callrecorder.widget.MyRecycleViewDecoration;

import java.util.ArrayList;
import java.util.List;

import zuo.biao.library.model.Entry;

public class CallLogFragment extends Fragment {

    private FragmentHomeBinding binding;
    private CallLogAdapter callLogAdapter;

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
    public static CallLogFragment createInstance() {
        return new CallLogFragment();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
//        setContentView(R.layout.fragment_home);

        CallLogViewModel homeViewModel =
                new ViewModelProvider(this).get(CallLogViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
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
        callLogAdapter = new CallLogAdapter((Activity) getContext());
        binding.recycleView.setAdapter(callLogAdapter);

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

//        List<CallItem> callItems = CallHistoryUtil.getInstance().getDataList(getContext());
        List<CallItem> callItems = CallHistoryUtil.getInstance().getTestDataList();
        callLogAdapter.refreshData(callItems);
        if (callItems == null || callItems.size() == 0) {
            binding.tvEmpty.setVisibility(View.VISIBLE);
        } else {
            binding.tvEmpty.setVisibility(View.GONE);
        }
    }

    public void getListAsync(int page) {

        //示例代码<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<

        List<Entry<String, String>> list = new ArrayList<Entry<String, String>>();
        for (int i = 0; i < 64; i++) {
            list.add(new Entry<String, String>("联系人" + i, String.valueOf(1311736568 + i * i)));
        }

        //示例代码>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
    }


    //Data数据区(存在数据获取或处理代码，但不存在事件监听代码)>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>


}