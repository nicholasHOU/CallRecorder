package com.android.callrecorder.home.receiver;//package com.android.callrecorder.home.receiver;//
import android.content.Context;

public class PhoneCallReceiverImpl extends PhoneCallReceiver {
    public static final String ACTION_IN = "android.intent.action.PHONE_STATE";
    public static final String ACTION_OUT = "android.intent.action.NEW_OUTGOING_CALL";
    public static final String EXTRA_PHONE_NUMBER = "android.intent.extra.PHONE_NUMBER";


    @Override
    protected void endLastCall(Context paramContext, String paramString, long paramLong, boolean paramBoolean) {

    }

    @Override
    protected void onIncomingCallAnswered(Context paramContext, String paramString, long paramLong) {

    }

    @Override
    protected void onIncomingCallEnded(Context paramContext, String paramString, long paramLong1, long paramLong2) {

    }

    @Override
    protected void onIncomingCallReceived(Context paramContext, String paramString, long paramLong) {

    }

    @Override
    protected void onMissedCall(Context paramContext, String paramString, long paramLong) {

    }

    @Override
    protected void onOutgoingCallEnded(Context paramContext, String paramString, long paramLong1, long paramLong2) {

    }

    @Override
    protected void onOutgoingCallStarted(Context paramContext, String paramString, long paramLong) {

    }
}
