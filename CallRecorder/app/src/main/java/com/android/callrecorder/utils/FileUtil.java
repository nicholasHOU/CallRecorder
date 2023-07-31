package com.android.callrecorder.utils;

import android.text.TextUtils;

import com.android.callrecorder.config.Constant;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;

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



    /**
     * 将文件转为byte[]
     * @param filePath 文件路径
     * @return
     */
    public static byte[] getRecordFile(String filePath) {
        byte[] bFile;
        try {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                bFile = Files.readAllBytes(new File(filePath).toPath());
            }else {
                bFile =getBytes(filePath);
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return bFile;
    }

    /**
     * 将文件转为byte[]
     * @param filePath 文件路径
     * @return
     */
    private static byte[] getBytes(String filePath){
        File file = new File(filePath);
        ByteArrayOutputStream out = null;
        try {
            FileInputStream in = new FileInputStream(file);
            out = new ByteArrayOutputStream();
            byte[] b = new byte[1024];
            int i = 0;
            while ((i = in.read(b)) != -1) {
                out.write(b, 0, b.length);
            }
            out.close();
            in.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        byte[] s = out.toByteArray();
        return s;
    }
}
