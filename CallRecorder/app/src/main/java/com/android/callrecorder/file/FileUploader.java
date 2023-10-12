package com.android.callrecorder.file;

import android.app.Application;
import android.text.TextUtils;

import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.OSSClient;
import com.alibaba.sdk.android.oss.ServiceException;
import com.alibaba.sdk.android.oss.callback.OSSCompletedCallback;
import com.alibaba.sdk.android.oss.callback.OSSProgressCallback;
import com.alibaba.sdk.android.oss.common.auth.OSSCredentialProvider;
import com.alibaba.sdk.android.oss.common.auth.OSSStsTokenCredentialProvider;
import com.alibaba.sdk.android.oss.internal.OSSAsyncTask;
import com.alibaba.sdk.android.oss.model.PutObjectRequest;
import com.alibaba.sdk.android.oss.model.PutObjectResult;
import com.android.callrecorder.bean.CrashLog;
import com.android.callrecorder.bean.response.BaseResponse;
import com.android.callrecorder.config.Constant;
import com.android.callrecorder.config.GlobalConfig;
import com.android.callrecorder.http.MyHttpManager;
import com.android.callrecorder.utils.CrashHandler;
import com.android.callrecorder.utils.Logs;
import com.android.callrecorder.utils.ToastUtil;

import java.util.HashMap;
import java.util.Map;

public class FileUploader {

    public static volatile FileUploader instance;
    private Application context;
    private String bucketName;
    private OSSClient oss;

    public static FileUploader getInstance() {
        if (instance == null) {
            instance = new FileUploader();
        }
        return instance;
    }

    private FileUploader() {
    }

    public void init(Application ctx) {
        context = ctx;
        bucketName = "zhongyaoqg-com-oss3";
        // yourEndpoint填写Bucket所在地域对应的Endpoint。以华东1（杭州）为例，Endpoint填写为https://oss-cn-hangzhou.aliyuncs.com。
//        String endpoint = "https://oss-cn-beijing.aliyuncs.com";
        String endpoint = "https://oss-cn-shanghai.aliyuncs.com";
//        String endpoint = "https://zhongyaoqg-com-oss3.oss-accelerate.aliyuncs.com";
        // 从STS服务获取的临时访问密钥（AccessKey ID和AccessKey Secret）。
        String accessKeyId = GlobalConfig.AccessKeyId;
        //        String accessKeySecret = "8QXNG0OgOZll2o8vcLHYdbtUQvQQRL";
        String accessKeySecret = GlobalConfig.AccessKeySecret;
        // 从STS服务获取的安全令牌（SecurityToken）。
        String securityToken = GlobalConfig.SecurityToken;

        //    OSSCredentialProvider credentialProvider = new OSSStsTokenCredentialProvider(accessKeyId, accessKeySecret, securityToken);
        OSSCredentialProvider credentialProvider = new OSSStsTokenCredentialProvider(accessKeyId, accessKeySecret, securityToken);
        // 创建OSSClient实例。
        oss = new OSSClient(context, endpoint, credentialProvider);
    }

    public void uploadFile(String fileName, String filePath, CallBack callBack) {
        if (oss == null) return;
        // 构造上传请求。
        PutObjectRequest put = new PutObjectRequest(bucketName, fileName, filePath);

        // 异步上传时可以设置进度回调。
        put.setProgressCallback(new OSSProgressCallback<PutObjectRequest>() {
            @Override
            public void onProgress(PutObjectRequest request, long currentSize, long totalSize) {
//                Logs.d("PutObject", "currentSize: " + currentSize + " totalSize: " + totalSize);
            }
        });

        OSSAsyncTask task = oss.asyncPutObject(put, new OSSCompletedCallback<PutObjectRequest, PutObjectResult>() {
            @Override
            public void onSuccess(PutObjectRequest request, PutObjectResult result) {
                callBack.onSuccess(result.getETag(),result.getRequestId());
                Logs.d("PutObject", "UploadSuccess");
                Logs.d("ETag", result.getETag());
                Logs.d("RequestId", result.getRequestId());
            }

            @Override
            public void onFailure(PutObjectRequest request, ClientException clientExcepion, ServiceException serviceException) {
                // 请求异常。
                if (clientExcepion != null) {
                    // 本地异常，如网络异常等。
                    clientExcepion.printStackTrace();
                }
                if (serviceException != null) {
                    // 服务异常。
                    Logs.e("ErrorCode", serviceException.getErrorCode());
                    Logs.e("RequestId", serviceException.getRequestId());
                    Logs.e("HostId", serviceException.getHostId());
                    Logs.e("RawMessage", serviceException.getRawMessage());
                    callBack.onFail(serviceException.getErrorCode(),serviceException.getRequestId());
                }
                Map params = new HashMap();
                params.put("content",serviceException.getRawMessage());
                uploadFeedback(params);
            }
        });
        // task.cancel(); // 可以取消任务。
        // task.waitUntilFinished(); // 等待上传完成。
    }


    private void uploadFeedback(Map params) {
        MyHttpManager.getInstance().post(params, Constant.URL_FEEDBACK, 125,
                new MyHttpManager.ResponseListener<BaseResponse>() {
                    @Override
                    public void onHttpResponse(int requestCode, boolean isSuccess, BaseResponse resultJson) {
                        if (isSuccess) {
                            ToastUtil.showToast("文件上传失败，问题已反馈");
                        } else {
                            if (resultJson != null && Constant.HttpCode.HTTP_NEED_LOGIN == resultJson.code) {
                                ToastUtil.showToast("登录信息失效，请登录后重试");
                            } else {
                                ToastUtil.showToast("提交失败，请稍后重试");
                            }
                        }
                    }

                    @Override
                    public Class getTClass() {
                        return BaseResponse.class;
                    }
                });
    }


    public interface CallBack{
        void onSuccess(String eTag , String requstId);
        void onFail(String errorCode , String requstId);
    }
}
