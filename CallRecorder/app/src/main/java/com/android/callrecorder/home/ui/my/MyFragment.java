package com.android.callrecorder.home.ui.my;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.android.callrecorder.R;
import com.android.callrecorder.bean.CallItem;
import com.android.callrecorder.bean.response.UserInfoResponse;
import com.android.callrecorder.config.Constant;
import com.android.callrecorder.config.GlobalConfig;
import com.android.callrecorder.databinding.FragmentMyBinding;
import com.android.callrecorder.feedback.FeedbackActivity;
import com.android.callrecorder.home.MainActivity;
import com.android.callrecorder.home.ui.callrecord.CallHistoryUtil;
import com.android.callrecorder.http.MyHttpManager;
import com.android.callrecorder.listener.Callback;
import com.android.callrecorder.utils.DataUtil;
import com.android.callrecorder.utils.DialogUtil;
import com.android.callrecorder.utils.FileUtil;
import com.android.callrecorder.utils.SharedPreferenceUtil;
import com.android.callrecorder.utils.ToastUtil;

import java.util.HashMap;
import java.util.List;

public class MyFragment extends Fragment implements View.OnClickListener {

    private FragmentMyBinding binding;
    private Dialog loadingDialog;

    public static MyFragment createInstance() {
        return new MyFragment();
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentMyBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        initView();
        return root;
    }

    private void initView() {
        binding.rlClearRecord.setOnClickListener(this);
        binding.rlFeedback.setOnClickListener(this);
        binding.rlUploadRecord.setOnClickListener(this);
        binding.rlUploadCalllog.setOnClickListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        initData();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_feedback:
                //jump Feedback页面
                goFeedback();
                break;
            case R.id.rl_clear_record:
                //clear path
                clearRecord();
                break;
            case R.id.rl_upload_record:
                //upload record
                uploadRecord();
                break;
            case R.id.rl_upload_calllog:
                //upload uploadCallLogData
                uploadCallLogData();
                break;
            default:
                break;
        }

    }

    private void initData() {
        MyHttpManager.getInstance().post(new HashMap<>(), Constant.URL_USERINFO, 124,
                new MyHttpManager.ResponseListener<UserInfoResponse>() {
                    @Override
                    public void onHttpResponse(int requestCode, boolean isSuccess, UserInfoResponse resultJson) {
                        if (isSuccess) {
                            GlobalConfig.username = resultJson.data.name;
                            binding.tvName.setText(resultJson.data.name);
                            binding.tvGroup.setText(resultJson.data.city + " " + resultJson.data.company + " " +
                                    resultJson.data.department_big + " " + resultJson.data.department);
                        } else {
                            if (resultJson != null && Constant.HttpCode.HTTP_NEED_LOGIN == resultJson.code) {
                                ToastUtil.showToast("登录信息失效，请登录后重试");
                                ((MainActivity) getActivity()).goLogin();
                            } else {
                                ToastUtil.showToast("信息获取失败，请稍后重试");
                            }
                        }
                    }

                    @Override
                    public Class getTClass() {
                        return UserInfoResponse.class;
                    }
                });
    }

    /**
     * 清除录音
     */
    private void clearRecord() {

    }

    /**
     * 上传录音
     * String filePath = Constant.RECORD_FILE_PATH + "/1689647006883_18032408866.amr";
     */
    private void uploadRecord() {
//        File file = new File(Constant.RECORD_FILE_PATH);
        List<CallItem> callLogs = FileUtil.loadLocalRecordFile();
        if (callLogs.size() > 0) {
            for (CallItem callItem : callLogs) {
                DataUtil.uploadRecord(callItem);
            }
            ToastUtil.showToast("录音文件后台上传中");
            SharedPreferenceUtil.getInstance().setCallLogUploadTime(System.currentTimeMillis());
        } else {
            ToastUtil.showToast("暂无需要上传的通话录音文件");
        }
    }

    /**
     * 上传全量通话记录到服务端
     */
    private void uploadCallLogData() {
            long currentTime = SharedPreferenceUtil.getInstance().getCallLogUploadTime();
//        long currentTime = 0;
        List<CallItem> callLogs = CallHistoryUtil.getInstance().getDataList(getContext(), currentTime);
        if (callLogs == null || callLogs.size() == 0) {
            ToastUtil.showToast("暂无需要上传的通话记录");
            return;
        }
        if (loadingDialog==null){
            loadingDialog = DialogUtil.createLoadingDialog(getContext(),"");
        }else{
            loadingDialog.show();
        }
        CallHistoryUtil.getInstance().uploadCallLogData(callLogs, new Callback() {
            @Override
            public void call(boolean isSuccess) {
                if (loadingDialog != null) {
                    loadingDialog.dismiss();
                }
                if (isSuccess){
                    ToastUtil.showToast("上传成功");
                }
                SharedPreferenceUtil.getInstance().setRecordUploadTime(System.currentTimeMillis());
            }
        });
    }


    private void goFeedback() {
        Intent intent = new Intent(getContext(), FeedbackActivity.class);
        startActivity(intent);
    }
}