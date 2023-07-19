package com.android.callrecorder.bean;

/**
 * 设备信息
 */
public class MobileInfo extends Entity {
    private String device;
    private boolean hasNumber;
    private boolean isNeedFmNumber;
    private String model;
    private String recordPath;

    public String getDevice() {
        return this.device;
    }

    public String getModel() {
        return this.model;
    }

    public String getRecordPath() {
        return this.recordPath;
    }

    public boolean isHasNumber() {
        return this.hasNumber;
    }

    public boolean isNeedFmNumber() {
        return this.isNeedFmNumber;
    }

    public void setDevice(String paramString) {
        this.device = paramString;
    }

    public void setHasNumber(boolean paramBoolean) {
        this.hasNumber = paramBoolean;
    }

    public void setModel(String paramString) {
        this.model = paramString;
    }

    public void setNeedFmNumber(boolean paramBoolean) {
        this.isNeedFmNumber = paramBoolean;
    }

    public void setRecordPath(String paramString) {
        this.recordPath = paramString;
    }

    public String toString() {
        return "MobileInfo{model='" + this.model + '\'' + ", device='" + this.device + '\'' + ", recordPath='" + this.recordPath + '\'' + ", hasNumber=" + this.hasNumber + ", isNeedFmNumber=" + this.isNeedFmNumber + '}';
    }
}
