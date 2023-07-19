package com.android.callrecorder.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class CallRecordUtil {

    public static String getOSSFileName(int paramInt, String paramString1, long paramLong, String paramString2) {
        paramString1 = paramString2.substring(paramString2.lastIndexOf("."));
        paramString2 = new SimpleDateFormat("yyyyMMdd").format(new Date(paramLong));
        String str = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date(paramLong));
        StringBuilder localStringBuilder = new StringBuilder();
        localStringBuilder.append(paramInt).append("/").append(paramString2).append("/").append(str).append(paramString1);
        return localStringBuilder.toString();
    }

    public static boolean isUseSystemCallRecord() {
        if (DeviceUtil.isHW6A()) {
            return true;
        }
        if (DeviceUtil.isHW5A()) {
            return true;
        }
        if (DeviceUtil.isHWHornorLite()) {
            return true;
        }
        if (DeviceUtil.isHongMi4A()) {
            return true;
        }
        if (DeviceUtil.isZTExx5()) {
            return true;
        }
        return DeviceUtil.isZTExxx();
    }

//    public static void macthCallRecord(Context paramContext) {
//        if (!CallRecordManager.getIntance().isUseSystemCallRecord()) {
//            return;
//        }
//        List localList = DataSupport.where(new String[]{"callrecord_findstate = 1 AND callrecod IS NULL"}).order("date desc").limit(10).find(CallRecordInfo.class);
//        int i = 0;
//        while (i < localList.size()) {
//            saveCallRecord(paramContext, (CallRecordInfo) localList.get(i));
//            i += 1;
//        }
//        RecordFinder.searchCallRecord(paramContext, 0L, true);
//    }

//    private static void saveCallRecord(Context paramContext, CallRecordInfo paramCallRecordInfo) {
//        Timber.v(paramCallRecordInfo.toString(), new Object[0]);
//        final long l1 = paramCallRecordInfo.getDate();
//        long l2 = paramCallRecordInfo.getDate();
//        long l3 = paramCallRecordInfo.getDuration();
//        final String str = paramCallRecordInfo.getNumber();
//        HashMap localHashMap = new HashMap();
//        localHashMap.put("mobile", Build.MODEL + ";" + Build.DEVICE);
//        localHashMap.put("matchretry", "true");
//        Observable.create(new ObservableOnSubscribe() {
//            public void subscribe(ObservableEmitter<String> paramAnonymousObservableEmitter)
//                    throws Exception {
//                String str = RecordFinder.getCallRecordSaveFile(CallRecordUtil.this, l1, str, this.val$number);
//                if (!TextUtils.isEmpty(str)) {
//                    MobclickAgent.onEvent(CallRecordUtil.this, "find_record_success", this.val$map);
//                    paramAnonymousObservableEmitter.onNext(str);
//                    paramAnonymousObservableEmitter.onComplete();
//                    return;
//                }
//                MobclickAgent.onEvent(CallRecordUtil.this, "find_record_failed", this.val$map);
//                paramAnonymousObservableEmitter.onComplete();
//            }
//        }).subscribe(new Consumer() {
//            public void accept(String paramAnonymousString)
//                    throws Exception {
//                if ((CallRecordUtil.this.getType() != 3) && (paramAnonymousString != null)) {
//                    CallRecordUtil.this.setCallrecod(paramAnonymousString);
//                    CallRecordUtil.this.setCallrecord_findstate(2);
//                    CallRecordUtil.this.save();
//                    EventBus.getDefault().post(new NewCallRecordEvent());
//                }
//            }
//        });
//    }
}
