package com.tianji.jingsuan.module;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;

import com.alibaba.sdk.android.oss.ClientConfiguration;
import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.OSS;
import com.alibaba.sdk.android.oss.OSSClient;
import com.alibaba.sdk.android.oss.ServiceException;
import com.alibaba.sdk.android.oss.callback.OSSCompletedCallback;
import com.alibaba.sdk.android.oss.callback.OSSProgressCallback;
import com.alibaba.sdk.android.oss.common.auth.OSSAuthCredentialsProvider;
import com.alibaba.sdk.android.oss.common.auth.OSSCredentialProvider;
import com.alibaba.sdk.android.oss.common.auth.OSSFederationToken;
import com.alibaba.sdk.android.oss.internal.OSSAsyncTask;
import com.alibaba.sdk.android.oss.model.PutObjectRequest;
import com.alibaba.sdk.android.oss.model.PutObjectResult;
import com.benmu.framework.constant.WXConstant;
import com.benmu.framework.event.shorage.EventGetData;
import com.benmu.framework.manager.ManagerFactory;
import com.benmu.framework.manager.StorageManager;
import com.benmu.framework.model.UniversalResultModule;
import com.benmu.framework.model.WeexEventBean;
import com.lzy.imagepicker.util.Utils;
import com.taobao.weex.annotation.JSMethod;
import com.taobao.weex.bridge.JSCallback;
import com.taobao.weex.common.WXModule;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by chencentury on 2018/4/4.
 */


public class XyOss {
    private static XyOss instance = null;
    private static final String TAG = "==========>";
    private String endpoint = "http://oss-cn-qingdao.aliyuncs.com";
    private String bucketName = "gdai";
    private OSS oss;
    private String stsServer = "http://47.96.255.181:3000";

    private XyOss() {
    }

    public static XyOss getInstance(Context context) {
        if (instance == null) {
            instance = new XyOss();
        }
        instance.config(context);
        instance.init(context);
        return instance;
    }

    public void config(Context context) {
        StorageManager storageManager = ManagerFactory.getManagerService(StorageManager.class);
        String result = storageManager.getData(context, "_RescSer");
        if (result == null) {
            return;
        }
        try {
            JSONObject jsonObject = new JSONObject(result);
            int id = jsonObject.getInt("id");
            String name = jsonObject.getString("name");
            if (endpoint != null) {
                endpoint = jsonObject.getString("storageUrl");
                bucketName = jsonObject.getString("bucketName");
            }
        } catch (Exception e) {

        }
    }


    public void init(Context context) {
        OSSCredentialProvider credentialProvider = new OSSAuthCredentialsProvider(stsServer);

        //该配置类如果不设置，会有默认配置，具体可看该类
        ClientConfiguration conf = new ClientConfiguration();
        conf.setConnectionTimeout(15 * 1000); // 连接超时，默认15秒
        conf.setSocketTimeout(15 * 1000); // socket超时，默认15秒
        conf.setMaxConcurrentRequest(5); // 最大并发请求数，默认5个
        conf.setMaxErrorRetry(2); // 失败后最大重试次数，默认2次
        try {
            oss = new OSSClient(context.getApplicationContext(), endpoint, credentialProvider);
        } catch (Exception e) {
            Log.d(TAG, "init: " + e);
        }
    }

    public void upload(String uploadFilePath, final JSCallback success, final JSCallback error, String thePath) {
        final String filePath = thePath + getDay() + getFileName(uploadFilePath);
        // 构造上传请求
        PutObjectRequest put = new PutObjectRequest(bucketName, filePath, uploadFilePath);
        // 异步上传时可以设置进度回调
        put.setProgressCallback(new OSSProgressCallback<PutObjectRequest>() {
            @Override
            public void onProgress(PutObjectRequest request, long currentSize, long totalSize) {
//                Log.d(TAG, "currentSize: " + currentSize + " totalSize: " + totalSize);
            }
        });
        OSSAsyncTask task = oss.asyncPutObject(put, new OSSCompletedCallback<PutObjectRequest, PutObjectResult>() {
            @Override
            public void onSuccess(PutObjectRequest request, PutObjectResult result) {
                Log.i(TAG, "UploadSuccess");
//                Log.d(TAG, result.getETag());
//                Log.d(TAG, result.getRequestId());
                success.invoke(filePath);
            }

            @Override
            public void onFailure(PutObjectRequest request, ClientException clientExcepion, ServiceException serviceException) {
                // 请求异常
                if (clientExcepion != null) {
                    // 本地异常如网络异常等
                    clientExcepion.printStackTrace();
                    error.invoke("上传图片网络异常");
                    return;
                }
                if (serviceException != null) {
                    error.invoke("服务异常");
                    return;
                    // 服务异常
                }
                error.invoke("上传oss异常");
            }
        });
        // task.cancel(); // 可以取消任务
        // task.waitUntilFinished(); // 可以等待任务完成
    }



    public static String getFileName(String filename) {
        if ((filename != null) && (filename.length() > 0)) {
            int dot = filename.lastIndexOf('/');
            if ((dot > -1) && (dot < (filename.length() - 1))) {
                return filename.substring(dot + 1);
            }
        }
        return filename;
    }

    public String getDay() {
        Calendar now = Calendar.getInstance();
        String year = String.valueOf(now.get(Calendar.YEAR));
        String month = String.valueOf((now.get(Calendar.MONTH) + 1));
        String day = String.valueOf(now.get(Calendar.DAY_OF_MONTH));
        String realPath = "/" + year + "/" + month + "/" + day + "/";
        return realPath;
    }
}
