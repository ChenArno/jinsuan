package com.tianji.jingsuan.Component;

import android.app.Activity;
import android.graphics.SurfaceTexture;
import android.util.Log;

import com.serenegiant.usb.Size;
import com.serenegiant.usb.USBMonitor;
import com.serenegiant.usb.UVCCamera;
import com.serenegiant.usb.common.AbstractUVCCameraHandler;
import com.serenegiant.usb.common.UVCCameraHandler;
import com.serenegiant.usb.encoder.RecordParams;
import com.serenegiant.usb.widget.CameraViewInterface;

import java.util.List;

/**
 * Created by chencentury on 2018/4/22.
 */

public class XYHelper {
    private static final String TAG = "======>";
    private UVCCameraHandler mCameraHandler;
    private CameraViewInterface mcamView;
    USBMonitor.UsbControlBlock mctrlBlock;
    private boolean isRequest;
    private String deviceName = null;
    private float banwidth = 0.65f;//双摄像头，0.65f  1f //最大0.75f

    //640*480 800*600
    private int previewWidth = 640;
    private int previewHeight = 480;
    private Activity mActivity;
    // 默认使用MJPEG  //UVCCamera 53行 https://github.com/saki4510t/UVCCamera/issues/231
    private static int FRAME_FORMAT_MJPEG = UVCCamera.FRAME_FORMAT_MJPEG;
    private static int FRAME_FORMAT_YUYV = UVCCamera.FRAME_FORMAT_YUYV;

    public XYHelper(Activity activity,CameraViewInterface camView){
        createUVCCamera(activity,camView);
    }

    public XYHelper(Activity activity,CameraViewInterface camView,int width,int height){
        createUVCCamera(activity,camView,width,height);
    }

    public void closeCamera() {
        if (mCameraHandler != null) {
            mCameraHandler.close();
        }
    }

    public void createUVCCamera(Activity activity,CameraViewInterface camView) {
        mActivity = activity;
        mcamView = camView;
        if (mcamView == null)
            throw new NullPointerException("CameraViewInterface cannot be null!");

        // release resources for initializing camera handler
        if (mCameraHandler != null) {
            mCameraHandler.release();
            mCameraHandler = null;
        }
        // initialize camera handler
        mcamView.setAspectRatio(previewWidth / (float) previewHeight);
        mCameraHandler = UVCCameraHandler.createHandler(mActivity, mcamView, 2,
                previewWidth, previewHeight, FRAME_FORMAT_MJPEG,banwidth);
    }

    public void createUVCCamera(Activity activity,CameraViewInterface camView,int width,int height) {
        mActivity = activity;
        mcamView = camView;
        if (mcamView == null)
            throw new NullPointerException("CameraViewInterface cannot be null!");

        // release resources for initializing camera handler
        if (mCameraHandler != null) {
            mCameraHandler.release();
            mCameraHandler = null;
        }
        // initialize camera handler
        mcamView.setAspectRatio(previewWidth / (float) previewHeight);
        mCameraHandler = UVCCameraHandler.createHandler(mActivity, mcamView, 2,
                width, height, FRAME_FORMAT_MJPEG,banwidth);
    }

    public void updateResolution(int width, int height) {
        if (previewWidth == width && previewHeight == height) {
            return;
        }
        this.previewWidth = width;
        this.previewHeight = height;
        if (mCameraHandler != null) {
            mCameraHandler.release();
            mCameraHandler = null;
        }
        mcamView.setAspectRatio(previewWidth / (float) previewHeight);
        mCameraHandler = UVCCameraHandler.createHandler(mActivity, mcamView, 2,
                previewWidth, previewHeight, FRAME_FORMAT_MJPEG);
        openAndPreview(mctrlBlock);
    }


    public void openAndPreview(USBMonitor.UsbControlBlock ctrlBlock) {
        mctrlBlock = ctrlBlock;
        openCamera();
        new Thread(new Runnable() {
            @Override
            public void run() {
                // 休眠500ms，等待Camera创建完毕
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                // 开启预览
                startPreview();
                Log.i(TAG, "run: 开始播放");
            }
        }).start();
    }

    public boolean checkSupportFlag(final int flag) {
        return mCameraHandler != null && mCameraHandler.checkSupportFlag(flag);
    }

    public int getModelValue(final int flag) {
        return mCameraHandler != null ? mCameraHandler.getValue(flag) : 0;
    }

    public int setModelValue(final int flag, final int value) {
        return mCameraHandler != null ? mCameraHandler.setValue(flag, value) : 0;
    }

    public int resetModelValue(final int flag) {
        return mCameraHandler != null ? mCameraHandler.resetValue(flag) : 0;
    }


    public void capturePicture(String savePath, AbstractUVCCameraHandler.OnCaptureListener listener) {
        if (mCameraHandler != null && mCameraHandler.isOpened()) {
            mCameraHandler.captureStill(savePath, listener);
        }
    }

    public void startRecording(RecordParams params, AbstractUVCCameraHandler.OnEncodeResultListener listener) {
        if (mCameraHandler != null && !isRecording()) {
            mCameraHandler.startRecording(params, listener);
        }
    }

    public void stopRecording() {
        if (mCameraHandler != null && isRecording()) {
            mCameraHandler.stopRecording();
        }
    }

    public boolean isRecording() {
        if (mCameraHandler != null) {
            return mCameraHandler.isRecording();
        }
        return false;
    }

    public boolean isCameraOpened() {
        if (mCameraHandler != null) {
            return mCameraHandler.isOpened();
        }
        return false;
    }

    public void release() {
        if (mCameraHandler != null) {
            mCameraHandler.release();
            mCameraHandler = null;
        }
    }

    public void setOnPreviewFrameListener(AbstractUVCCameraHandler.OnPreViewResultListener listener) {
        if (mCameraHandler != null) {
            mCameraHandler.setOnPreViewResultListener(listener);
        }
    }

    private void openCamera() {
        if (mCameraHandler != null) {
            mCameraHandler.open(mctrlBlock);
//            UVCCamera uvcCamera = new UVCCamera();
//            uvcCamera.setPreviewSize(UVCCamera.DEFAULT_PREVIEW_WIDTH,UVCCamera.DEFAULT_PREVIEW_HEIGHT,UVCCamera.FRAME_FORMAT_MJPEG,0.6f);
        }
    }

    public void startPreview() {
        SurfaceTexture st = mcamView.getSurfaceTexture();
        if (mCameraHandler != null) {
            mCameraHandler.startPreview(st);
            this.isRequest = true;
        }
    }

    public void stopPreview() {
        if (mCameraHandler != null) {
            mCameraHandler.stopPreview();
        }
    }

    public void startCameraFoucs() {
        if (mCameraHandler != null) {
            mCameraHandler.startCameraFoucs();
        }
    }

    public List<Size> getSupportedPreviewSizes() {
        if (mCameraHandler == null)
            return null;
        return mCameraHandler.getSupportedPreviewSizes();
    }

    public void setRequest(boolean request) {
        this.isRequest = request;
    }

    public boolean isRequest() {
        return isRequest;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getDeviceName() {
        return deviceName;
    }

}
