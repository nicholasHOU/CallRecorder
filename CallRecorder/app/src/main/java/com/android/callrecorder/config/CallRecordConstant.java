package com.android.callrecorder.config;

import com.android.callrecorder.bean.MobileInfo;

import java.util.concurrent.CopyOnWriteArrayList;

public class CallRecordConstant {
    public static final CopyOnWriteArrayList<MobileInfo> mobileInfos;

    static {
        CopyOnWriteArrayList localCopyOnWriteArrayList = new CopyOnWriteArrayList();
        mobileInfos = localCopyOnWriteArrayList;
        MobileInfo localMobileInfo = new MobileInfo();
        localMobileInfo.setDevice("HWPRA-H");
        localMobileInfo.setHasNumber(true);
        localMobileInfo.setNeedFmNumber(true);
        localMobileInfo.setRecordPath("record");
        localCopyOnWriteArrayList.add(localMobileInfo);
        localMobileInfo = new MobileInfo();
        localMobileInfo.setDevice("HWCAM-Q");
        localMobileInfo.setHasNumber(true);
        localMobileInfo.setNeedFmNumber(true);
        localMobileInfo.setRecordPath("record");
        localCopyOnWriteArrayList.add(localMobileInfo);
        localMobileInfo = new MobileInfo();
        localMobileInfo.setDevice("HWDLI-Q");
        localMobileInfo.setHasNumber(true);
        localMobileInfo.setNeedFmNumber(true);
        localMobileInfo.setRecordPath("record");
        localCopyOnWriteArrayList.add(localMobileInfo);
        localMobileInfo = new MobileInfo();
        localMobileInfo.setDevice("P817S01");
        localMobileInfo.setHasNumber(false);
        localMobileInfo.setNeedFmNumber(true);
        localMobileInfo.setRecordPath("Records");
        localCopyOnWriteArrayList.add(localMobileInfo);
        localMobileInfo = new MobileInfo();
        localMobileInfo.setDevice("P650A30");
        localMobileInfo.setHasNumber(false);
        localMobileInfo.setNeedFmNumber(true);
        localMobileInfo.setRecordPath("Records");
        localCopyOnWriteArrayList.add(localMobileInfo);
        localMobileInfo = new MobileInfo();
        localMobileInfo.setDevice("rolex");
        localMobileInfo.setHasNumber(true);
        localMobileInfo.setNeedFmNumber(false);
        localMobileInfo.setRecordPath("MIUI/sound_recorder/call_rec");
        localCopyOnWriteArrayList.add(localMobileInfo);
        localMobileInfo = new MobileInfo();
        localMobileInfo.setDevice("Redmi");
        localMobileInfo.setHasNumber(true);
        localMobileInfo.setNeedFmNumber(false);
        localMobileInfo.setRecordPath("MIUI/sound_recorder/call_rec");
        localCopyOnWriteArrayList.add(localMobileInfo);
        localMobileInfo = new MobileInfo();
        localMobileInfo.setDevice("Xiaomi");
        localMobileInfo.setHasNumber(true);
        localMobileInfo.setNeedFmNumber(false);
        localMobileInfo.setRecordPath("MIUI/sound_recorder/call_rec");
        localCopyOnWriteArrayList.add(localMobileInfo);
    }
}