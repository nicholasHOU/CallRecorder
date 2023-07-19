package com.android.callrecorder.home.ui.callhistory;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.callrecorder.databinding.FragmentCallHistoryBinding;
import com.android.callrecorder.bean.CallItem;
import com.android.callrecorder.home.MainActivity;
import com.android.callrecorder.home.ui.callrecord.CallHistoryUtil;
import com.android.callrecorder.home.ui.callrecord.CallRecordFragment;
import com.android.callrecorder.home.ui.callrecord.CallRecordViewModel;
import com.android.callrecorder.utils.Logs;
import com.android.callrecorder.widget.MyRecycleViewDecoration;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class CallHistoryFragment extends Fragment {

    private FragmentCallHistoryBinding binding;
    private CallHistoryAdapter callHistoryAdapter;
    private DatePickerDialog mdialog;
    private int year;
    private int month;

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

        binding = FragmentCallHistoryBinding.inflate(inflater, container, false);
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
        callHistoryAdapter = new CallHistoryAdapter((Activity) getContext());
        binding.recycleView.setAdapter(callHistoryAdapter);

        LinearLayoutManager manager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
        binding.recycleView.setLayoutManager(manager);
        binding.recycleView.addItemDecoration(new MyRecycleViewDecoration(getContext(), manager.getOrientation()));
//        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
//        transaction.setMaxLifecycle(this, Lifecycle.State.RESUMED).commit();

        binding.tvMonth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });
    }

    /**
     * 展示日期选择控件
     */
    private void showDatePickerDialog() {
        createDateDialog();
        DatePicker dp = mdialog.getDatePicker();// 设置弹出年月日
        ((ViewGroup) ((ViewGroup) dp.getChildAt(0)).getChildAt(0))
                .getChildAt(1).setVisibility(View.GONE);//.getChildAt(0)
        mdialog.show();
    }

    @SuppressLint("ResourceType")
    private void createDateDialog() {
        DatePickerDialog.OnDateSetListener onDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month + 1;
//                String date = year+"年"+month+"月"+day+"日";//把日期变成字符串格式显示出来
                binding.tvYear.setText(year + "年");
                binding.tvMonth.setText(month + "月");
            }
        };
        if (mdialog == null) {
            mdialog = new DatePickerDialog(getContext(), 3, onDateSetListener, year, month, 1);
        }
    }

    /**
     * 从当前Dialog中查找DatePicker子控件
     *
     * @param group
     * @return
     */
    private DatePicker findDatePicker(ViewGroup group) {
        if (group != null) {
            for (int i = 0, j = group.getChildCount(); i < j; i++) {
                View child = group.getChildAt(i);
                if (child instanceof DatePicker) {
                    return (DatePicker) child;
                } else if (child instanceof ViewGroup) {
                    DatePicker result = findDatePicker((ViewGroup) child);
                    if (result != null)
                        return result;
                }
            }
        }
        return null;
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
        getCurrentDate();
        binding.tvYear.setText(year + "年");
        binding.tvMonth.setText(month + "月");

        List<CallItem> callItems = CallHistoryUtil.getInstance().getDataList(getContext());
//        List<CallItem> callItems = CallHistoryUtil.getInstance().getTestDataList();
        callHistoryAdapter.refreshData(callItems);
        if (callItems == null || callItems.size() == 0) {
            binding.tvEmpty.setVisibility(View.VISIBLE);
        } else {
            binding.tvEmpty.setVisibility(View.GONE);
        }


    }


    /**
     * @return
     */
    private void getCurrentDate() {
        Calendar calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH) + 1;
//        int day = calendar.get(Calendar.DAY_OF_MONTH);
    }


    //Data数据区(存在数据获取或处理代码，但不存在事件监听代码)>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>


}