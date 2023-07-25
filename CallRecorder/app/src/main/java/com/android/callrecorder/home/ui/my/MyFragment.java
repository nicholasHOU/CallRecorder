package com.android.callrecorder.home.ui.my;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.android.callrecorder.R;
import com.android.callrecorder.bean.response.LoginResponse;
import com.android.callrecorder.bean.response.UserInfoResponse;
import com.android.callrecorder.config.Constant;
import com.android.callrecorder.config.GlobalConfig;
import com.android.callrecorder.databinding.FragmentMyBinding;
import com.android.callrecorder.feedback.FeedbackActivity;
import com.android.callrecorder.http.MyHttpManager;
import com.android.callrecorder.login.LoginActivity;
import com.android.callrecorder.utils.SharedPreferenceUtil;
import com.android.callrecorder.utils.ToastUtil;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import zuo.biao.library.util.thread.pool.ThreadPoolProxyFactory;

public class MyFragment extends Fragment implements View.OnClickListener {

    private FragmentMyBinding binding;

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
            default:
                break;
        }

    }


    private void initData() {
        MyHttpManager.getInstance().post(new HashMap<>(), Constant.URL_USERINFO, 124,
                (MyHttpManager.ResponseListener<UserInfoResponse>) (requestCode, isSuccess, resultJson) -> {
                    if (isSuccess) {
                        GlobalConfig.username = resultJson.name;
                        binding.tvName.setText(resultJson.name);
                        binding.tvGroup.setText(resultJson.city + " " + resultJson.company + " " +
                                resultJson.department_big + " " + resultJson.department);
                    } else {
                        if (resultJson != null && Constant.HttpCode.HTTP_NEED_LOGIN == resultJson.code) {
                            ToastUtil.showToast("登录信息失效，请登录后重试");
                            goLogin();
                        } else {
                            ToastUtil.showToast("信息获取失败，请稍后重试");
                        }
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
     */
    private void uploadRecord() {

        ThreadPoolProxyFactory.getCacheThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                Map params = new HashMap();
                params.put("time","");
                params.put("during","");
                params.put("phone","");
                params.put("name","");
                params.put("callType","");
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
    private void uploadFile(Map params){

        MyHttpManager.getInstance().post(params, Constant.URL_UPLOAD_RECORD_ALL, 125,
                (MyHttpManager.ResponseListener<UserInfoResponse>) (requestCode, isSuccess, resultJson) -> {
                    if (isSuccess) {
                      // 已上传成功的更新上传时间戳
                        SharedPreferenceUtil.getInstance().setRecordUploadTime((Long) params.get("time"));
                    } else {
                        if (resultJson != null && Constant.HttpCode.HTTP_NEED_LOGIN == resultJson.code) {
                            goLogin();
                        } else {
//                            ToastUtil.showToast("，请稍后重试");
                        }
                    }
                });
    }

    private void goLogin() {
        Intent intent = new Intent(getContext(), LoginActivity.class);
        startActivity(intent);
        getActivity().finish();
    }

    private void goFeedback() {
        Intent intent = new Intent(getContext(), FeedbackActivity.class);
        startActivity(intent);
    }
}