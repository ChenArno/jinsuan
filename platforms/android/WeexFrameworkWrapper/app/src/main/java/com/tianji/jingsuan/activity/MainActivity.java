package com.tianji.jingsuan.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.benmu.framework.activity.AbstractWeexActivity;
import com.pgyersdk.javabean.AppBean;
import com.pgyersdk.update.PgyUpdateManager;
import com.pgyersdk.update.UpdateManagerListener;
import com.tianji.jingsuan.R;
import com.tianji.jingsuan.module.MQHelper;
import com.tianji.jingsuan.utils.SoundsHelper;

public class MainActivity extends AbstractWeexActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        hideBottomUIMenu();

        initView();
        renderPage();
        pgyUpdate();
        // 初始化音频一下，加载音频文件需要时间，不然第一次点击测试音响会没声音
        SoundsHelper.getInstance();
    }

    private void initView() {
        mContainer = (ViewGroup) findViewById(R.id.layout_container);
    }

    /**
     * 隐藏虚拟按键，并且全屏
     */
    protected void hideBottomUIMenu() {
        Window _window = getWindow();

        WindowManager.LayoutParams params = _window.getAttributes();
        params.systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE | View.SYSTEM_UI_FLAG_FULLSCREEN;
        _window.setAttributes(params);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);//屏幕常亮
    }

    private void pgyUpdate() {
        PgyUpdateManager.register(MainActivity.this, new UpdateManagerListener() {
            @Override
            public void onNoUpdateAvailable() {
//                Log.i("======>", "暂无新版本: ");
            }

            @Override
            public void onUpdateAvailable(String result) {
// 将新版本信息封装到AppBean中
                final AppBean appBean = getAppBeanFromString(result);
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("更新")
                        .setMessage(appBean.getReleaseNote())
                        .setNegativeButton(
                                "确定",
                                new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(
                                            DialogInterface dialog,
                                            int which) {
                                        startDownloadTask(
                                                MainActivity.this,
                                                appBean.getDownloadURL());
                                    }
                                }).show();
            }
        });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        SoundsHelper.getInstance().releaseAndDestroy();

        new Thread(new Runnable() {
            @Override
            public void run() {
                MQHelper.getMqHelper().close();
            }
        }).start();
    }
}
