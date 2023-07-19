package com.android.callrecorder.utils;

import android.os.Environment;
import android.text.TextUtils;

import java.io.File;
import java.io.IOException;

public class FileUtil {
    public static final String SEP = "_";

    public static File file;
    public static boolean checkValid(String paramString) {
        if (TextUtils.isEmpty(paramString)) {
            return false;
        }
        file = new File(paramString);
        return (file.exists()) && (file.length() > 100L);
    }

    public static File getCallRecordSaveFile(long paramLong, String paramString) {
        String str = Environment.getExternalStorageDirectory().getPath() + File.separator + "ZDTCallRecord";
        Object localObject = new File(str);
        if (!((File) localObject).exists()) {
            ((File) localObject).mkdirs();
        }
        localObject = new StringBuilder();
        ((StringBuilder) localObject).append(paramLong).append("_").append(paramString).append(".3gp");
        file = new File(str, ((StringBuilder) localObject).toString());
        try {
            if (!file.exists()) {
                file.createNewFile();
                return file;
            }
        } catch (IOException localIOException) {
            localIOException.printStackTrace();
        }
        return file;
    }
}
