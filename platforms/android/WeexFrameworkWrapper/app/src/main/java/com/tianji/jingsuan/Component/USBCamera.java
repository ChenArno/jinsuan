package com.tianji.jingsuan.Component;

import android.app.Activity;
import android.content.Context;
import android.hardware.input.InputManager;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.InputDevice;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.View;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.igexin.sdk.PushBuildConfig;
import com.serenegiant.usb.USBMonitor;
import com.serenegiant.usb.common.AbstractUVCCameraHandler;
import com.serenegiant.usb.widget.CameraViewInterface;
import com.taobao.weex.WXSDKInstance;
import com.taobao.weex.annotation.JSMethod;
import com.taobao.weex.bridge.JSCallback;
import com.taobao.weex.dom.WXDomObject;
import com.taobao.weex.ui.component.WXVContainer;
import com.taobao.weex.ui.view.WXFrameLayout;
import com.tianji.jingsuan.R;
import com.tianji.jingsuan.module.XyOss;
import com.tianji.jingsuan.module.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static android.content.Context.INPUT_SERVICE;

/**
 * Created by chencentury on 2018/4/22.
 */

public class USBCamera extends WXVContainer<WXFrameLayout> {
    private final String TAG = "==========>";
    private static WXSDKInstance mWXSDKInstance;
    private static Context mcontext;
    private XyOss xyOss;
    private static WXFrameLayout layout;
    private static View mTextureViewL;
    private static View mTextureViewR;
    private static View view;

    private CameraViewInterface mUVCCameraViewL;
    private CameraViewInterface mUVCCameraViewR;

    private XYCameraHelper mCameraHelper;

    private static XYHelper xyHelper;
    private static XYHelper xyHelperR;

    private static String xyCamera = "";
    public static String xySampling = "";
    private static final int SET_LVISIBLE_RVISIBLE = 0;  //左显示，右显示
    private static final int SET_LVISIBLE_RVINISIBLE = 1;  //左显示，右隐藏
    private static final int SET_LINVISIBLE_RVISIBLE = 2;  //左隐藏，右显示
    private static final int SET_LINVISIBLE_RINVISIBLE = 3;  //左隐藏，右隐藏
    private static Handler handler;

    //重力传感器
    private UsbManager mUsbManager;
    private USBWeight usbWeight;
    private Boolean isPersimmon = false;//传感器权限


    public USBCamera(WXSDKInstance instance, WXDomObject dom, WXVContainer parent) {
        super(instance, dom, parent);
        if (mWXSDKInstance == null) {
            mWXSDKInstance = instance;
        }
    }

    @Override
    protected WXFrameLayout initComponentHostView(@NonNull Context context) {
        layout = new WXFrameLayout(context);
        if (mcontext == null) {
            mcontext = context;
        }
        view = LayoutInflater.from(getContext()).inflate(R.layout.usb_camera, null);
        mTextureViewL = view.findViewById(R.id.cameraViewL);
        mTextureViewR = view.findViewById(R.id.cameraViewR);
        layout.addView(view);

        // step.1 initialize UVCCameraHelper
        mUVCCameraViewL = (CameraViewInterface) mTextureViewL;//强转view
        mUVCCameraViewL.setCallback(mcallback);

        mUVCCameraViewR = (CameraViewInterface) mTextureViewR;//强转view
        mUVCCameraViewR.setCallback(mcallback);
        createHandler();
        xyOss = XyOss.getInstance(mWXSDKInstance.getContext());
        return layout;
    }

    private void detectUsbDeviceWithInputManager() {
        InputManager im = (InputManager) mWXSDKInstance.getContext().getSystemService(INPUT_SERVICE);
        int[] devices = im.getInputDeviceIds();
        for (int id : devices) {
            InputDevice device = im.getInputDevice(id);
            Log.d(TAG, "detectUsbDeviceWithInputManager: " + JSON.toJSONString(device));
            //do something
        }
    }

    @JSMethod
    public void uvcInit() {
        if (mCameraHelper != null) return;
        mCameraHelper = XYCameraHelper.getInstance();
        Activity activity = ((Activity) mcontext);
        mCameraHelper.initUSBMonitor(activity, listener);
        xyHelper = new XYHelper(activity, mUVCCameraViewL);
        xyHelperR = new XYHelper(activity, mUVCCameraViewR);

        usbWeight = USBWeight.getUsbWeight(mCameraHelper, mWXSDKInstance);
        mCameraHelper.registerUSB();
        createFile();
//        detectUsbDeviceWithInputManager();
    }

