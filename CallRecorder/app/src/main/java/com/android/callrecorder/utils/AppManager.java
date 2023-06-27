package com.android.callrecorder.utils;

import android.app.Activity;

import java.util.Iterator;
import java.util.List;
import java.util.Stack;

/**
 * @author hou
 */
public class AppManager {

    private static final String TAG = "SHYT_AppManager";

    private static Stack<Activity> activityStack;

    private static volatile AppManager instance;

    private AppManager() {
        activityStack = new Stack<>();
    }

    /**
     * 单一实例
     */
    public static AppManager getAppManager() {
        if (instance == null) {
            synchronized (AppManager.class) {
                if (instance == null) {
                    instance = new AppManager();
                }
            }
        }
        return instance;
    }

    /**
     * 添加Activity到堆栈
     */
    public synchronized void addActivity(Activity activity) {
        if (activityStack != null) {
            activityStack.add(activity);
        }
    }

    public synchronized void removeActivity(Activity activity) {
        if (activityStack != null) {
            activityStack.remove(activity);
        }
    }

    /**
     * 获取当前Activity（堆栈中最后一个压入的）
     */
    public synchronized Activity currentActivity() {
        if (activityStack == null || activityStack.size() == 0) return null;
        return activityStack.lastElement();
    }


    public synchronized Activity firstElement() {
        if (activityStack == null || activityStack.size() == 0) return null;
        if (activityStack.size() > 2) {
            return activityStack.get(activityStack.size() - 2);
        } else {
            return activityStack.firstElement();
        }
    }

    /**
     * 结束当前Activity（堆栈中最后一个压入的）
     */
    public synchronized void finishActivity() {
        if (activityStack == null) return;
        Activity activity = activityStack.lastElement();
        finishActivity(activity);
    }

    public synchronized Stack<Activity> getActivityList() {
        return activityStack;
    }

    public synchronized void finishAc(Class<?> clz) {
        if (activityStack == null) return;
        Iterator<Activity> iterator = activityStack.iterator();
        while (iterator.hasNext()) {
            Activity next = iterator.next();
            if (next.getClass().equals(clz)) {
                iterator.remove();
                next.finish();
            }
        }
    }

    /**
     * 结束指定的Activity
     */
    public synchronized void finishActivity(Activity activity) {
        if (activityStack == null || activity == null) return;
        activityStack.remove(activity);
        activity.finish();
    }

    /**
     * 结束所有Activity
     */
    public synchronized void finishAllActivity() {
        if (activityStack == null) return;
        for (int i = 0, size = activityStack.size(); i < size; i++) {
            if (null != activityStack.get(i)) {
                activityStack.get(i).finish();
            }
        }
        activityStack.clear();
    }

    /**
     * 退出应用程序
     */
    public synchronized void AppExit() {
        try {
            finishAllActivity();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            System.exit(0);
        }
    }

    /**
     * 关闭指定类外的所有Activity
     *
     * @param cls
     */
    public synchronized void finishAllActExceptCls(List<Class> cls) {
        if (activityStack == null) return;
        Iterator<Activity> iterator = activityStack.iterator();
        while (iterator.hasNext()) {
            Activity activity = iterator.next();
            if (activity != null) {
                boolean finish = true;
                for (Class c : cls) {
                    if (activity.getClass().equals(c)) {
                        finish = false;
                        break;
                    }
                }
                if (finish) {
                    iterator.remove();
                    activity.finish();
                }
            }
        }
    }
}
