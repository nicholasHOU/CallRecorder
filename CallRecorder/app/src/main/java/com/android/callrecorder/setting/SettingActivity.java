package com.android.callrecorder.setting;

import android.os.Bundle;
import android.view.View;

import com.android.callrecorder.base.BaseActivity;
import com.android.callrecorder.databinding.ActivitySettingBinding;
import com.android.callrecorder.utils.SharedPreferenceUtil;

/**
 * 服务器重置
 */
public class SettingActivity extends BaseActivity {

    private ActivitySettingBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySettingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initView();
    }

    private void initView() {
        binding.llRestart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //重置服务器地址，重启app
                String host = binding.etHost.getText() == null ? "" : binding.etHost.getText().toString();
                SharedPreferenceUtil.getInstance().setHost(host);
                android.os.Process.killProcess(android.os.Process.myPid());
                System.exit(1);
            }
        });
    }

}