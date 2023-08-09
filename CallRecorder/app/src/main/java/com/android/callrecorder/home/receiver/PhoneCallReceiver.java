package com.android.callrecorder.home.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import com.android.callrecorder.utils.Logs;

public abstract class PhoneCallReceiver extends BroadcastReceiver {
    public static final String ACTION_IN = "android.intent.action.PHONE_STATE";
    public static final String ACTION_OUT = "android.intent.action.NEW_OUTGOING_CALL";
    public static final String EXTRA_PHONE_NUMBER = "android.intent.extra.PHONE_NUMBER";

    private static final String TAG = "PhoneCallReceiver";
    private static long callStartTime = 0L;
    protected static boolean isCallEnd = true;
    private static boolean isIncoming;
    private static String lastSavedNumber;
    private static int lastState;
    private static String savedNumber;
    private PhoneStateListener phoneStateListener = new PhoneStateListener() {
        public void onCallStateChanged(int paramAnonymousInt, String paramAnonymousString) {
            super.onCallStateChanged(paramAnonymousInt, paramAnonymousString);
//            Logs.v("onCallStateChanged state == " + paramAnonymousInt + "  incomingNumber = " + paramAnonymousString, new Object[0]);
        }
    };

    protected abstract void endLastCall(Context paramContext, String paramString, long paramLong, boolean paramBoolean);

    public void onCallStateChanged(Context paramContext, int paramInt, String paramString) {
        if (lastState == paramInt) {
            return;
        }
        if (paramInt != 0) {
            long l;
            if (paramInt != 1) {
                if (paramInt == 2) {
//                    Timber.i(TAG + " CALL_STATE_OFFHOOK", new Object[0]);
                    if (lastState != 1) {
                        if (!isCallEnd) {
                            endLastCall(paramContext, savedNumber, callStartTime, isIncoming);
                        }
                        isIncoming = false;
                        l = System.currentTimeMillis();
                        callStartTime = l;
                        onOutgoingCallStarted(paramContext, savedNumber, l);
                        isCallEnd = false;
                        lastSavedNumber = savedNumber;
                    } else {
                        isIncoming = true;
                        l = System.currentTimeMillis();
                        callStartTime = l;
                        onIncomingCallAnswered(paramContext, savedNumber, l);
                        isCallEnd = false;
                        lastSavedNumber = savedNumber;
                    }
                }
            } else {
//                Timber.i(TAG + "CALL_STATE_RINGING SystemCallRecord record start", new Object[0]);
                if (!isCallEnd) {
                    endLastCall(paramContext, lastSavedNumber, callStartTime, isIncoming);
                }
                isIncoming = true;
                l = System.currentTimeMillis();
                callStartTime = l;
                savedNumber = paramString;
                onIncomingCallReceived(paramContext, paramString, l);
            }
        } else {
//            Timber.i(TAG + " CALL_STATE_IDLE", new Object[0]);
            if (lastState == 1) {
                onMissedCall(paramContext, savedNumber, callStartTime);
            } else if (isIncoming) {
                onIncomingCallEnded(paramContext, savedNumber, callStartTime, System.currentTimeMillis());
            } else {
                onOutgoingCallEnded(paramContext, savedNumber, callStartTime, System.currentTimeMillis());
            }
            isCallEnd = true;
        }
        lastState = paramInt;
    }

    protected abstract void onIncomingCallAnswered(Context paramContext, String paramString, long paramLong);

    protected abstract void onIncomingCallEnded(Context paramContext, String paramString, long paramLong1, long paramLong2);

    protected abstract void onIncomingCallReceived(Context paramContext, String paramString, long paramLong);

    protected abstract void onMissedCall(Context paramContext, String paramString, long paramLong);

    protected abstract void onOutgoingCallEnded(Context paramContext, String paramString, long paramLong1, long paramLong2);

    protected abstract void onOutgoingCallStarted(Context paramContext, String paramString, long paramLong);

    public void onReceive(Context paramContext, Intent paramIntent) {
        if (paramIntent.getAction().equals("android.intent.action.NEW_OUTGOING_CALL")) {
            savedNumber = paramIntent.getExtras().getString("android.intent.extra.PHONE_NUMBER");
            ((TelephonyManager) paramContext.getSystemService(Context.TELEPHONY_SERVICE)).listen(this.phoneStateListener, 32);
            return;
        }
        String str = paramIntent.getExtras().getString("state");
        String params = paramIntent.getExtras().getString("incoming_number");
        if (!TextUtils.isEmpty(params)) {
            savedNumber = params;
        }
        boolean bool = str.equals(TelephonyManager.EXTRA_STATE_IDLE);
        int i = 0;
        if (!bool) {
            if (str.equals(TelephonyManager.EXTRA_STATE_OFFHOOK)) {
                i = 2;
            } else if (str.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
                i = 1;
            }
        }
        Logs.e("PhoneCallReceiver",paramContext +"---" + i + "---" + params);
        onCallStateChanged(paramContext, i, params);
    }

}
