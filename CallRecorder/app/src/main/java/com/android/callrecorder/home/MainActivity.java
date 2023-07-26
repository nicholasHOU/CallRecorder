package com.android.callrecorder.home;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.android.callrecorder.R;
import com.android.callrecorder.base.BaseActivity;
import com.android.callrecorder.bean.response.CallPhoneResponse;
import com.android.callrecorder.bean.response.ConfigResponse;
import com.android.callrecorder.config.Constant;
import com.android.callrecorder.config.GlobalConfig;
import com.android.callrecorder.databinding.ActivityMainBinding;
import com.android.callrecorder.home.ui.callhistory.CallHistoryFragment;
import com.android.callrecorder.home.ui.callrecord.CallRecordFragment;
import com.android.callrecorder.home.ui.my.MyFragment;
import com.android.callrecorder.http.MyHttpManager;
import com.android.callrecorder.login.LoginActivity;
import com.google.android.material.tabs.TabLayout;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import zuo.biao.library.manager.TimeRefresher;

public class MainActivity extends BaseActivity {

    private ActivityMainBinding binding;
    private String TAG_CALLPHONE = "callPhoneCheck";//循环调用接口，获取是否有需要拨号的任务
    private TimeRefresher.OnTimeRefreshListener onTimeRefreshListener;

    private FragmentManager mFragmentManager;
    private FragmentTransaction mTransaction;
    protected Fragment mCurrentFragment;
    private int mCurrentTabPos;
    private boolean isDestroyed;

    private int tabTitles[] = {R.string.title_callrecord, R.string.title_calllog, R.string.title_my};
    private int tabImages[] = {R.drawable.bg_home_first, R.drawable.bg_home_second, R.drawable.bg_home_third};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initTablayout();
        initListener();
        initFragment();
        loadConfigData();
        startService();
        startTimer();
        binding.tabLayout.selectTab(binding.tabLayout.getTabAt(0));
    }

    private void initTablayout() {
        binding.tabLayout.removeAllTabs();
        for (int i = 0; i < 3; i++) {
            TabLayout.Tab tab = binding.tabLayout.newTab();
            if (tab != null) {
                tab.setCustomView(getTabView(i));
                binding.tabLayout.addTab(tab);
            }
        }
    }

    private void selectTablayout() {
        for (int i = 0; i < 3; i++) {
            View view = binding.tabLayout.getTabAt(i).getCustomView();
            ImageView img = view.findViewById(R.id.iv_bg);
            TextView tvTitle = view.findViewById(R.id.tv_title);
            img.setSelected(mCurrentTabPos == i);
            tvTitle.setSelected(mCurrentTabPos == i);
        }
    }

    protected View getTabView(int position) {
        View imTabView = LayoutInflater.from(this).inflate(R.layout.activity_main_tablayout, null);
        ImageView img = imTabView.findViewById(R.id.iv_bg);
        TextView tvTitle = imTabView.findViewById(R.id.tv_title);
        img.setImageResource(tabImages[position]);
        tvTitle.setText(tabTitles[position]);
        if (position == mCurrentTabPos) {
            img.setSelected(true);
            tvTitle.setSelected(true);
        }
        return imTabView;
    }

    /**
     * 初始化事件监听
     */
    protected void initListener() {
        binding.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mCurrentTabPos = tab.getPosition();
                createCurrentFragment(mCurrentTabPos);
                selectTablayout();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                onTabSelected(tab);
            }
        });
    }


    private void initFragment() {
        mFragmentManager = getSupportFragmentManager();
        mTransaction = mFragmentManager.beginTransaction();
    }

    private void createCurrentFragment(int mCurrentTabPos) {
        if (mTransaction == null) {
            mTransaction = mFragmentManager.beginTransaction();
        }
        String name = makeFragmentTag(mCurrentTabPos);
        Fragment currentFragment = mFragmentManager.findFragmentByTag(name);
        if (currentFragment != null) {
            mTransaction.attach(currentFragment);
        } else {
            currentFragment = getItem(mCurrentTabPos);
            mTransaction.add(getContentFragmentId(), currentFragment, name);
        }
        if (currentFragment != mCurrentFragment) {
            if (mCurrentFragment != null) {
                mCurrentFragment.setMenuVisibility(false);
                mCurrentFragment.setUserVisibleHint(false);
                mTransaction.hide(mCurrentFragment);
            } else {//内存回收，但mFragmentManager会保存相关fragment
                List<Fragment> fragments = mFragmentManager.getFragments();
                if (fragments != null && fragments.size() > 0) {
                    for (Fragment fragment : fragments) {
                        if (fragment != null) {
                            fragment.setMenuVisibility(false);
                            fragment.setUserVisibleHint(false);
                            mTransaction.hide(fragment);
                        }
                    }
                }
            }
        }
        mCurrentFragment = currentFragment;

        mTransaction.show(mCurrentFragment);
        if (!isDestroyed) {
            mTransaction.commitAllowingStateLoss();
            mFragmentManager.executePendingTransactions();
        }
        if (currentFragment != null) {
            currentFragment.setMenuVisibility(true);
//            if (currentFragment.isAdded()) {
            currentFragment.setUserVisibleHint(true);
//            }
        }
        mTransaction = null;
    }

    private Fragment getItem(int position) {
        Fragment fragment = null;
        switch (position) {
            case 0:
                fragment = CallRecordFragment.createInstance();
                break;
            case 1:
                fragment = CallHistoryFragment.createInstance();
                break;
            case 2:
                fragment = MyFragment.createInstance();
                break;

        }
        return fragment;
    }

    private String makeFragmentTag(int mCurrentTabPos) {
        return "FragmentHome:" + mCurrentTabPos;
    }

    private int getContentFragmentId() {
        return R.id.fl_content;
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
        isDestroyed = true;
        TimeRefresher.getInstance().removeTimeRefreshListener(TAG_CALLPHONE);
    }
}