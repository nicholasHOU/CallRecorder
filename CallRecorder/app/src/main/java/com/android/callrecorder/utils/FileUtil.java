package com.android.callrecorder.utils;

import android.text.TextUtils;

import com.android.callrecorder.bean.CallItem;
import com.android.callrecorder.bean.response.BaseResponse;
import com.android.callrecorder.config.Constant;
import com.android.callrecorder.config.GlobalConfig;
import com.android.callrecorder.http.MyHttpManager;
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
import java.util.Map;

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
     * @return
     */
    public static List<CallItem> loadLocalRecordFile() {
        List<CallItem> callLogs = new ArrayList<>();
        File file = new File(GlobalConfig.url);
        if (file.isDirectory()) {
            File[] filesChilds = file.listFiles();
            for (File recordFile : filesChilds) {
                CallItem callItem = null;
                if (recordFile.isDirectory()) {
                    File[] files = recordFile.listFiles();
                    for (File recordItem : files) {
                        callItem = getRecordInfo(recordItem);
                        callItem.phone = recordFile.getName();//更改文件名为目录名，目录为电话号码
                        callLogs.add(callItem);
                    }
                } else {
                    callItem = getRecordInfo(recordFile);
                    callLogs.add(callItem);
                }
            }
        }
        return callLogs;
    }


    private static CallItem getRecordInfo(File recordFile) {
        String callRecordPath = recordFile.getAbsolutePath();
        int duration = RecordPlayerManager.getInstance().getDuration(callRecordPath);
        CallItem callItem = new CallItem();
        callItem.phone = StringUtil.checkNum(callRecordPath);
        callItem.name = "";
        callItem.time = recordFile.lastModified();
        String date = dateFormat.format(callItem.time);
        callItem.timeStr = date;

        callItem.during = duration/60;
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


    /**
     * “time”:13123131,  //时间戳
     * “video”: file,  //视频文件
     * “long”: 12312,  // 视频的长度
     * “phone”: 13211111111, // 手机号
     * “back”: “手机号的备注” // 手机号备注   可以为空
     */
    public static  void uploadFile(Map params) {
        MyHttpManager.getInstance().post(params, Constant.URL_UPLOAD_RECORD, 125,
                new MyHttpManager.ResponseListener<BaseResponse>() {
                    @Override
                    public void onHttpResponse(int requestCode, boolean isSuccess, BaseResponse resultJson) {
                        if (isSuccess) {
                            // 已上传成功的更新上传时间戳
                            SharedPreferenceUtil.getInstance().setRecordUploadTime((Long) params.get("time"));
                        } else {
                            if (resultJson != null && Constant.HttpCode.HTTP_NEED_LOGIN == resultJson.code) {
//                                goLogin();
                            } else {
//                            ToastUtil.showToast("，请稍后重试");
                            }
                        }
                    }

                    @Override
                    public Class getTClass() {
                        return BaseResponse.class;
                    }
                });
    }

}
