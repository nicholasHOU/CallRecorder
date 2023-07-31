package com.android.callrecorder.home.ui.callrecord;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.callrecorder.bean.response.UserInfoResponse;
import com.android.callrecorder.config.Constant;
import com.android.callrecorder.databinding.FragmentCallRecordBinding;
import com.android.callrecorder.bean.CallItem;
import com.android.callrecorder.http.MyHttpManager;
import com.android.callrecorder.utils.SharedPreferenceUtil;
import com.android.callrecorder.widget.MyRecycleViewDecoration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CallRecordFragment extends Fragment {

    private FragmentCallRecordBinding binding;
    private CallRecordAdapter callRecordAdapter;
    private List<CallItem> callLogs;

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
        return root;//返回值必须为view
    }

    public void initView() {//必须在onCreateView方法内调用
        callRecordAdapter = new CallRecordAdapter((Activity) getContext());
        binding.recycleView.setAdapter(callRecordAdapter);

        LinearLayoutManager manager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
        binding.recycleView.setLayoutManager(manager);
        binding.recycleView.addItemDecoration(new MyRecycleViewDecoration(getContext(), manager.getOrientation()));

        binding.tvTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadCallLog();
            }


        });
    }
    private void uploadCallLog() {
        if (callLogs==null||callLogs.size()==0){
            return;
        }
        Map params = new HashMap();
        params.put("callLog", callLogs);

        MyHttpManager.getInstance().post(params, Constant.URL_CALLLOG_UPLOAD, 125,
                new MyHttpManager.ResponseListener<UserInfoResponse>() {
                    @Override
                    public void onHttpResponse(int requestCode, boolean isSuccess, UserInfoResponse resultJson) {
                        if (isSuccess) {
                            // 已上传成功的更新上传时间戳
                            SharedPreferenceUtil.getInstance().setRecordUploadTime((Long) params.get("time"));
                        } else {
                            if (resultJson != null && Constant.HttpCode.HTTP_NEED_LOGIN == resultJson.code) {
//                                goLogin();
                            } else {
//                            ToastUtil.showToast("，请稍后重试");
                            }
                        }
                    }

                    @Override
                    public Class getTClass() {
                        return UserInfoResponse.class;
                    }
                });
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

        callLogs = CallHistoryUtil.getInstance().getDataList(getContext());
//        List<CallItem> callItems = CallHistoryUtil.getInstance().getTestDataList();
        callRecordAdapter.refreshData(callLogs);
        if (callLogs == null || callLogs.size() == 0) {
            binding.tvEmpty.setVisibility(View.VISIBLE);
        } else {
            binding.tvEmpty.setVisibility(View.GONE);
        }
    }



    //Data数据区(存在数据获取或处理代码，但不存在事件监听代码)>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>


}