    @JSMethod
    public void uvcInitAgain() {
        layout.addView(Preview.getPreviewView());
    }

    @JSMethod
    public void photo(final JSCallback succ) {
//        Log.i(TAG, "photo: "+xyHelperR.isRequest());
        if (!xyHelperR.isRequest()) {
            TakePicter(succ, xyCamera);
            return;
        }
        TakePicter(succ, xyCamera, true);
    }

    @JSMethod
    public void uploadImage(String path, JSCallback success, JSCallback error) {
//        Log.i(TAG, "开始uploadImage: ");
        xyOss.upload(path, success, error, "xyFileManage");
    }

    private XYCameraHelper.OnMyDevConnectListener listener = new XYCameraHelper.OnMyDevConnectListener() {
        // 插入USB设备
        @Override
        public void onAttachDev(UsbDevice device) {
            if (device.getDeviceClass() == mCameraHelper.getUsbType()) {
                if (xyHelper.getDeviceName() == null) {
                    xyHelper.setDeviceName(device.getDeviceName());
                    xyHelper.setRequest(false);
                    mCameraHelper.getUSBMonitor().requestPermission(device);
                } else if (xyHelperR.getDeviceName() == null) {
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
//                    Log.i(TAG, "onAttachDev: "+device.getDeviceClass());
                    xyHelperR.setDeviceName(device.getDeviceName());
                    xyHelperR.setRequest(false);
                    mCameraHelper.getUSBMonitor().requestPermission(device);
                }
                return;
            }

            usbWeight.getWeightDevices(device);
        }

        // 连接USB设备成功
        @Override
        public void onConnectDev(UsbDevice device, final USBMonitor.UsbControlBlock ctrlBlock, boolean isConnected) {
            Log.i(TAG, "onConnectDev: " + device.getDeviceName());
            if (device.getDeviceClass() == mCameraHelper.getUsbType()) {
                if (!xyHelper.isRequest() && xyHelper.getDeviceName().equals(device.getDeviceName())) {
                    xyHelper.openAndPreview(ctrlBlock);
                    connectState("成功", "cameraL");
                } else if (!xyHelperR.isRequest() && xyHelperR.getDeviceName().equals(device.getDeviceName())) {
//                    new Thread(new Runnable() {
//                        @Override
//                        public void run() {
//                            // 休眠500ms，等待Camera创建完毕
//                            try {
//                                Thread.sleep(1000);
//                            } catch (InterruptedException e) {
//                                e.printStackTrace();
//                            }
//
//                        }
//                    }).start();
                    // 开启预览
                    xyHelperR.openAndPreview(ctrlBlock);
                    connectState("成功", "cameraR");
                }
                return;
            }

            if (usbWeight.getEntriesSize() > 0) {
                usbWeight.showConsoleActivity();
            }
        }

        // 拔出USB设备
        @Override
        public void onDettachDev(UsbDevice device) {
            if (device.getDeviceClass() == mCameraHelper.getUsbType()) {
                if (xyHelper.getDeviceName() != null && xyHelper.getDeviceName().equals(device.getDeviceName())) {
                    xyHelper.setDeviceName(null);
                    xyHelper.closeCamera();
                    connectState("拔出", "cameraL");
//                    Toast.makeTe
// xt(mcontext, "左camera拔出", Toast.LENGTH_SHORT).show();
                }
                if (xyHelperR.getDeviceName() != null && xyHelperR.getDeviceName().equals(device.getDeviceName())) {
                    xyHelperR.setDeviceName(null);
                    xyHelperR.closeCamera();
                    connectState("拔出", "cameraR");
//                    Toast.makeText(mcontext, "右camera拔出", Toast.LENGTH_SHORT).show();
                }
                return;
            }
            if (usbWeight.getPersimmon()) {
                usbWeight.setPersimmon(false);
//                setNullPort();
                connectState("断开", "usb");
//                Toast.makeText(mcontext, "UsbPort拔出", Toast.LENGTH_SHORT).show();
            }
        }

        // 与USB设备断开连接
        @Override
        public void onDisConnectDev(UsbDevice device) {
            String msg = "cameraL";
            if (device.getDeviceClass() == mCameraHelper.getUsbType()) {
                if (xyHelper.getDeviceName() != null && xyHelper.getDeviceName().equals(device.getDeviceName())) {
                    msg = "cameraL";
                }
                if (xyHelperR.getDeviceName() != null && xyHelperR.getDeviceName().equals(device.getDeviceName())) {
                    msg = "cameraR";
                }
            } else {
                msg = "usb";
            }
            connectState("断开", msg);
        }
    };

