package com.android.callrecorder.home;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.android.callrecorder.R;
import com.android.callrecorder.base.AppApplication;
import com.android.callrecorder.bean.CallRecordEvent;
import com.android.callrecorder.utils.FileUtil;
import com.android.callrecorder.utils.Logs;

import org.greenrobot.eventbus.EventBus;

import java.io.File;

/**
 * 通话状态监听
 */
public class PhoneListenerService extends Service {
    private static final String NOTIFICATION_CHANNEL_ID = "PhoneListenerService";
    public static final int MANAGER_NOTIFICATION_ID = 0x1005;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Logs.e("PhoneListenerService", "PhoneListenerService == onStartCommand");
        addForegroundNotification();
        TelephonyManager manager =
                (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        //监听电话的状态
        manager.listen(new MyListener(), PhoneStateListener.LISTEN_CALL_STATE);
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private final class MyListener extends PhoneStateListener {
        private String phone;
        private MediaRecorder recorder;
        private File file;

        public void onCallStateChanged(int state, String incomingNumber) {
            phone = incomingNumber;
            switch (state) {
                case TelephonyManager.CALL_STATE_RINGING: /* 电话进来时 */
                    Logs.e("PhoneCall ","CALL_STATE_RINGING");
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
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    EventBus.getDefault().post(new CallRecordEvent(CallRecordEvent.START, System.currentTimeMillis()));
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

    private void addForegroundNotification() {
        createNotificationChannel();

        String contentTitle = getResources().getString(R.string.app_name);
        String contentText = "记时通";

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext(), NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher)
                .setLargeIcon(((BitmapDrawable) getResources().getDrawable(R.drawable.ic_launcher)).getBitmap())
                .setContentTitle(contentTitle)
                .setContentText(contentText)
                .setWhen(System.currentTimeMillis())
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        Intent msgIntent = getStartAppIntent(getApplicationContext());
        PendingIntent mainPendingIntent = PendingIntent.getActivity(getApplicationContext(), 0,
                msgIntent, PendingIntent.FLAG_IMMUTABLE);
        Notification notification = mBuilder.setContentIntent(mainPendingIntent)
                .setAutoCancel(false).build();

        startForeground(MANAGER_NOTIFICATION_ID, notification);
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String name = getResources().getString(R.string.app_name);
            String description = name;
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, name, importance);
            channel.setDescription(description);
            channel.setShowBadge(false);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }

    private Intent getStartAppIntent(Context context) {
        Intent intent = context.getPackageManager()
                .getLaunchIntentForPackage(AppApplication.app.getPackageName());
        if (intent != null) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                    | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
        }

        return intent;
    }

}
