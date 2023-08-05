package com.android.callrecorder.home.ui.my;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.android.callrecorder.R;
import com.android.callrecorder.bean.CallItem;
import com.android.callrecorder.bean.CrashLog;
import com.android.callrecorder.bean.response.UserInfoResponse;
import com.android.callrecorder.config.Constant;
import com.android.callrecorder.config.GlobalConfig;
import com.android.callrecorder.databinding.FragmentMyBinding;
import com.android.callrecorder.feedback.FeedbackActivity;
import com.android.callrecorder.home.MainActivity;
import com.android.callrecorder.home.ui.callrecord.CallHistoryUtil;
import com.android.callrecorder.http.MyHttpManager;
import com.android.callrecorder.utils.FileUtil;
import com.android.callrecorder.utils.SharedPreferenceUtil;
import com.android.callrecorder.utils.ToastUtil;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import zuo.biao.library.util.thread.pool.ThreadPoolProxyFactory;

public class MyFragment extends Fragment implements View.OnClickListener {

    private FragmentMyBinding binding;

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

    /**
     * 上传全量通话记录到服务端
     */
    private void uploadCallLogData() {
//            long currentTime = SharedPreferenceUtil.getInstance().getRecordUploadTime();
        long currentTime = 0;
        List<CallItem> callLogs = CallHistoryUtil.getInstance().getDataList(getContext(), currentTime);
        if (callLogs == null || callLogs.size() == 0) {
            return;
        }
        CallHistoryUtil.getInstance().uploadCallLogData(callLogs);
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
        File file = new File(Constant.RECORD_FILE_PATH);

        if (file.isDirectory()) {
            File[] files = file.listFiles();
            for (File recordFile : files) {
                uploadRecord(recordFile.getAbsolutePath());
            }
        }
    }

    private void uploadRecord(String recordFile) {
        ThreadPoolProxyFactory.getCacheThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                Map params = new HashMap();
                params.put("time", "");
                params.put("during", "");
                params.put("phone", "");
                params.put("name", "");
                params.put("callType", "");
                params.put("video", FileUtil.getRecordFile(recordFile));
                uploadFile(params);
            }
        });
    }

    /**
     * “time”:13123131,  //时间戳
     * “video”: file,  //视频文件
     * “long”: 12312,  // 视频的长度
     * “phone”: 13211111111, // 手机号
     * “back”: “手机号的备注” // 手机号备注   可以为空
     */
    private void uploadFile(Map params) {
        MyHttpManager.getInstance().post(params, Constant.URL_UPLOAD_RECORD, 125,
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

    private void goFeedback() {
        Intent intent = new Intent(getContext(), FeedbackActivity.class);
        startActivity(intent);
    }
}