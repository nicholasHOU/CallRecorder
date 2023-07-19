//package com.android.callrecorder.manager;
//
//import android.os.Build;
//import android.text.TextUtils;
//
//import com.android.callrecorder.bean.MobileInfo;
//import com.android.callrecorder.config.CallRecordConstant;
//import com.android.callrecorder.utils.Logs;
//
//import io.reactivex.schedulers.Schedulers;
//
//public class CallRecordManager {
//    private static volatile CallRecordManager callRecordManager;
//    private boolean hasNumber;
//    private boolean isNeedFmNumber;
//    private boolean isUseSysCallRecord;
//    private String recordPath;
//    private long updateTime;
//
//    private CallRecordManager() {
//        getMobileInfo();
//        update();
//    }
//
//    public static CallRecordManager getIntance() {
//        if (callRecordManager == null) {
//            try {
//                if (callRecordManager == null) {
//                    callRecordManager = new CallRecordManager();
//                }
//            } finally {
//            }
//        }
//        return callRecordManager;
//    }
//
//    private void getMobileInfo() {
//        Logs.d(CallRecordConstant.mobileInfos.toString(), "new Object[0]");
//        int i = 0;
//        while (i < CallRecordConstant.mobileInfos.size()) {
//            MobileInfo localMobileInfo = (MobileInfo) CallRecordConstant.mobileInfos.get(i);
//            if ((!Build.DEVICE.equalsIgnoreCase(localMobileInfo.getDevice())) && (!Build.BRAND.equalsIgnoreCase(localMobileInfo.getDevice()))) {
//                i += 1;
//            } else {
//                this.isUseSysCallRecord = true;
//                this.recordPath = localMobileInfo.getRecordPath();
//                this.hasNumber = localMobileInfo.isHasNumber();
//                this.isNeedFmNumber = localMobileInfo.isNeedFmNumber();
//            }
//        }
//        MobileInfo localMobileInfo = (MobileInfo) DataSupport.findFirst(MobileInfo.class);
//        if ((localMobileInfo != null) && (!TextUtils.isEmpty(localMobileInfo.getRecordPath()))) {
//            this.isUseSysCallRecord = true;
//            this.recordPath = localMobileInfo.getRecordPath();
//            this.hasNumber = localMobileInfo.isHasNumber();
//            this.isNeedFmNumber = localMobileInfo.isNeedFmNumber();
//            Logs.d(localMobileInfo.toString()," new Object[0]");
//        }
//    }
//
//    public String getRecordPath() {
//        return this.recordPath;
//    }
//
//    public boolean isHasNumber() {
//        return this.hasNumber;
//    }
//
//    public boolean isNeedFmNumber() {
//        return this.isNeedFmNumber;
//    }
//
//    public boolean isUseSystemCallRecord() {
//        return this.isUseSysCallRecord;
//    }
//
//    public void update() {
//        if (System.currentTimeMillis() - this.updateTime < 21600000L) {
//            return;
//        }
//        CallLogConnector.getMobileInfo(ZdApplication.applicationContext).subscribeOn(Schedulers.io()).observeOn(Schedulers.io()).subscribe(new DefaultDisposableObserver() {
//            public void onNext(final ResponseResult<MobileInfo> paramAnonymousResponseResult) {
//                if (paramAnonymousResponseResult.success()) {
//                    paramAnonymousResponseResult = (MobileInfo) paramAnonymousResponseResult.data;
//                    CallRecordManager.access$002(CallRecordManager.this, System.currentTimeMillis());
//                    if ((paramAnonymousResponseResult != null) && (!TextUtils.isEmpty(paramAnonymousResponseResult.getRecordPath()))) {
//                        CallRecordManager.access$102(CallRecordManager.this, true);
//                        CallRecordManager.access$202(CallRecordManager.this, paramAnonymousResponseResult.getRecordPath());
//                        CallRecordManager.access$302(CallRecordManager.this, paramAnonymousResponseResult.isHasNumber());
//                        CallRecordManager.access$402(CallRecordManager.this, paramAnonymousResponseResult.isNeedFmNumber());
//                        Schedulers.io().createWorker().schedule(new Runnable() {
//                            public void run() {
//                                try {
//                                    MobileInfo localMobileInfo = (MobileInfo) DataSupport.findFirst(MobileInfo.class);
//                                    if (localMobileInfo != null) {
//                                        localMobileInfo.setRecordPath(CallRecordManager.this.recordPath);
//                                        localMobileInfo.setHasNumber(CallRecordManager.this.hasNumber);
//                                        localMobileInfo.setNeedFmNumber(CallRecordManager.this.isNeedFmNumber);
//                                        localMobileInfo.save();
//                                        return;
//                                    }
//                                    paramAnonymousResponseResult.save();
//                                    return;
//                                } catch (Exception localException) {
//                                    localException.printStackTrace();
//                                }
//                            }
//                        });
//                    }
//                }
//            }
//        });
//    }
//}
//
