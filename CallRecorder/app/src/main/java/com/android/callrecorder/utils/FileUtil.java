package com.android.callrecorder.utils;

import android.text.TextUtils;

import com.android.callrecorder.bean.CallItem;
import com.android.callrecorder.config.Constant;
import com.android.callrecorder.config.GlobalConfig;
import com.android.callrecorder.manager.RecordPlayerManager;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class FileUtil {
    public static final String SEP = "_";
    private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");

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
     * 获取通话记录录音文件
     * new File("/storage/emulated/0/MIUI/sound_recorder/call_rec/本机(18032408866)_20230805171115.mp3").length();
     * @return
     */
    public static List<CallItem> loadLocalRecordFile() {
        return loadLocalRecordFile(true);
    }

    /**
     *
     * @param isAll 是否返回全量本地通话录音文件，or增量数据
     * @return
     */
    public static List<CallItem> loadLocalRecordFile(boolean isAll) {
        List<CallItem> callLogs = new ArrayList<>();
        if (TextUtils.isEmpty(GlobalConfig.url)) return callLogs;
        File file = new File(GlobalConfig.url);
        if (file.isDirectory()) {
            File[] filesChilds = file.listFiles();
            for (File recordFile : filesChilds) {
                CallItem callItem = null;
                if (recordFile.isDirectory()) {
                    File[] files = recordFile.listFiles();
                    for (File recordItem : files) {
                        callItem = getRecordInfo(recordItem,isAll);
                        if (callItem==null){continue;}
                        callItem.phone = recordFile.getName();//更改文件名为目录名，目录为电话号码
                        callLogs.add(callItem);
                    }
                } else {
                    callItem = getRecordInfo(recordFile,isAll);
                    if (callItem==null){continue;}
                    callLogs.add(callItem);
                }
            }
        }
        return callLogs;
    }


    private static CallItem getRecordInfo(File recordFile,boolean isAll) {
        String callRecordPath = recordFile.getAbsolutePath();
        String fileName = recordFile.getName();
        long fileTime = recordFile.lastModified();
        long uploadTime = SharedPreferenceUtil.getInstance().getCallLogUploadTime();
        if (!isAll&&fileTime < uploadTime) return null;//获取时间点以后新生成的录音文件，如果是全量,不过滤
        int duration = RecordPlayerManager.getInstance().getDuration(callRecordPath);
        CallItem callItem = new CallItem();
        callItem.phone = StringUtil.getPhoneNum(fileName);
        callItem.name = StringUtil.getChinese(fileName);
        callItem.time = fileTime;

        String date = dateFormat.format(callItem.time);
        callItem.timeStr = date;

        callItem.during = duration / 60;
        int minutes = (int) (callItem.during / 60);
        int seconds = (int) (callItem.during % 60);
        String minute = minutes == 0 ? "" : minutes + "分";
        String second = seconds + "秒";
        callItem.duringStr = minute + second;
//                callItem.callType = callType;
        callItem.recordPath = callRecordPath;
        return callItem;
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
