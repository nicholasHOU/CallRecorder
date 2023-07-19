package com.android.callrecorder.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtil
{
    private static final SimpleDateFormat mCommonDateFormat = new SimpleDateFormat("yyyy MM-dd HH:mm");

    public static String formatTime(boolean paramBoolean, long paramLong)
    {
        long l1 = 86400000;
        l1 = paramLong - paramLong / l1 * l1;
        long l2 = 3600000;
        paramLong = l1 / l2;
        l2 = l1 - l2 * paramLong;
        long l3 = 60000;
        l1 = l2 / l3;
        l2 = (l2 - l3 * l1) / 1000;
        if (paramBoolean) {
            return getTimeUnitFm(paramLong, "时", false) + getTimeUnitFm(l1, "分", false) + getTimeUnitFm(l2, "秒", true);
        }
        return getTimeUnitFm(paramLong, ":", false) + getTimeUnitFm(l1, ":", true) + getTimeUnitFm(l2, "", true);
    }

    public static String getCommonDateFormat(long paramLong)
    {
        return mCommonDateFormat.format(new Date(paramLong));
    }

    public static int getDay(long paramLong)
    {
        Calendar localCalendar = Calendar.getInstance();
        localCalendar.setTimeInMillis(paramLong);
        return localCalendar.get(5);
    }

    public static int getMonth(long paramLong)
    {
        Calendar localCalendar = Calendar.getInstance();
        localCalendar.setTimeInMillis(paramLong);
        return localCalendar.get(2) + 1;
    }

    public static String getTimeUnitFm(long paramLong, String paramString, boolean paramBoolean)
    {
        if (paramLong > 9L) {
            return paramLong + paramString;
        }
        if (paramLong <= 0L)
        {
            if (paramBoolean) {
                return "00" + paramString;
            }
            return "";
        }
        return "0" + paramLong + paramString;
    }

    public static int getYear(long paramLong)
    {
        Calendar localCalendar = Calendar.getInstance();
        localCalendar.setTimeInMillis(paramLong);
        return localCalendar.get(1);
    }
}
