package com.android.callrecorder.home;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.android.callrecorder.R;
import com.android.callrecorder.base.BaseActivity;
import com.android.callrecorder.databinding.ActivityDayRecordBinding;
import com.android.callrecorder.home.ui.callrecord.DayRecordFragment;

/**
 * 当天通话录音文件
 */
public class DayRecordActivity extends BaseActivity {

    private ActivityDayRecordBinding binding;
    private Fragment fragment;
    private long time;
    private String timeStr;

    public static void jump(Activity context, long time, String timeStr) {
        Intent intent  = new Intent(context,DayRecordActivity.class);
        intent.putExtra("time",time);
        intent.putExtra("timeStr",timeStr);
        context.startActivity(intent);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityDayRecordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initParams();
        initFragment();

    }

    private void initParams() {
        time = getIntent().getLongExtra("time", 0);
        timeStr = getIntent().getStringExtra("timeStr");
    }

    private void initFragment() {
        FragmentManager mFragmentManager = getSupportFragmentManager();
        FragmentTransaction mTransaction = mFragmentManager.beginTransaction();
        if (mTransaction == null) {
            mTransaction = mFragmentManager.beginTransaction();
        }
        fragment = DayRecordFragment.createInstance(time, timeStr);
        mTransaction.add(R.id.rl_content, fragment, "CallRecordFragment");
        mTransaction.show(fragment);
        if (!this.isDestroyed()) {
            mTransaction.commitAllowingStateLoss();
            mFragmentManager.executePendingTransactions();
        }
        if (fragment != null) {
            fragment.setMenuVisibility(true);
            fragment.setUserVisibleHint(true);
        }
    }

    private void initData() {
//        fragment
    }


}