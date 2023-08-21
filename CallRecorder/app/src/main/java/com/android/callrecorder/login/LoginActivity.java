package com.android.callrecorder.login;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import com.android.callrecorder.R;
import com.android.callrecorder.base.BaseActivity;
import com.android.callrecorder.bean.response.LoginResponse;
import com.android.callrecorder.bean.response.UpdateResponse;
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

import zuo.biao.library.ui.AlertDialog;

public class LoginActivity extends BaseActivity {

    private ActivityLoginBinding binding;

    private String username;
    private String password;
    private int loginRequestCode = 123;
    private AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initView();
        initLoginInfo();
        checkVersion();
    }

    private void initView() {
        binding.llLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isChecked()) {
                    return;
                }
                login(new MyHttpManager.ResponseListener<LoginResponse>() {
                    @Override
                    public void onHttpResponse(int requestCode, boolean isSuccess, LoginResponse resultJson) {
                        if (isSuccess) {
                            GlobalConfig.token = resultJson.data.token;
                            ToastUtil.showToast("登录成功");
                            SharedPreferenceUtil.getInstance().setLoginInfo(username, password);
                            goHome();
                        } else {
                            if (resultJson != null && !TextUtils.isEmpty(resultJson.message)) {
                                ToastUtil.showToast(resultJson.message);
                            } else {
                                ToastUtil.showToast("登录失败，请稍后重试");
                            }
                        }
//                        SharedPreferenceUtil.getInstance().setLoginInfo(username, password);
//                        goHome();
                    }

                    @Override
                    public Class getTClass() {
                        return LoginResponse.class;
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

    private void goSetting() {
        Intent intent = new Intent(this, SettingActivity.class);
        startActivity(intent);
    }

    /**
     * 参数校验
     *
     * @return
     */
    private boolean isChecked() {
        username = binding.etUsername.getText().toString();
        password = binding.etPassword.getText().toString();
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

    private void login(final MyHttpManager.ResponseListener<LoginResponse> listener) {
        Map<String, Object> request = new HashMap<>(8);
        request.put("phone", username);
//        request.put("username", MD5Util.MD5(password));
        request.put("password", password);

        MyHttpManager.getInstance().post(request, Constant.URL_LOGIN, loginRequestCode, listener);
    }

    /**
     * 检测升级
     */
    private void checkVersion() {
        Map<String, Object> request = new HashMap<>(8);
//        request.put("version", username);

        MyHttpManager.getInstance().post(request, Constant.URL_UPDATE, loginRequestCode, new MyHttpManager.ResponseListener<UpdateResponse>() {
            @Override
            public void onHttpResponse(int requestCode, boolean isSuccess, UpdateResponse resultJson) {
                if (isSuccess) {
                    int updateType = resultJson.data.updateType;
                    if (updateType == UpdateResponse.UPDATETYPE_NONE) {
                        return;
                    } else if (updateType == UpdateResponse.UPDATETYPE_FORCE) {
                        showUpdateDialog(resultJson.data.tip, true, resultJson.data.downloadUrl);
                    } else if (updateType == UpdateResponse.UPDATETYPE_NORMAL) {
                        showUpdateDialog(resultJson.data.tip, false, resultJson.data.downloadUrl);
                    }
                } else {
                    if (resultJson != null && !TextUtils.isEmpty(resultJson.message)) {
//                        ToastUtil.showToast(resultJson.message);
                    } else {
//                        ToastUtil.showToast("登录失败，请稍后重试");
                    }
                }
            }

            @Override
            public Class getTClass() {
                return UpdateResponse.class;
            }
        });
    }

    private void showUpdateDialog(String title, boolean isForceUpdate, String updateUrl) {
        String tips = (TextUtils.isEmpty(title)) ? "优化了已知的bug，体验更佳~\n是否立刻升级" : title;
        dialog = new AlertDialog(this, "温馨提示", tips,
                !isForceUpdate, 0, new AlertDialog.OnDialogButtonClickListener() {
            @Override
            public void onDialogButtonClick(int requestCode, boolean isPositive) {
                if (isPositive) {
                    openUrl(updateUrl);
                } else {
                    if (isForceUpdate) {
                        finish();
                    }else {
                        dialog.dismiss();
                    }
                }
            }
        });
        dialog.setCancelable(!isForceUpdate);
        dialog.setCanceledOnTouchOutside(!isForceUpdate);
        dialog.show();
    }

    private void openUrl(String url) {
        Intent intent = new Intent();
        intent.setAction("android.intent.action.VIEW");
        Uri contentUrl = Uri.parse(url);
        intent.setData(contentUrl);
        startActivity(intent);
    }

    private void goHome() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}