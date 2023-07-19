package com.android.callrecorder.feedback;

import android.os.Bundle;
import android.view.View;

import com.android.callrecorder.base.BaseActivity;
import com.android.callrecorder.databinding.ActivityFeedbackBinding;
import com.android.callrecorder.databinding.ActivitySettingBinding;
import com.android.callrecorder.utils.SharedPreferenceUtil;

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

            }
        });
    }

}