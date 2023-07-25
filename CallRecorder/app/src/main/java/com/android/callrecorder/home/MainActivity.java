package com.android.callrecorder.home;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.android.callrecorder.R;
import com.android.callrecorder.base.BaseActivity;
import com.android.callrecorder.bean.response.CallPhoneResponse;
import com.android.callrecorder.bean.response.ConfigResponse;
import com.android.callrecorder.config.Constant;
import com.android.callrecorder.config.GlobalConfig;
import com.android.callrecorder.databinding.ActivityMainBinding;
import com.android.callrecorder.http.MyHttpManager;
import com.android.callrecorder.login.LoginActivity;
import com.android.callrecorder.widget.CustomNavigator;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.HashMap;
import java.util.Map;

import zuo.biao.library.manager.TimeRefresher;

public class MainActivity extends BaseActivity {

    private ActivityMainBinding binding;
    private String TAG_CALLPHONE = "callPhoneCheck";//循环调用接口，获取是否有需要拨号的任务
    private TimeRefresher.OnTimeRefreshListener onTimeRefreshListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        navController.getNavigatorProvider().addNavigator(new CustomNavigator(this, getSupportFragmentManager(), R.id.nav_host_fragment_activity_main));
        NavigationUI.setupWithNavController(binding.navView, navController);
        loadConfigData();
        startService();
        startTimer();
    }

    private void loadConfigData() {
        Map params = new HashMap();
        MyHttpManager.getInstance().post(params, Constant.URL_CONFIG, 125,
                (MyHttpManager.ResponseListener<ConfigResponse>) (requestCode, isSuccess, resultJson) -> {
                    if (isSuccess) {
                        if (!TextUtils.isEmpty(resultJson.url)) {
                            GlobalConfig.url = resultJson.url;
                        }
                        if (resultJson.runTime > 2000) {
                            GlobalConfig.runTime = resultJson.runTime;
                        }
                    } else {
                        if (resultJson != null && Constant.HttpCode.HTTP_NEED_LOGIN == resultJson.code) {
                            goLogin();
                        } else {
                        }
                    }
                });
    }

    /**
     * 启动服务
     */
    private void startService() {
        Intent intent = new Intent(MainActivity.this, PhoneListenerService.class);
        startService(intent);
    }

    /**
     * 启动计时器
     */
    private void startTimer() {
        TimeRefresher.getInstance().addTimeRefreshListener(TAG_CALLPHONE, GlobalConfig.runTime, onTimeRefreshListener);
        onTimeRefreshListener = new TimeRefresher.OnTimeRefreshListener() {
            @Override
            public void onTimerStart() {

            }

            @Override
            public void onTimerRefresh() {
                loadServerCallPhoneTask();
            }


            @Override
            public void onTimerStop() {

            }
        };
    }

    @Override
    protected void onResume() {
        super.onResume();
        TimeRefresher.getInstance().startTimeRefreshListener(TAG_CALLPHONE);
    }


    /**
     * 获取拨号任务接口
     */
    private void loadServerCallPhoneTask() {
        Map params = new HashMap();
        MyHttpManager.getInstance().post(params, Constant.URL_CALLPHONE, 125,
                (MyHttpManager.ResponseListener<CallPhoneResponse>) (requestCode, isSuccess, resultJson) -> {
                    if (isSuccess) {
                        if (!TextUtils.isEmpty(resultJson.phone)) {
                            Intent intent = new Intent(Intent.ACTION_DIAL);
                            Uri data = Uri.parse("tel:" + resultJson.phone);
                            intent.setData(data);
                            startActivity(intent);
                            TimeRefresher.getInstance().stopTimeRefreshListener(TAG_CALLPHONE);
                        }
                    } else {
                        if (resultJson != null && Constant.HttpCode.HTTP_NEED_LOGIN == resultJson.code) {
                            goLogin();
                        } else {
                        }
                    }
                });
    }


    private void goLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onStop() {
        super.onStop();
        TimeRefresher.getInstance().stopTimeRefreshListener(TAG_CALLPHONE);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        TimeRefresher.getInstance().removeTimeRefreshListener(TAG_CALLPHONE);
    }
}