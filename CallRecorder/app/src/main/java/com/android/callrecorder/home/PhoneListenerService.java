package com.android.callrecorder.home;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaRecorder;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

import androidx.annotation.Nullable;

import com.android.callrecorder.bean.CallRecordEvent;
import com.android.callrecorder.utils.FileUtil;
import com.android.callrecorder.utils.Logs;

import org.greenrobot.eventbus.EventBus;

import java.io.File;

/**
 * 通话状态监听
 */
public class PhoneListenerService extends Service {

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Logs.e("PhoneListenerService", "PhoneListenerService == onStartCommand");
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
            switch (state) {
                case TelephonyManager.CALL_STATE_RINGING: /* 电话进来时 */
                    phone = incomingNumber;
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK: /* 接起电话时 */
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
                    try {
                        if (recorder != null) {
                            recorder.stop();
                            recorder.release();
                        }
                        CallRecordEvent event = new CallRecordEvent(CallRecordEvent.START, System.currentTimeMillis());
                        if (file != null) {
                            event.recordFile = file.getAbsolutePath();
                            event.phone = phone;
                            EventBus.getDefault().post(event);
                        }
                        phone = "";
                        file = null;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }
    }
}