    private CameraViewInterface.Callback mcallback = new CameraViewInterface.Callback() {
        @Override
        public void onSurfaceCreated(CameraViewInterface view, Surface surface) {
            if (view.equals(mUVCCameraViewL)) {
                if (xyHelper != null && xyHelper.isCameraOpened()) {
                    xyHelper.startPreview();
                }
            }
            if (view.equals(mUVCCameraViewR)) {
                if (xyHelperR != null && xyHelperR.isCameraOpened()) {
                    xyHelperR.startPreview();
                }
            }
        }

        @Override
        public void onSurfaceChanged(CameraViewInterface view, Surface surface, int width, int height) {

        }

        @Override
        public void onSurfaceDestroy(CameraViewInterface view, Surface surface) {
            if (view.equals(mUVCCameraViewL)) {
                if (xyHelper != null && xyHelper.isCameraOpened()) {
                    xyHelper.stopPreview();
                }
            }
            if (view.equals(mUVCCameraViewR)) {
                if (xyHelperR != null && xyHelperR.isCameraOpened()) {
                    xyHelperR.stopPreview();
                }
            }
        }
    };

    public static void onViewPasue(Boolean goon) {
        if (goon) {
            if (xyHelper != null && xyHelper.isCameraOpened()) {
                xyHelper.startPreview();
            }
            if (xyHelperR != null && xyHelperR.isCameraOpened()) {
                xyHelperR.startPreview();
            }
        } else {
            if (xyHelper != null && xyHelper.isCameraOpened()) {
                xyHelper.stopPreview();
            }
            if (xyHelperR != null && xyHelperR.isCameraOpened()) {
                xyHelperR.stopPreview();
            }
        }
    }

    public void createFile() {
        utils mutils = new utils();
        xyCamera = mutils.getDiskCacheDir(mcontext, "documents/");
        xySampling = mutils.getDiskCacheDir(mcontext, "sample/");
    }

    public static View getPreviewView() {
        layout.removeView(view);
        return view;
    }

//    public static void onstop(JSCallback succ) {
////        if (xyHelper != null) {
////            xyHelper.stopPreview();
////        }
////        if (xyHelperR != null) {
////            xyHelperR.stopPreview();
////        }
//        succ.invoke("succ");
//    }

    @JSMethod
    public static void changeInit() {
        Message msg = new Message();
        msg.what = SET_LVISIBLE_RVINISIBLE;
        handler.sendMessage(msg);
    }

    public static void hideAll(int chose) {
        Message msg = new Message();
        switch (chose) {
            case 3:
                msg.what = SET_LINVISIBLE_RINVISIBLE;
                break;
            case 1:
                msg.what = SET_LVISIBLE_RVINISIBLE;
                break;
            case 2:
                msg.what = SET_LINVISIBLE_RVISIBLE;
                break;
        }
        handler.sendMessage(msg);
    }

    public static void changeView(Boolean change) {
        Message msg = new Message();
        if (change) {
            msg.what = SET_LINVISIBLE_RVISIBLE;
        } else {
            msg.what = SET_LVISIBLE_RVINISIBLE;
        }
        handler.sendMessage(msg);
    }

    public static void TakePicter(final JSCallback succ, String savePath) {
        if (xyHelper == null || !xyHelper.isCameraOpened()) {
            Toast.makeText(mcontext, " sorry,左摄像头开启失败", Toast.LENGTH_SHORT).show();
            succ.invoke("");
        }

        String picPath = savePath + "L" + saveTime();
        xyHelper.capturePicture(picPath, new AbstractUVCCameraHandler.OnCaptureListener() {
            @Override
            public void onCaptureResult(final String path) {
                succ.invoke(path);
            }
        });
    }

