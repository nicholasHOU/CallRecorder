package com.android.callrecorder.utils;

import android.text.TextUtils;

import com.android.callrecorder.config.Constant;

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
        Object localObject = new File(Constant.RECORD_FILE_PATH);
        if (!((File) localObject).exists()) {
            ((File) localObject).mkdirs();
        }
        localObject = new StringBuilder();
        ((StringBuilder) localObject).append(paramLong).append("_").append(paramString).append(".3gp");
        file = new File(Constant.RECORD_FILE_PATH, ((StringBuilder) localObject).toString());
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
