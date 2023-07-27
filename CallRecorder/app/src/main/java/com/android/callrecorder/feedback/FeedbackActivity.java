package com.android.callrecorder.feedback;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.android.callrecorder.base.BaseActivity;
import com.android.callrecorder.bean.response.BaseResponse;
import com.android.callrecorder.bean.response.UserInfoResponse;
import com.android.callrecorder.config.Constant;
import com.android.callrecorder.databinding.ActivityFeedbackBinding;
import com.android.callrecorder.databinding.ActivitySettingBinding;
import com.android.callrecorder.http.MyHttpManager;
import com.android.callrecorder.login.LoginActivity;
import com.android.callrecorder.utils.SharedPreferenceUtil;
import com.android.callrecorder.utils.ToastUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * 服务器重置
 */
public class FeedbackActivity extends BaseActivity {

    private ActivityFeedbackBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFeedbackBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initView();
    }

    private void initView() {
        binding.ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        binding.llCommit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //重置服务器地址，重启app
                String content = binding.etContent.getText() == null ? "" : binding.etContent.getText().toString();
                Map params = new HashMap();
                params.put("content",content);
                uploadFeedback(params);
            }
        });
    }

    private void uploadFeedback(Map params) {
        MyHttpManager.getInstance().post(params, Constant.URL_FEEDBACK, 125,
                new MyHttpManager.ResponseListener<BaseResponse>() {
                    @Override
                    public void onHttpResponse(int requestCode, boolean isSuccess, BaseResponse resultJson) {
                        if (isSuccess) {
                            ToastUtil.showToast("提交成功，谢谢您的反馈");
                            finish();
                        } else {
                            if (resultJson != null && Constant.HttpCode.HTTP_NEED_LOGIN == resultJson.code) {
                                ToastUtil.showToast("登录信息失效，请登录后重试");
                                goLogin();
                            } else {
                                ToastUtil.showToast("提交失败，请稍后重试");
                            }
                        }
                    }

                    @Override
                    public Class getTClass() {
                        return BaseResponse.class;
                    }
                });
    }

    private void goLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

}