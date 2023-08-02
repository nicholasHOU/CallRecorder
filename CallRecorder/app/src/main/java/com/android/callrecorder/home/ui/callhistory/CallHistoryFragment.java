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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.callrecorder.bean.response.CallHistoryResponse;
import com.android.callrecorder.config.Constant;
import com.android.callrecorder.databinding.FragmentCallHistoryBinding;
import com.android.callrecorder.home.MainActivity;
import com.android.callrecorder.http.MyHttpManager;
import com.android.callrecorder.utils.ToastUtil;
import com.android.callrecorder.widget.MyRecycleViewDecoration;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CallHistoryFragment extends Fragment {

    private FragmentCallHistoryBinding binding;
    private CallHistoryAdapter callHistoryAdapter;
    private DatePickerDialog mdialog;
    private int year;
    private int month;
    private List<CallHistoryResponse.CallLogDay> callLogDays;
    private boolean loaded;

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
    public static CallHistoryFragment createInstance() {
        return new CallHistoryFragment();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
//        setContentView(R.layout.fragment_home);
        binding = FragmentCallHistoryBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        getCurrentDate();
        initView();
        return root;//返回值必须为view
    }


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
                .getChildAt(2).setVisibility(View.GONE);//.getChildAt(0)
        mdialog.show();
    }

    @SuppressLint("ResourceType")
    private void createDateDialog() {
        DatePickerDialog.OnDateSetListener onDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                CallHistoryFragment.this.month = month + 1;
                CallHistoryFragment.this.year = year;
//                String date = year+"年"+month+"月"+day+"日";//把日期变成字符串格式显示出来
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
        if (loaded) return;
        binding.tvYear.setText(year + "年");
        binding.tvMonth.setText(month + "月");
        loadCallHistory();
        loaded = true;
    }


    /**
     * 获取网络通话历史记录数据
     */
    private void loadCallHistory() {
        Map params = new HashMap();
        params.put("time",4294967295l);
        MyHttpManager.getInstance().post(params, Constant.URL_CALLLOG_LIST, 124,
                new MyHttpManager.ResponseListener<CallHistoryResponse>() {
                    @Override
                    public void onHttpResponse(int requestCode, boolean isSuccess, CallHistoryResponse resultJson) {
                        if (isSuccess) {
                            callLogDays = resultJson.son;
                            if (callLogDays != null && callLogDays.size() > 0) {
                                showData();
                            } else {
                                binding.tvEmpty.setVisibility(View.VISIBLE);
                                binding.recycleView.setVisibility(View.GONE);
                            }
                        } else {
                            binding.tvEmpty.setVisibility(View.VISIBLE);
                            binding.recycleView.setVisibility(View.GONE);
                            if (resultJson != null && Constant.HttpCode.HTTP_NEED_LOGIN == resultJson.code) {
                                ToastUtil.showToast("登录信息失效，请登录后重试");
                                ((MainActivity)getActivity()).goLogin();
                            } else {
                                ToastUtil.showToast("信息获取失败，请稍后重试");
                            }
                        }
                    }

                    @Override
                    public Class getTClass() {
                        return CallHistoryResponse.class;
                    }
                });
    }

    /**
     * 展示当月数据
     */
    private void showData() {
        binding.tvYear.setText(year + "年");
        binding.tvMonth.setText(month + "月");
        boolean isHasCurrentMonth = false;
        if (callLogDays == null || callLogDays.size() == 0) {
        } else {
            int length = callLogDays.size();
            for (int i = 0; i < length; i++) {
                CallHistoryResponse.CallLogDay day = callLogDays.get(i);
                if (day.year == year && day.month == month) {
                    if (day.son != null && day.son.size() > 0) {
                        isHasCurrentMonth = true;
                        callHistoryAdapter.refreshData(day.son);
                    } else {
                    }
                    int minutes = (day.total_time / 60);
                    int seconds = (day.total_time % 60);
                    String minute = minutes == 0 ? "" : minutes + "分";
                    String second = seconds + "秒";
                    binding.tvCallDuring.setText(minute + second);
                    binding.tvCallRecordCount.setText(day.total_number + "");
                    break;
                }
            }
        }
        if (isHasCurrentMonth) {
            binding.tvEmpty.setVisibility(View.GONE);
            binding.recycleView.setVisibility(View.VISIBLE);
        } else {
            binding.tvEmpty.setVisibility(View.VISIBLE);
            binding.recycleView.setVisibility(View.GONE);
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

}