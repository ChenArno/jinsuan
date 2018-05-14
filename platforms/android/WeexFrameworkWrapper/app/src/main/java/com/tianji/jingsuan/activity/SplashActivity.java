package com.tianji.jingsuan.activity;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;

import com.benmu.framework.BMWXEnvironment;
import com.benmu.framework.constant.Constant;
import com.benmu.framework.constant.WXConstant;
import com.benmu.framework.manager.ManagerFactory;
import com.benmu.framework.manager.impl.ParseManager;
import com.benmu.framework.manager.impl.VersionManager;
import com.benmu.framework.manager.impl.dispatcher.DispatchEventManager;
import com.benmu.framework.model.RouterModel;
import com.benmu.framework.model.WeexEventBean;
import com.tianji.jingsuan.R;

/**
 * Created by Carry on 2017/8/23.
 */

public class SplashActivity extends Activity {
    private Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        hideBottomUIMenu();
        init();
    }

    private void init() {
        final VersionManager versionManager = ManagerFactory.getManagerService(VersionManager
                .class);
        new Thread(new Runnable() {
            @Override
            public void run() {
                long prepareTime = versionManager.prepareJsBundle(SplashActivity.this);
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        toHome();
                    }
                }, 2000 - prepareTime);
            }
        }).start();


    }

    private void toHome() {
        String homePage = BMWXEnvironment.mPlatformConfig.getPage().getHomePage(this);
        String NavigationColor = BMWXEnvironment.mPlatformConfig.getPage().getNavBarColor();
        RouterModel router = new RouterModel(homePage, Constant.ACTIVITIES_ANIMATION
                .ANIMATION_PUSH, null, null, false, null);
        DispatchEventManager dispatchEventManager = ManagerFactory.getManagerService
                (DispatchEventManager.class);
        WeexEventBean eventBean = new WeexEventBean();
        eventBean.setKey(WXConstant.WXEventCenter.EVENT_OPEN);
        eventBean.setJsParams(ManagerFactory.getManagerService(ParseManager.class).toJsonString
                (router));
        eventBean.setContext(this);
        dispatchEventManager.getBus().post(eventBean);
        finish();
    }

    /**
     * 隐藏虚拟按键，并且全屏
     */
    protected void hideBottomUIMenu() {
        //隐藏虚拟按键，并且全屏
        if (Build.VERSION.SDK_INT > 11 && Build.VERSION.SDK_INT < 19) { // lower api
            View v = this.getWindow().getDecorView();
            v.setSystemUiVisibility(View.GONE);
        } else if (Build.VERSION.SDK_INT >= 19) {
            //for new api versions.
            View decorView = getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY ;
            decorView.setSystemUiVisibility(uiOptions);
        }
    }
}
