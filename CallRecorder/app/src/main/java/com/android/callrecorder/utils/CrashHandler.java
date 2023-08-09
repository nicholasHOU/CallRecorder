package com.android.callrecorder.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.os.Environment;
import android.os.Looper;
import android.os.SystemClock;
import android.text.TextUtils;
import android.widget.Toast;

import com.android.callrecorder.bean.CrashLog;
import com.android.callrecorder.config.Constant;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class CrashHandler implements UncaughtExceptionHandler {
    private static final String TAG = "CrashHandler";
    private UncaughtExceptionHandler mDefaultHandler = null;
    private static CrashHandler INSTANCE = new CrashHandler();
    private Context mContext;
    private Map<String, String> info = new HashMap<String, String>();
    @SuppressLint("SimpleDateFormat")
    private SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
    private String exceptionMessage = null;
    private String dirct = "call-crash";

//    private boolean b = false;
//    private String path;

    // 保证只有一个CrashHandler实例
    private CrashHandler() {
    }

    public static CrashHandler getInstance() {
        return INSTANCE;
    }

    public void init(Context context) {
        mContext = context;

        // 获取系统默认的UncaughtException处理器
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();

        // 设置该CrashHandler为程序的默认处理器
        Thread.setDefaultUncaughtExceptionHandler(this);

//        if (!this.b) {
//            this.path = mContext.getFilesDir() + File.separator + "crash";
//        } else {
//            this.path = mContext.getExternalFilesDir((String) null) + File.separator + "crash";
//        }
    }

    public void uncaughtException(Thread thread, Throwable ex) {
        ex.printStackTrace();
        if (null != mDefaultHandler && !handleException(ex)) {
            mDefaultHandler.uncaughtException(thread, ex);
        }
    }

    private Toast toast = null;

    public boolean handleException(Throwable ex) {
        Logs.d(TAG, "CrashHandler=======>handleException()");

        if (ex == null) {
            return false;
        }

//		ex.printStackTrace();

        new Thread() {

            @Override
            public void run() {
                Looper.prepare();
                toast = Toast.makeText(mContext, "程序异常，即将退出程序", Toast.LENGTH_LONG);
                toast.show();
                Looper.loop();
            }
        }.start();

        collectDeviceInfo(mContext);
        exceptionMessage = saveCrashInfo2File(ex);

//        if (null != exceptionMessage) {
//            SystemClock.sleep(1000);
//        }
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(0);
        return true;
    }

    public void collectDeviceInfo(Context context) {
        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(), PackageManager.GET_ACTIVITIES);
            if (pi != null) {
                String versionName = pi.versionName == null ? "null" : pi.versionName;
                String versionCode = pi.versionCode + "";
                info.put("versionName", versionName);
                info.put("versionCode", versionCode);
            }
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        Field[] fields = Build.class.getDeclaredFields();
        for (Field field : fields) {
            try {
                field.setAccessible(true);
                info.put(field.getName(), field.get("").toString());
//				Logs.d(TAG, field.getName() + ":" + field.get(""));
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    private String saveCrashInfo2File(Throwable ex) {
        StringBuffer sb = new StringBuffer();
        for (Map.Entry<String, String> entry : info.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            if ("unknown".equals(value)) continue;
            sb.append(key + "=" + value + "\r\n");
        }
        Writer writer = new StringWriter();
        PrintWriter pw = new PrintWriter(writer);
        ex.printStackTrace(pw);
        Throwable cause = ex.getCause();
        while (cause != null) {
            cause.printStackTrace(pw);
            cause = cause.getCause();
        }
        pw.close();
        String result = writer.toString();
//        Logs.e("TAG", "CrashHandler:" + result);
        sb.append(result);

        long timetamp = System.currentTimeMillis();
        String time = format.format(new Date());
        String fileName = "crash-" + time + "-" + timetamp + ".txt";
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            try {
//				File dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + dirct);
                File dir = new File(Constant.CRASH_FILE_PATH);
                Logs.i("CrashHandler", dir.toString());
                if (!dir.exists())
                    dir.mkdir();
                FileOutputStream fos = new FileOutputStream(new File(dir, fileName));
                fos.write(sb.toString().getBytes());
                fos.close();
                return sb.toString();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }


    public void delCrashAllLogFile() {
        File var1 = new File(Constant.CRASH_FILE_PATH);
        File[] var2 = var1.listFiles();
        int var3 = var2.length;

        for (int var4 = 0; var4 < var3; ++var4) {
            File var5 = var2[var4];
            this.del(var5.getAbsolutePath());
        }
    }

    public void del(String var1) {
        File var2 = new File(var1);
        if (var2.exists()) {
            var2.delete();
        }

    }

    private void del(File file) {
        if (file.exists()) {
            file.delete();
        }
    }

    public CrashLog readCrashLog() {
        File file = new File(Constant.CRASH_FILE_PATH);
        CrashLog crashLogBean;
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            for (File crashLog : files) {
                crashLogBean = readCrashLog(crashLog.getAbsolutePath());
                if (!TextUtils.isEmpty(crashLogBean.crashLogContent)) {
                    return crashLogBean;
                }
            }
        }
        return null;
    }

    public CrashLog readCrashLog(String crashLog) {
        CrashLog crashLogBean = new CrashLog();
        try {
            BufferedReader reader = null;
            reader = new BufferedReader(new FileReader(crashLog));
            StringBuilder stringBuilder = new StringBuilder();
            String line = null;
            String ls = System.getProperty("line.separator");
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
                stringBuilder.append(ls);
            }
            // 删除最后一个新行分隔符
            stringBuilder.deleteCharAt(stringBuilder.length() - 1);
            reader.close();

            String crashLogStr = stringBuilder.toString();
            crashLogBean.crashLogFile = crashLog;
            crashLogBean.crashLogContent = crashLogStr;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return crashLogBean;
    }
}
