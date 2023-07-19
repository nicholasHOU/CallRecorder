package com.android.callrecorder.home;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaRecorder;
import android.os.Environment;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

import androidx.annotation.Nullable;

import com.android.callrecorder.utils.FileUtil;
import com.android.callrecorder.utils.Logs;

import java.io.File;

/**
 * 通话状态监听
 *
 */
public class PhoneListenerService extends Service {

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Logs.e("PhoneListenerService","PhoneListenerService == onStartCommand");
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
        private String num;
        private MediaRecorder recorder;

        public void onCallStateChanged(int state, String incomingNumber) {
            switch (state) {
                case TelephonyManager.CALL_STATE_RINGING: /* 电话进来时 */
                    num = incomingNumber;
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK: /* 接起电话时 */
                    try {
//                        File file = new File(Environment.getExternalStorageDirectory(), num + "_" + System.currentTimeMillis() + ".3gp");
                        File file = FileUtil.getCallRecordSaveFile(System.currentTimeMillis(), num);
//                        File file = new File(Environment.getExternalStorageDirectory(), num + "_" + System.currentTimeMillis() + ".mp3");
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
                    break;
                case TelephonyManager.CALL_STATE_IDLE: /* 无任何状态时 */
                    if (recorder != null) {
                        recorder.stop();
                        recorder.release();
                    }
                    break;
            }
        }
    }
}
