package com.tianji.jingsuan.Component;

import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.os.AsyncTask;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.hoho.android.usbserial.driver.UsbSerialDriver;
import com.hoho.android.usbserial.driver.UsbSerialPort;
import com.hoho.android.usbserial.driver.UsbSerialProber;
import com.hoho.android.usbserial.util.SerialInputOutputManager;
import com.taobao.weex.WXSDKInstance;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.serenegiant.utils.UIThreadHelper.runOnUiThread;

/**
 * Created by chencentury on 2018/4/22.
 */

public class USBWeight {
    private static String TAG = "========>";
    private static USBWeight usbWeight;
    private List<UsbSerialPort> mEntries = new ArrayList<UsbSerialPort>();
    private static UsbManager mUsbManager;
    private static XYCameraHelper xyCameraHelper;
    private Boolean isPersimmon = false;
    private UsbSerialPort UsbPort;
    private int baudRate = 9600;
    private int data = 8;
    private SerialInputOutputManager mSerialIoManager;
    private final ExecutorService mExecutor = Executors.newSingleThreadExecutor();
    private static String message = "";
    private static int mlength = 100;
    private static int msleep = 300;
    private static WXSDKInstance mWXSDKInstance;

    public static USBWeight getUsbWeight(XYCameraHelper mCameraHelper, WXSDKInstance wxSDKInstance) {
        if (usbWeight == null) {
            usbWeight = new USBWeight();
        }
        xyCameraHelper = mCameraHelper;
        mWXSDKInstance = wxSDKInstance;
        mUsbManager = xyCameraHelper.getUsbManager(wxSDKInstance.getContext());
        return usbWeight;
    }

    public void getWeightDevices(final UsbDevice device) {
        UsbInterface anInterface = device.getInterface(0);
        int interfaceClass = anInterface.getInterfaceClass();
        if(interfaceClass != 255) return;
        new AsyncTask<Void, Void, List<UsbSerialPort>>() {
            @Override
            protected List<UsbSerialPort> doInBackground(Void... params) {
//                Log.d(TAG, "Refreshing device list ...");
                SystemClock.sleep(1000);

                List<UsbSerialDriver> drivers =
                        UsbSerialProber.getDefaultProber().findAllDrivers(mUsbManager);

                List<UsbSerialPort> result = new ArrayList<UsbSerialPort>();

                for (final UsbSerialDriver driver : drivers) {

                    final List<UsbSerialPort> ports = driver.getPorts();
//                    Log.d(TAG, String.format("+ %s: %s port%s",
//                            driver, Integer.valueOf(ports.size()), ports.size() == 1 ? "" : "s"));
                    result.addAll(ports);
                }

                return result;
            }

            @Override
            protected void onPostExecute(List<UsbSerialPort> result) {
                mEntries.clear();
                mEntries.addAll(result);
//                mAdapter.notifyDataSetChanged();
//                Toast.makeText(mcontext,"device(s) found "+mEntries.size(),Toast.LENGTH_LONG).show();
                Log.d(TAG, "Done refreshing, " + mEntries.size() + " entries found.");
//                Log.d(TAG, "onPostExecute: " + mEntries);
                if (mEntries.size() > 0) {
                    isOtherUsb(device);
                }
            }

        }.execute((Void) null);
    }


    public void isOtherUsb(final UsbDevice device) {
//        Log.d(TAG, "isOtherUsb: "+mEntries.size());
        if (xyCameraHelper != null && mEntries.size() > 0) {
            if (!isPersimmon) {
                isPersimmon = true;
                xyCameraHelper.getUSBMonitor().requestPermission(device);
            }
        }
    }

    public void setPersimmon(Boolean persimmon) {
        isPersimmon = persimmon;
    }

    public Boolean getPersimmon() {
        return isPersimmon;
    }

    public int getEntriesSize() {
        return mEntries.size();
    }

    public void showConsoleActivity() {

        UsbPort = mEntries.get(0);
        if (UsbPort != null) {
            getNowUsb();
            connection();
        }
    }

    public void getNowUsb() {
        String deviceName = UsbPort.getDriver().getClass().getSimpleName();
    }

    public void connection() {
//        Log.i(TAG, "connection: "+UsbPort.getDriver().getDevice());
        UsbDeviceConnection connection = mUsbManager.openDevice(UsbPort.getDriver().getDevice());
        if (connection == null) {
            Log.i(TAG, "connection is null");
            USBCamera.connectState("失败", "usb");
            return;
        }
        try {
            UsbPort.open(connection);
            UsbPort.setParameters(baudRate, data, UsbSerialPort.STOPBITS_1, UsbSerialPort.PARITY_NONE);
            USBCamera.connectState("成功", "usb");
        } catch (Exception e) {
            Log.e(TAG, "Error setting up device: " + e.getMessage(), e);
            USBCamera.connectState("失败", "usb");
            try {
                UsbPort.close();
            } catch (IOException e2) {
                // Ignore.
            }
            UsbPort = null;
            return;
        }
        onDeviceStateChange();
    }

    private void onDeviceStateChange() {
        stopIoManager();
        startIoManager();
    }

    private void stopIoManager() {
        if (mSerialIoManager != null) {
            Log.i(TAG, "Stopping io manager ..");
            mSerialIoManager.stop();
            mSerialIoManager = null;
        }
    }

    private void startIoManager() {
        if (UsbPort != null) {
            Log.i(TAG, "Starting io manager ..");
            mSerialIoManager = new SerialInputOutputManager(UsbPort, mListener);
            mExecutor.submit(mSerialIoManager);
        }
    }
    private final SerialInputOutputManager.Listener mListener =
            new SerialInputOutputManager.Listener() {

                @Override
                public void onRunError(Exception e) {
                    Log.d(TAG, "Runner stopped.");
                }

                @Override
                public void onNewData(final byte[] data) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //修改长度
                            updateReceivedData(data);
                        }
                    });
                }
            };

    private void updateReceivedData(final byte[] data) {
        String msg = new String(data);
//        String msg = HexDump.dumpHexString(data);//16进制
        message = message + msg;
        if (message.length() > mlength) {
            getK(message);
            message = "";
        }
    }

    private static float b = 0;
    private static float standard = 0;
    private int error = 30;
    private Map<String, Object> params = new HashMap<>();
    private Map<String, Object> params1 = new HashMap<>();
    public void getK(String data) {
        if ((data.length() > 0) && (data.indexOf("K") > -1)) {
            String[] msg = data.split("K");
            if (msg.length < 2 || msg[1].indexOf(",") == -1) return;
            final String[] newMsg = msg[1].split(",");
//            Log.d(TAG, "run: " + msg[1]);
            if (newMsg.length < 3) return;
            final String m = newMsg[1].replace(" ","");
            standard = convertToFloat(m,0);
            params1.put("msg", ""+standard);
            mWXSDKInstance.fireGlobalEventCallback("usb.device.realtime", params1);
            if(Math.abs(standard - b) > error){
                b = standard;
//                Log.d(TAG, "getK: " + b);
                params.put("msg", ""+b);
                mWXSDKInstance.fireGlobalEventCallback("usb.device.receive", params);
            }
        }
    }


    public static float getStandard(){
//        Log.i(TAG, "getStandard: "+standard);
        return standard;
    }

    //把String转化为float
    public static float convertToFloat(String number, float defaultValue) {
        if (TextUtils.isEmpty(number)) {
            return defaultValue;
        }
        try {
            return Float.parseFloat(number);
        } catch (Exception e) {
            return defaultValue;
        }

    }
}
