package com.android.callrecorder.bean;

import java.util.Calendar;

public class CallRecordInfo
        extends Entity {
    private String callrecod;
    //    @Column(defaultValue = "0")
    private int callrecord_findstate;
    //    @Column(defaultValue = "0")
    private int callrecord_uploadstate;
    private long date;
    private int day;
    private int duration;
    private String location;
    private int month;
    private String name;
    private String number;
    private int type;
    private int year;

    public int compareTo(Object paramObject) {
        if (this == paramObject) {
            return 0;
        }
        if ((paramObject instanceof CallRecordInfo)) {
            CallRecordInfo recordInfo = (CallRecordInfo) paramObject;
            if ((this.number == recordInfo.number) && (this.date == recordInfo.date)) {
                return 0;
            }
        }
        return -1;
    }

    public String getCallrecod() {
        return this.callrecod;
    }

    public int getCallrecord_findstate() {
        return this.callrecord_findstate;
    }

    public int getCallrecord_uploadstate() {
        return this.callrecord_uploadstate;
    }

    public long getDate() {
        return this.date;
    }

    public int getDay() {
        return this.day;
    }

    public int getDuration() {
        return this.duration;
    }

    public String getLocation() {
        return this.location;
    }

    public int getMonth() {
        return this.month;
    }

    public String getName() {
        return this.name;
    }

    public String getNumber() {
        return this.number;
    }

    public int getType() {
        return this.type;
    }

    public int getYear() {
        return this.year;
    }

    public void setCallrecod(String paramString) {
        this.callrecod = paramString;
    }

    public void setCallrecord_findstate(int paramInt) {
        this.callrecord_findstate = paramInt;
    }

    public void setCallrecord_uploadstate(int paramInt) {
        this.callrecord_uploadstate = paramInt;
    }

    public void setDate(long paramLong) {
        this.date = paramLong;
        Calendar localCalendar = Calendar.getInstance();
        localCalendar.setTimeInMillis(paramLong);
        this.year = localCalendar.get(1);
        this.month = (localCalendar.get(2) + 1);
        this.day = localCalendar.get(5);
    }

    public void setDay(int paramInt) {
        this.day = paramInt;
    }

    public void setDuration(int paramInt) {
        this.duration = paramInt;
    }

    public void setLocation(String paramString) {
        this.location = paramString;
    }

    public void setMonth(int paramInt) {
        this.month = paramInt;
    }

    public void setName(String paramString) {
        this.name = paramString;
    }

    public void setNumber(String paramString) {
        this.number = paramString;
    }

    public void setType(int paramInt) {
        this.type = paramInt;
    }

    public void setYear(int paramInt) {
        this.year = paramInt;
    }

    public String toString() {
        return "CallLogInfo{name='" + this.name + '\'' + ", number='" + this.number + '\'' + ", type=" + this.type + ", date=" + this.date + ", duration=" + this.duration + ", year=" + this.year + ", month=" + this.month + ", day=" + this.day + ", location='" + this.location + '\'' + ", callrecod='" + this.callrecod + '\'' + ", callrecord_uploadstate=" + this.callrecord_uploadstate + ", callrecord_findstate=" + this.callrecord_findstate + '}';
    }
}
