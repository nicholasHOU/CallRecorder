package com.android.callrecorder.base;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;

import com.android.callrecorder.utils.AppManager;
import com.android.callrecorder.utils.CrashHandler;
import com.android.callrecorder.utils.Logs;
import com.android.callrecorder.utils.SharedPreferenceUtil;
import com.android.callrecorder.utils.ToastUtil;

import java.util.List;

/**
 * @author hou
 */
public class AppApplication extends Application {

    private static final String TAG = "AppApplication";

    private int count;
    private boolean isBackground;

    @Override
    public void onCreate() {
        super.onCreate();

        if (isMainProcess()) {
            initCrash();
            initLifecycle();
            initWidget();
        }

    }


    private void initLifecycle() {

        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
                AppManager.getAppManager().addActivity(activity);
            }

            @Override
            public void onActivityStarted(Activity activity) {
                count++;
                Logs.e("BACKGROUND_TO_MAIN", "onActivityStarted:" + isBackground);
                if (isBackground) {//!ConfigConstans.isFirstOpen &&    &&1 == count
//                    if (activity instanceof MainActivity) {
                    isBackground = false;
//                    EventBus.getDefault().post(ConfigConstans.BACKGROUND_TO_MAIN);//
//                    }
                }
            }

            @Override
            public void onActivityResumed(Activity activity) {
            }

            @Override
            public void onActivityPaused(Activity activity) {
            }

            @Override
            public void onActivityStopped(Activity activity) {
                count--;
//                if (!ConfigConstans.isFirstOpen && !isBackground && 0 == count) {
//                    isBackground = true;
//                }
                if (!isBackground && 0 == count) {
                    isBackground = true;
                }
            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
            }

            @Override
            public void onActivityDestroyed(Activity activity) {
                AppManager.getAppManager().removeActivity(activity);
            }
        });
    }

    private void initCrash() {
        CrashHandler crashHandler = CrashHandler.getInstance();
        crashHandler.init(getApplicationContext());
    }


    /**
     * 初始化组件
     */
    private void initWidget() {
        SharedPreferenceUtil.init(this);
        ToastUtil.init(this);
        Logs.setLogLevel(1);
    }


    public boolean isMainProcess() {
        return this.getPackageName().equals(getCurrentProcessName());
    }

    private String getCurrentProcessName() {
        int pid = android.os.Process.myPid();
        String currentProcessName = "";
        ActivityManager activityManager = (ActivityManager) getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> runningAppProcesses = activityManager.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo processInfo : runningAppProcesses) {
            Logs.d(TAG, "processName: " + processInfo.processName + " processId:" + processInfo.pid);
            if (pid == processInfo.pid) {
                currentProcessName = processInfo.processName;
            }
        }
        return currentProcessName;
    }
}
