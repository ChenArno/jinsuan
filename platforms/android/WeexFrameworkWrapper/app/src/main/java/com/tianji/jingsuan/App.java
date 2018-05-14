package com.tianji.jingsuan;

import android.app.Application;
import android.content.Context;

import com.benmu.framework.BMWXApplication;
import com.blankj.utilcode.util.Utils;
import com.taobao.weex.WXSDKEngine;
import com.tianji.jingsuan.Component.Preview;
import com.tianji.jingsuan.Component.QCode;
import com.tianji.jingsuan.Component.USBCamera;
import com.tianji.jingsuan.Component.loadingView;
import com.tianji.jingsuan.module.MQTools;
import com.tianji.jingsuan.module.utils;

/**
 * Created by Carry on 2017/8/23.
 */

public class App extends BMWXApplication {
    public Application mInstance;
    private static Context context;

    public static Context getContext() {
        return context;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        context = getApplicationContext();

        WeexInit();

        Utils.init(this);
    }
    public void WeexInit(){
        try {
            WXSDKEngine.registerModule("utils",utils.class);

            WXSDKEngine.registerComponent("mqtools-view",MQTools.class);
            WXSDKEngine.registerComponent("Camera-view",USBCamera.class);
            WXSDKEngine.registerComponent("Preview-view",Preview.class);
            WXSDKEngine.registerComponent("Qcode-view",QCode.class);
            WXSDKEngine.registerComponent("loading-view",loadingView.class);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
