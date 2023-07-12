package com.android.callrecorder.login;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.android.callrecorder.R;
import com.android.callrecorder.base.BaseActivity;
import com.android.callrecorder.databinding.ActivityLoginBinding;
import com.android.callrecorder.databinding.ActivityMainBinding;
import com.android.callrecorder.feedback.FeedbackActivity;
import com.android.callrecorder.home.MainActivity;
import com.android.callrecorder.setting.SettingActivity;
import com.android.callrecorder.utils.Config;
import com.android.callrecorder.utils.Constant;
import com.android.callrecorder.utils.SharedPreferenceUtil;
import com.android.callrecorder.utils.ToastUtil;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.HashMap;
import java.util.Map;

import zuo.biao.library.interfaces.OnHttpResponseListener;
import zuo.biao.library.manager.HttpManager;
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
                login(username, password, loginRequestCode, new OnHttpResponseListener() {
                    @Override
                    public void onHttpResponse(int requestCode, String resultJson, Exception e) {
                        if (requestCode == loginRequestCode) {
                            ToastUtil.showToast("login --- ");
                            SharedPreferenceUtil.getInstance().setLoginInfo(username, password);
                            goHome();
                            if (!TextUtils.isEmpty(resultJson) && resultJson.contains("200")) {

                            }
                        }
                    }
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
                      final int requestCode, final OnHttpResponseListener listener) {
        Map<String, Object> request = new HashMap<>(8);
        request.put("username", username);
        request.put("username", MD5Util.MD5(password));

        HttpManager.getInstance().post(request, Config.URL_BASE + Constant.URL_LOGIN, requestCode, listener);
    }


    private void goHome(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}