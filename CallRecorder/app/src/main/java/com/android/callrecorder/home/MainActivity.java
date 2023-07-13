package com.android.callrecorder.home;

import android.os.Bundle;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.android.callrecorder.R;
import com.android.callrecorder.base.BaseActivity;
import com.android.callrecorder.databinding.ActivityMainBinding;
import com.android.callrecorder.widget.CustomNavigator;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import zuo.biao.library.manager.TimeRefresher;

public class MainActivity extends BaseActivity {

    private ActivityMainBinding binding;
    private String TAG_CALLPHONE = "callPhoneCheck";//循环调用接口，获取是否有需要拨号的任务
    private long during = 5000;//5s 循环调用接口，获取是否有需要拨号的任务

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

        startTimer();
    }

    private void startTimer() {
        TimeRefresher.getInstance().addTimeRefreshListener(TAG_CALLPHONE, during, new TimeRefresher.OnTimeRefreshListener() {
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
        });
    }

    /**
     * 获取拨号任务接口
     */
    private void loadServerCallPhoneTask() {

    }

    @Override
    protected void onStart() {
        super.onStart();
        TimeRefresher.getInstance().startTimeRefreshListener(TAG_CALLPHONE);

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