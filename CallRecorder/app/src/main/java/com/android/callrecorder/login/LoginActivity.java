package com.android.callrecorder.login;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import com.android.callrecorder.R;
import com.android.callrecorder.base.BaseActivity;
import com.android.callrecorder.bean.response.LoginResponse;
import com.android.callrecorder.config.Constant;
import com.android.callrecorder.config.GlobalConfig;
import com.android.callrecorder.databinding.ActivityLoginBinding;
import com.android.callrecorder.home.MainActivity;
import com.android.callrecorder.http.MyHttpManager;
import com.android.callrecorder.setting.SettingActivity;
import com.android.callrecorder.utils.SharedPreferenceUtil;
import com.android.callrecorder.utils.ToastUtil;

import java.util.HashMap;
import java.util.Map;

import zuo.biao.library.util.MD5Util;

public class LoginActivity extends BaseActivity {

    private ActivityLoginBinding binding;

    private String username;
    private String password;
    private int loginRequestCode = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initView();
        initLoginInfo();
    }

    private void initView() {
        binding.llLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isChecked()) {
                    return;
                }
                login(username, password, loginRequestCode, (requestCode,isSuccess, resultJson) -> {
                    if (isSuccess){
                        GlobalConfig.token = resultJson.token;
                        ToastUtil.showToast("登录成功");
                    }else {
                        if (resultJson!=null&&Constant.HttpCode.HTTP_NEED_LOGIN == resultJson.code) {
                        } else {
                        }
                        ToastUtil.showToast("登录失败，请稍后重试");
                    }
                    SharedPreferenceUtil.getInstance().setLoginInfo(username, password);
                    goHome();
                });
            }
        });

        binding.container.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                goSetting();
                return false;
            }
        });
    }

    private void goSetting(){
        Intent intent = new Intent(this, SettingActivity.class);
        startActivity(intent);
    }

    /**
     * 参数校验
     *
     * @return
     */
    private boolean isChecked() {
        if (TextUtils.isEmpty(username)) {
            username = binding.etUsername.getText().toString();
        }
        if (TextUtils.isEmpty(password)) {
            password = binding.etPassword.getText().toString();
        }
        if (TextUtils.isEmpty(username)) {
            ToastUtil.showToast(R.string.input_username_tip);
            return false;
        }
        if (TextUtils.isEmpty(password)) {
            ToastUtil.showToast(R.string.input_password_tip);
            return false;
        }
        return true;
    }

    private void initLoginInfo() {
        boolean hasLoginInfo = SharedPreferenceUtil.getInstance().hasLoginInfo();
        if (hasLoginInfo) {
            username = SharedPreferenceUtil.getInstance().getUsername();
            password = SharedPreferenceUtil.getInstance().getPassword();
            binding.etUsername.setText(username);
            binding.etPassword.setText(password);
        }
    }

    private void login(String username, String password,
                      final int requestCode, final MyHttpManager.ResponseListener<LoginResponse> listener) {
        Map<String, Object> request = new HashMap<>(8);
        request.put("username", username);
//        request.put("username", MD5Util.MD5(password));
        request.put("password", password);

        MyHttpManager.getInstance().post(request, Constant.URL_LOGIN, requestCode, listener);
    }


    private void goHome(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}