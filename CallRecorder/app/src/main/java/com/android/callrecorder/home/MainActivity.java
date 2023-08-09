package com.android.callrecorder.home;

import android.content.Intent;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
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
import com.android.callrecorder.bean.CallItem;
import com.android.callrecorder.bean.CallRecordEvent;
import com.android.callrecorder.bean.response.CallPhoneResponse;
import com.android.callrecorder.config.Constant;
import com.android.callrecorder.config.GlobalConfig;
import com.android.callrecorder.databinding.ActivityMainBinding;
import com.android.callrecorder.home.ui.callhistory.CallHistoryFragment;
import com.android.callrecorder.home.ui.callrecord.CallHistoryUtil;
import com.android.callrecorder.home.ui.callrecord.CallRecordFragment;
import com.android.callrecorder.home.ui.my.MyFragment;
import com.android.callrecorder.http.MyHttpManager;
import com.android.callrecorder.listener.Callback;
import com.android.callrecorder.utils.DataUtil;
import com.android.callrecorder.utils.FileUtil;
import com.android.callrecorder.utils.Logs;
import com.android.callrecorder.utils.SharedPreferenceUtil;
import com.android.callrecorder.widget.floatwindow.FloatUtils;
import com.google.android.material.tabs.TabLayout;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.ArrayList;
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

    private int tabTitlesV2[] = {R.string.title_callrecord, R.string.title_calllog, R.string.title_my};
    private int tabImagesV2[] = {R.drawable.bg_home_first, R.drawable.bg_home_second, R.drawable.bg_home_third};
    private int tabTitles[] = {R.string.title_calllog, R.string.title_my};
    private int tabImages[] = {R.drawable.bg_home_second, R.drawable.bg_home_third};
    private boolean isRecording;//是否正在录音
    private long recordStartTime;//开始录音的时间点
    private boolean isV1 = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        if (!isV1) {
            tabTitles = tabTitlesV2;
            tabImages = tabImagesV2;
        }

        EventBus.getDefault().register(this);
        initTablayout();
        initListener();
        initFragment();
        uploadCallLogData();
        startService();
        startTimer();
        binding.tabLayout.selectTab(binding.tabLayout.getTabAt(0));
        FloatUtils.openFloatWindow(this);
        registerService();
    }

    private void registerService() {

    }

        private final class MyListener extends PhoneStateListener {
            private String phone;
            private MediaRecorder recorder;
            private File file;

            public void onCallStateChanged(int state, String incomingNumber) {
                switch (state) {
                    case TelephonyManager.CALL_STATE_RINGING: /* 电话进来时 */
                        Logs.e("PhoneCall ","CALL_STATE_RINGING");
                        phone = incomingNumber;
                        break;
                    case TelephonyManager.CALL_STATE_OFFHOOK: /* 接起电话时 */
                        Logs.e("PhoneCall ","CALL_STATE_OFFHOOK");
                        try {
//                        File file = new File(Environment.getExternalStorageDirectory(), num + "_" + System.currentTimeMillis() + ".3gp");
                            file = FileUtil.getCallRecordSaveFile(System.currentTimeMillis(), phone);
                            recorder = new MediaRecorder();
                            recorder.setAudioSource(MediaRecorder.AudioSource.MIC);//声音采集来源(话筒)
//                        recorder.setOutputFormat(MediaRecorder.OutputFormat.RAW_AMR);//输出的格式
                            recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);//输出的格式
                            recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);//音频编码方式
                            recorder.setOutputFile(file.getAbsolutePath());//输出方向
                            recorder.prepare();
                            recorder.start();
                            EventBus.getDefault().post(new CallRecordEvent(CallRecordEvent.START, System.currentTimeMillis()));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    case TelephonyManager.CALL_STATE_IDLE: /* 无任何状态时 */
                        Logs.e("PhoneCall ","CALL_STATE_IDLE");
                        try {
                            CallRecordEvent event = new CallRecordEvent(CallRecordEvent.END, System.currentTimeMillis());
                            if (file != null) {
                                event.recordFile = file.getAbsolutePath();
                                event.phone = phone;
                                EventBus.getDefault().post(event);
                                phone = "";
                                file = null;
                            }
                            if (recorder != null) {
                                recorder.stop();
                                recorder.release();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                }
            }

    }

    private void initTablayout() {
        binding.tabLayout.removeAllTabs();
        for (int i = 0; i < tabImages.length; i++) {
            TabLayout.Tab tab = binding.tabLayout.newTab();
            if (tab != null) {
                tab.setCustomView(getTabView(i));
                binding.tabLayout.addTab(tab);
            }
        }
    }

    private void selectTablayout() {
        for (int i = 0; i < tabImages.length; i++) {
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
                fragment = isV1 ? CallHistoryFragment.createInstance() : CallRecordFragment.createInstance();
                break;
            case 1:
                fragment = isV1 ? MyFragment.createInstance() : CallHistoryFragment.createInstance();
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

    /**
     * 启动服务
     */
    private void startService() {
//        Intent intent = new Intent(MainActivity.this, PhoneListenerService.class);
//        startService(intent);
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


    /**
     * 获取拨号任务接口
     */
    private void loadServerCallPhoneTask() {
        Map params = new HashMap();
        MyHttpManager.getInstance().post(params, Constant.URL_CALLPHONE, 125,
                new MyHttpManager.ResponseListener<CallPhoneResponse>() {
                    @Override
                    public void onHttpResponse(int requestCode, boolean isSuccess, CallPhoneResponse resultJson) {
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
                    }

                    @Override
                    public Class getTClass() {
                        return CallPhoneResponse.class;
                    }
                });

    }

    @Override
    protected void onResume() {
        super.onResume();
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
        isDestroyed = true;
        TimeRefresher.getInstance().removeTimeRefreshListener(TAG_CALLPHONE);
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(CallRecordEvent event) {
        Logs.e("CallRecordEvent", event.toString());
        if (event.type == CallRecordEvent.START) {
            isRecording = true;
            recordStartTime = event.timestamp;
        } else if (event.type == CallRecordEvent.END) {
            if (isRecording) {//结束当前录音
                long timeDuring = event.timestamp - recordStartTime;//录音时长
                List<CallItem> callLogs = new ArrayList<>();
                CallItem item = new CallItem();
                item.time = recordStartTime;
                item.during = timeDuring;
                item.phone = event.phone;
                item.name = "";
//                item.recordPath = event.recordFile;
                item.callType = CallItem.CALLTYPE_OUT;
                callLogs.add(item);
                CallHistoryUtil.getInstance().uploadCallLogData(callLogs,null);

                Map params = new HashMap();
                params.put("time", recordStartTime);
                params.put("during", timeDuring);
                params.put("phone", event.phone);
                params.put("name", "");
                params.put("callType", "");
                byte[] file = FileUtil.getRecordFile(event.recordFile);
                if(file.length == 0){
                    uploadSystemCallRecordFile();
                }else{
                    params.put("video", file);
                    DataUtil.uploadFile(params);
                }
                isRecording = false;
            }
        }
    }

    private void uploadSystemCallRecordFile() {
        List<CallItem> callLogs = FileUtil.loadLocalRecordFile();
        if (callLogs.size() > 0) {
            for (CallItem callItem : callLogs) {
                DataUtil.uploadRecord(callItem);
            }
            SharedPreferenceUtil.getInstance().setCallLogUploadTime(System.currentTimeMillis());
        } else {
        }
    }

    private void uploadCallLogData() {
        long currentTime = SharedPreferenceUtil.getInstance().getCallLogUploadTime();
//        long currentTime =0;
        List<CallItem> callLogs = CallHistoryUtil.getInstance().getDataList(this, currentTime);
        if (callLogs == null || callLogs.size() == 0) {
            return;
        }
        CallHistoryUtil.getInstance().uploadCallLogData(callLogs, new Callback() {
            @Override
            public void call(boolean isSuccess) {
                if (isSuccess) {
                    SharedPreferenceUtil.getInstance().setRecordUploadTime(System.currentTimeMillis());
                }
            }
        });
    }

}