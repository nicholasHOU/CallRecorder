//package com.android.callrecorder.home.receiver;
//
//import android.content.Context;
//import android.content.Intent;
//import android.media.MediaRecorder;
//import android.text.TextUtils;
//
//import com.android.callrecorder.utils.FileUtil;
//
//import org.greenrobot.eventbus.EventBus;
//
//import java.io.File;
//
//public class CallRecorderReceiver extends PhoneCallReceiver {
//    public static final String ACTION_IN = "android.intent.action.PHONE_STATE";
//    public static final String ACTION_OUT = "android.intent.action.NEW_OUTGOING_CALL";
//    public static final String EXTRA_PHONE_NUMBER = "android.intent.extra.PHONE_NUMBER";
//    private static final String TAG = "CallRecordReceiver";
//    private static MediaRecorder recorder;
//    private File audiofile;
//    private long calltime;
//    private boolean isRecordStarted = false;
//    private boolean needRecord = true;
//
//    private void releaseMediaRecorder() {
//        MediaRecorder localMediaRecorder = recorder;
//        if (localMediaRecorder != null) {
//            localMediaRecorder.reset();
//            recorder.release();
//            recorder = null;
//        }
//    }
//
//    private void startRecord(Context paramContext, String paramString1, String paramString2) {
//        CallRecordManager.getIntance().update();
//        paramString1 = paramString2;
//        if (TextUtils.isEmpty(paramString2)) {
//            paramString1 = "未知号码";
//        }
//        if (CallRecordManager.getIntance().isUseSystemCallRecord()) {
//            this.needRecord = false;
//            onRecordingStarted(paramContext, this.calltime, paramString1, null);
//            Timber.i(TAG + " SystemCallRecord record start", new Object[0]);
//            return;
//        }
//        try {
//            if (recorder != null) {
//                releaseMediaRecorder();
//            }
//            this.needRecord = true;
//            this.audiofile = FileUtil.getCallRecordSaveFile(this.calltime, paramString1);
//            paramString2 = new MediaRecorder();
//            recorder = paramString2;
//            paramString2.setAudioSource(7);
//            recorder.setOutputFormat(3);
//            recorder.setAudioEncoder(1);
//            recorder.setOutputFile(this.audiofile.getAbsolutePath());
//            recorder.prepare();
//            recorder.start();
//            this.isRecordStarted = true;
//            onRecordingStarted(paramContext, this.calltime, paramString1, this.audiofile);
//            Timber.i(TAG + "record start", new Object[0]);
//            return;
//        } catch (Exception paramString1) {
//            MobclickAgent.reportError(paramContext, paramString1);
//            paramString1.printStackTrace();
//        }
//    }
//
//    private void stopRecord(Context paramContext, long paramLong, String paramString, boolean paramBoolean) {
//        String str = paramString;
//        if (TextUtils.isEmpty(paramString)) {
//            str = "未知号码";
//        }
//        if (this.needRecord) {
//            paramString = recorder;
//            if ((paramString != null) && (this.isRecordStarted)) {
//                try {
//                    paramString.stop();
//                } catch (Exception paramString) {
//                    MobclickAgent.reportError(paramContext, paramString);
//                    paramString.printStackTrace();
//                }
//                releaseMediaRecorder();
//                this.isRecordStarted = false;
//                onRecordingFinished(paramContext, this.calltime, paramLong, str, this.audiofile, paramBoolean);
//                Timber.i(TAG + "record stop", new Object[0]);
//                return;
//            }
//        }
//        this.isRecordStarted = false;
//        onRecordingFinished(paramContext, this.calltime, paramLong, str, null, paramBoolean);
//        Timber.i(TAG + " system record stop", new Object[0]);
//    }
//
//    protected void endLastCall(Context paramContext, String paramString, long paramLong, boolean paramBoolean) {
//        if ((this.isRecordStarted) && (!TextUtils.isEmpty(paramString))) {
//            stopRecord(paramContext, System.currentTimeMillis(), paramString, paramBoolean);
//        }
//    }
//
//    protected void onIncomingCallAnswered(Context paramContext, String paramString, long paramLong) {
//        Timber.v("onIncomingCallAnswered**" + paramLong + "**" + paramString, new Object[0]);
//        this.calltime = paramLong;
//        startRecord(paramContext, "incoming", paramString);
//    }
//
//    protected void onIncomingCallEnded(Context paramContext, String paramString, long paramLong1, long paramLong2) {
//        Timber.v("onIncomingCallEnded**" + paramLong2 + "**" + paramString, new Object[0]);
//        stopRecord(paramContext, paramLong2, paramString, true);
//    }
//
//    protected void onIncomingCallReceived(Context paramContext, String paramString, long paramLong) {
//        this.calltime = paramLong;
//        Timber.v("onIncomingCallReceived**" + paramLong + "**" + paramString, new Object[0]);
//    }
//
//    protected void onMissedCall(Context paramContext, String paramString, long paramLong) {
//        Timber.v("onMissedCall " + paramLong, new Object[0]);
//        EventBus.getDefault().post(new NewMissedCallEvent());
//    }
//
//    protected void onOutgoingCallEnded(Context paramContext, String paramString, long paramLong1, long paramLong2) {
//        Timber.v("onOutgoingCallEnded " + paramLong2, new Object[0]);
//        stopRecord(paramContext, paramLong2, paramString, false);
//    }
//
//    protected void onOutgoingCallStarted(Context paramContext, String paramString, long paramLong) {
//        Timber.v("onOutgoingCallStarted " + paramLong, new Object[0]);
//        this.calltime = paramLong;
//        startRecord(paramContext, "outgoing", paramString);
//    }
//
//    protected void onRecordingFinished(Context paramContext, long paramLong1, long paramLong2, String paramString, File paramFile, boolean paramBoolean) {
//        Intent localIntent = new Intent(paramContext, CallLogService.class);
//        localIntent.putExtra("calltime", paramLong1);
//        localIntent.putExtra("endtime", paramLong2);
//        localIntent.putExtra("number", paramString);
//        localIntent.putExtra("isIncoming", paramBoolean);
//        if (paramFile != null) {
//            localIntent.putExtra("filepath", paramFile.getAbsolutePath());
//        }
//        paramContext.startService(localIntent);
//    }
//
//    protected void onRecordingStarted(Context paramContext, long paramLong, String paramString, File paramFile) {
//    }
//
//}