    public static void TakePicter(final JSCallback succ, final String savePath, final Boolean x) {
        final Map<String, Object> params = new HashMap<>();
        if (xyHelper == null || !xyHelper.isCameraOpened()) {
            Toast.makeText(mcontext, " sorry,左摄像头开启失败", Toast.LENGTH_SHORT).show();
            succ.invoke("");
        }
        String picPath = savePath + "L" + saveTime();
        xyHelper.capturePicture(picPath, new AbstractUVCCameraHandler.OnCaptureListener() {
            @Override
            public void onCaptureResult(final String path) {
                if (x) {
                    TakePicterR(succ, savePath, path);
                }
            }
        });
    }

    public static void TakePicterR(final JSCallback succ, String savePath) {
        if (xyHelperR == null || !xyHelperR.isCameraOpened()) {
            Toast.makeText(mcontext, " sorry,右摄像头开启失败", Toast.LENGTH_SHORT).show();
            succ.invoke("");
        }
        String picPath = savePath + "R" + saveTime();
        xyHelperR.capturePicture(picPath, new AbstractUVCCameraHandler.OnCaptureListener() {
            @Override
            public void onCaptureResult(final String path) {
                succ.invoke(path);
            }
        });
    }

    public static void TakePicterR(final JSCallback succ, String savePath, final String leftPath) {
        final Map<String, Object> params = new HashMap<>();
        if (xyHelperR == null || !xyHelperR.isCameraOpened()) {
            Toast.makeText(mcontext, " sorry,右摄像头开启失败", Toast.LENGTH_SHORT).show();
            params.put("leftPath", leftPath);
            succ.invoke(params);
        }
        String picPath = savePath + "R" + saveTime();
        xyHelperR.capturePicture(picPath, new AbstractUVCCameraHandler.OnCaptureListener() {
            @Override
            public void onCaptureResult(final String path) {
                params.put("leftPath", leftPath);
                params.put("rightPath", path);
                succ.invoke(params);
            }
        });
    }

    public static void connectState(String s, String device) {
        Map<String, Object> params = new HashMap<>();
        params.put("connectState", s);
        params.put("device", device);
        mWXSDKInstance.fireGlobalEventCallback("connectState", params);
    }

//    private void setNullPort() {
//        Map<String, Object> params = new HashMap<>();
//        params.put("entries", "");
//        mWXSDKInstance.fireGlobalEventCallback("usb.device", params);
//    }

    private void createHandler() {
        Looper mainLooper = Looper.getMainLooper();
        handler = new Handler(mainLooper) {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case SET_LVISIBLE_RVISIBLE:
                        if (mTextureViewL != null) {
                            mTextureViewL.setVisibility(View.VISIBLE);
                        }
                        if (mTextureViewR != null) {
                            mTextureViewR.setVisibility(View.VISIBLE);
                        }
                        break;
                    case SET_LVISIBLE_RVINISIBLE:
                        if (mTextureViewL != null) {
                            mTextureViewL.setVisibility(View.VISIBLE);
                        }
                        if (mTextureViewR != null) {
                            mTextureViewR.setVisibility(View.INVISIBLE);
                        }
                        break;
                    case SET_LINVISIBLE_RVISIBLE:
                        if (mTextureViewL != null) {
                            mTextureViewL.setVisibility(View.INVISIBLE);
                        }
                        if (mTextureViewR != null) {
                            mTextureViewR.setVisibility(View.VISIBLE);
                        }
                        break;
                    case SET_LINVISIBLE_RINVISIBLE:
                        if (mTextureViewL != null) {
                            mTextureViewL.setVisibility(View.INVISIBLE);
                        }
                        if (mTextureViewR != null) {
                            mTextureViewR.setVisibility(View.INVISIBLE);
                        }
                        break;
                    default:
                        throw new RuntimeException("unsupported message:what=" + msg.what);
                }
//            super.handleMessage(msg);
            }
        };
    }

    private static String saveTime() {
        String pattern = "yyyy-MM-dd hh:mm:ss.SSS";
        long milSecond = System.currentTimeMillis();
        Date date = new Date(milSecond);
        SimpleDateFormat format = new SimpleDateFormat(pattern);
        return format.format(date) + XYCameraHelper.SUFFIX_JPEG;
//        return System.currentTimeMillis() + XYCameraHelper.SUFFIX_JPEG;
    }
}
