package com.tianji.jingsuan.module;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TimePicker;
import android.widget.Toast;

import com.blankj.utilcode.util.DeviceUtils;
import com.taobao.weex.annotation.JSMethod;
import com.taobao.weex.appfram.pickers.DatePickerImpl;
import com.taobao.weex.bridge.JSCallback;
import com.taobao.weex.common.WXModule;
import com.taobao.weex.common.WXThread;
import com.taobao.weex.utils.WXLogUtils;
import com.tianji.jingsuan.Component.USBCamera;
import com.tianji.jingsuan.Component.USBWeight;
import com.tianji.jingsuan.R;
import com.tianji.jingsuan.utils.SoundsHelper;
import com.tianji.jingsuan.utils.ToastUtil;
import com.zyao89.view.zloading.ZLoadingView;
import com.zyao89.view.zloading.Z_TYPE;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static android.content.Context.WIFI_SERVICE;

/**
 * Created by chencentury on 2018/4/2.
 */

public class utils extends WXModule {
    public static final String TAG = "RongLog========>";
    private static String DOCUMENTS = "documents/";

    private WifiManager wifiManager;
    private int level;

    public utils() {
        super();
    }

    @JSMethod(uiThread = false)
    public void getWifiStrong(JSCallback success) {
        try {
            wifiManager = (WifiManager) mWXSDKInstance.getContext().getApplicationContext().getSystemService(WIFI_SERVICE);
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            level = wifiInfo.getRssi();
//            Log.i(TAG, "getWifiStrong: "+level);
            success.invoke("" + level);
        } catch (Exception e) {
            success.invoke("-200");
        }
    }

    @JSMethod(uiThread = false)
    public void getFileAbsolutePath(JSCallback callback) {
        String printTxtPath = getDiskCacheDir(mWXSDKInstance.getContext(), "sample/");
        if (callback != null) {
            callback.invoke(printTxtPath + "1523864817013.jpg");
        }
    }


    public static String getDiskCacheDir(Context context, String path) {
        String cachePath;
        if (Environment.MEDIA_MOUNTED.equals(Environment
                .getExternalStorageState())
                || !Environment.isExternalStorageRemovable()) {
            cachePath = context.getExternalCacheDir().getPath();
        } else {
            cachePath = context.getCacheDir().getPath();
        }
        String printTxtPath = cachePath + File.separator + path;
        File file = new File(printTxtPath);
        if (!file.exists()) {
            file.mkdir();
        }
        return printTxtPath;
    }

    @JSMethod(uiThread = false)
    public void getStandard(JSCallback succ) {
        succ.invoke("" + USBWeight.getStandard());
    }

    @JSMethod(uiThread = false)
    public void takePicter(Boolean rORl, JSCallback succ) {
        Log.i(TAG, "takePicter: " + rORl);
        if (rORl) {
            USBCamera.TakePicter(succ, USBCamera.xySampling);
        } else {
            USBCamera.TakePicterR(succ, USBCamera.xySampling);
        }

    }

    @JSMethod(uiThread = false)
    public void uploadImage(String path, String group, JSCallback success, JSCallback error) {
        XyOss xyOss = XyOss.getInstance(mWXSDKInstance.getContext());
        xyOss.upload(path, success, error, "xyExample/gp" + group);
    }

    @JSMethod(uiThread = true)
    public void setShowOrHide(Boolean b) {
        USBCamera.changeView(b);
    }

    @JSMethod
    public void console(String s) {
        Log.i(TAG, s);
    }

    @JSMethod(uiThread = false)
    public void sortBy(Object s, JSCallback callback) {
        callback.invoke(s.toString());
    }

    @JSMethod
    public void getMac(JSCallback callback) {
        String mac = DeviceUtils.getMacAddress();
        callback.invoke(mac);
    }

    @JSMethod(uiThread = false)
    public void deleteImage(String path){
//        if(!TextUtils.isEmpty(path)){
//            Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
//            ContentResolver mContentResolver = mWXSDKInstance.getContext().getContentResolver();
//            String where = MediaStore.Images.Media.DATA + "='" + path + "'";
//            //删除图片
//            mContentResolver.delete(uri, where, null);
//            //发送广播
//            Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
//            File file = new File(path);
//            Uri uri1 = Uri.fromFile(file);
//            intent.setData(uri1);
//            mWXSDKInstance.getContext().sendBroadcast(intent);
//        }
        if (!TextUtils.isEmpty(path)) {
            File file = new File(path);
            if (file.exists())
                file.delete();
        }
    }

    @JSMethod
    public void toast(String msg){
        ToastUtil.showToastInThread(mWXSDKInstance.getContext(),msg);
    }

    public static void pickTime(@NonNull Context context, String value, @NonNull final DatePickerImpl.OnPickListener listener, @Nullable Map<String, Object> extras) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(parseTime(value));
        TimePickerDialog dialog = new TimePickerDialog(
                context, AlertDialog.THEME_HOLO_LIGHT,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        String h = hourOfDay < 10 ? "0" + hourOfDay : String.valueOf(hourOfDay);
                        String m = minute < 10 ? "0" + minute : String.valueOf(minute);
                        String result = h + ":" + m;
                        listener.onPick(true, result);
                    }
                },
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                false
        );

        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                listener.onPick(false, null);
            }
        });

        setButtonText(dialog, DialogInterface.BUTTON_NEGATIVE, String.valueOf(extras != null ? extras.get("cancelTitle") : null));
        setButtonText(dialog, DialogInterface.BUTTON_POSITIVE, String.valueOf(extras != null ? extras.get("confirmTitle") : null));

        dialog.show();
    }
    private static SimpleDateFormat timeFormatter;
    private static Date parseTime(String s) {
        if (timeFormatter == null) {
            timeFormatter = new SimpleDateFormat("HH:mm", Locale.getDefault());
        }

        try {
            return timeFormatter.parse(s);
        } catch (ParseException e) {
            //don't worry
            WXLogUtils.w("[DatePickerImpl] " + e.toString());
        }
        return new Date();
    }
    private static void setButtonText(final AlertDialog dialog, final int which, final CharSequence text) {
        if (TextUtils.isEmpty(text) || "null".equals(text)) {
            return;
        }
        try {
            dialog.getWindow().getDecorView().post(WXThread.secure(new Runnable() {
                @Override
                public void run() {
                    Button button = dialog.getButton(which);
                    if (button != null) {
                        button.setAllCaps(false);
                        button.setText(text);
                    }
                }
            }));
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    @JSMethod
    public void finish(Boolean isExit,JSCallback callback){
        exit(isExit,callback);
    }

    /**
     * 点击两次退出程序
     */
    private void exit(Boolean isExit,JSCallback callback) {
        if (!isExit) {
            Toast.makeText(mWXSDKInstance.getContext().getApplicationContext(), "再按一次退出程序",
                    Toast.LENGTH_SHORT).show();
            callback.invoke("1");
        } else {
            Activity activity = (Activity) mWXSDKInstance.getContext();
            activity.finish();
            //参数用作状态码；根据惯例，非 0 的状态码表示异常终止。
//            System.exit(0);
        }
    }


    @JSMethod
    public void playSound(int type){
        //双声道
        SoundsHelper.getInstance().playSound(type, 1, 0);
        SoundsHelper.getInstance().playSound(type, 0, 1);
    }

}

