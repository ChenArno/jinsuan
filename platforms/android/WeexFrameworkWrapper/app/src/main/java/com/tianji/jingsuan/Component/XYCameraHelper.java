package com.tianji.jingsuan.Component;

import android.app.Activity;
import android.content.Context;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;

import com.serenegiant.usb.DeviceFilter;
import com.serenegiant.usb.USBMonitor;

import java.util.List;

/**
 * Created by chencentury on 2018/4/22.
 */

public class XYCameraHelper {
    public static final String SUFFIX_JPEG = ".jpg";
    public int usbType = 239;//摄像头
    private int previewWidth = 640;
    private int previewHeight = 480;

    private static XYCameraHelper xyCameraHelper;

    // USB Manager
    private USBMonitor mUSBMonitor;

    private Activity mActivity;

    private XYCameraHelper() {
    }

    public static XYCameraHelper getInstance() {
        if (xyCameraHelper == null) {
            xyCameraHelper = new XYCameraHelper();
        }
        return xyCameraHelper;
    }

    public interface OnMyDevConnectListener {
        void onAttachDev(UsbDevice device);

        void onDettachDev(UsbDevice device);

        void onConnectDev(UsbDevice device, USBMonitor.UsbControlBlock ctrlBlock, boolean isConnected);

        void onDisConnectDev(UsbDevice device);
    }

    public void initUSBMonitor(Activity activity, final XYCameraHelper.OnMyDevConnectListener listener) {
        this.mActivity = activity;
        mUSBMonitor = new USBMonitor(activity.getApplicationContext(), new USBMonitor.OnDeviceConnectListener() {
            // called by checking usb device
            // do request device permission
            @Override
            public void onAttach(UsbDevice device) {
                if (listener != null) {
                    listener.onAttachDev(device);
                }
            }

            // called by taking out usb device
            // do close camera
            @Override
            public void onDettach(UsbDevice device) {
                if (listener != null) {
                    listener.onDettachDev(device);
                }
            }

            // called by connect to usb camera
            // do open camera,start previewing
            // USBMonitor  980行
            @Override
            public void onConnect(final UsbDevice device, USBMonitor.UsbControlBlock ctrlBlock, boolean createNew) {
                if (listener != null) {
                    listener.onConnectDev(device, ctrlBlock, true);
                }
            }

            // called by disconnect to usb camera
            // do nothing
            @Override
            public void onDisconnect(UsbDevice device, USBMonitor.UsbControlBlock ctrlBlock) {
                if (listener != null) {
                    listener.onDisConnectDev(device);
                }
            }

            @Override
            public void onCancel(UsbDevice device) {
            }
        });
    }
    public void registerUSB() {
        if (mUSBMonitor != null) {
            mUSBMonitor.register();
        }
    }

    public void unregisterUSB() {
        if (mUSBMonitor != null) {
            mUSBMonitor.unregister();
        }
    }

    public void requestPermission(int index) {
        List<UsbDevice> devList = getUsbDeviceList();
        if (devList == null || devList.size() == 0) {
            return;
        }
        int count = devList.size();
        if (index >= count)
            new IllegalArgumentException("index illegal,should be < devList.size()");
        if (mUSBMonitor != null) {
            mUSBMonitor.requestPermission(getUsbDeviceList().get(index));
        }
    }

    public int getUsbDeviceCount() {
        List<UsbDevice> devList = getUsbDeviceList();
        if (devList == null || devList.size() == 0) {
            return 0;
        }
        return devList.size();
    }

    public List<UsbDevice> getUsbDeviceList() {
        List<DeviceFilter> deviceFilters = DeviceFilter
                .getDeviceFilters(mActivity.getApplicationContext(), com.jiangdg.libusbcamera.R.xml.device_filter);
        if (mUSBMonitor == null || deviceFilters == null)
            return null;
        return mUSBMonitor.getDeviceList(deviceFilters.get(0));
    }

    public void release() {
        if (mUSBMonitor != null) {
            mUSBMonitor.destroy();
            mUSBMonitor = null;
        }
    }

    public USBMonitor getUSBMonitor() {
        return mUSBMonitor;
    }

    public UsbManager getUsbManager(Context context){
        return (UsbManager) context.getSystemService(Context.USB_SERVICE);
//        return mUSBMonitor.getUsbManager();
    }

    public void setDefaultPreviewSize(int defaultWidth, int defaultHeight) {
        if (mUSBMonitor != null) {
            throw new IllegalStateException("setDefaultPreviewSize should be call before initMonitor");
        }
        this.previewWidth = defaultWidth;
        this.previewHeight = defaultHeight;
    }

    public int getPreviewWidth() {
        return previewWidth;
    }

    public int getPreviewHeight() {
        return previewHeight;
    }

    public int getUsbType() {
        return usbType;
    }
}
