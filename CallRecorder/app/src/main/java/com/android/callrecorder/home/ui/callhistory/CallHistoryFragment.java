package com.android.callrecorder.home.ui.callhistory;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.callrecorder.bean.response.CallHistoryResponse;
import com.android.callrecorder.bean.response.UserInfoResponse;
import com.android.callrecorder.config.Constant;
import com.android.callrecorder.config.GlobalConfig;
import com.android.callrecorder.databinding.FragmentCallHistoryBinding;
import com.android.callrecorder.bean.CallItem;
import com.android.callrecorder.home.MainActivity;
import com.android.callrecorder.home.ui.callrecord.CallHistoryUtil;
import com.android.callrecorder.home.ui.callrecord.CallRecordFragment;
import com.android.callrecorder.home.ui.callrecord.CallRecordViewModel;
import com.android.callrecorder.http.MyHttpManager;
import com.android.callrecorder.login.LoginActivity;
import com.android.callrecorder.utils.Logs;
import com.android.callrecorder.utils.ToastUtil;
import com.android.callrecorder.widget.MyRecycleViewDecoration;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class CallHistoryFragment extends Fragment {

    private FragmentCallHistoryBinding binding;
    private CallHistoryAdapter callHistoryAdapter;
    private DatePickerDialog mdialog;
    private int year;
    private int month;
    private List<CallHistoryResponse.CallLogDay> callLogDays;

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
                showData();
            }
        };
        if (mdialog == null) {
            mdialog = new DatePickerDialog(getContext(), 3, onDateSetListener, year, month, 1);
        }
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
        loadCallHistory();
    }


    /**
     * 获取网络通话历史记录数据
     */
    private void loadCallHistory() {
        MyHttpManager.getInstance().post(new HashMap<>(), Constant.URL_CALLLOG_LIST, 124,
                (MyHttpManager.ResponseListener<CallHistoryResponse>) (requestCode, isSuccess, resultJson) -> {
                    if (isSuccess) {
                        callLogDays = resultJson.son;
                        if (callLogDays !=null&& callLogDays.size()>0){
                            showData();
                        }else {
                            binding.tvEmpty.setVisibility(View.VISIBLE);
                        }
                    } else {
                        if (resultJson != null && Constant.HttpCode.HTTP_NEED_LOGIN == resultJson.code) {
                            ToastUtil.showToast("登录信息失效，请登录后重试");
                            goLogin();
                        } else {
                            ToastUtil.showToast("信息获取，请稍后重试");
                        }
                    }
                });
    }

    /**
     * 展示当月数据
     */
    private void showData() {
        int length = callLogDays.size();
        boolean isHasCurrentMonth = false;
        for (int i = 0; i < length; i++) {
            CallHistoryResponse.CallLogDay day = callLogDays.get(i);
            if (day.year == year && day.month == month) {
                if (day.son != null && day.son.size() > 0) {
                    isHasCurrentMonth = true;
                    callHistoryAdapter.refreshData(day.son);
                } else {
                }
                break;
            }
        }

        if (isHasCurrentMonth) {
            binding.tvEmpty.setVisibility(View.VISIBLE);
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


    private void goLogin() {
        Intent intent = new Intent(getContext(), LoginActivity.class);
        startActivity(intent);
        getActivity().finish();
    }

}