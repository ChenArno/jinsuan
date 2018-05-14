package com.tianji.jingsuan.Component;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

import com.taobao.weex.WXSDKInstance;
import com.taobao.weex.annotation.JSMethod;
import com.taobao.weex.bridge.JSCallback;
import com.taobao.weex.dom.WXDomObject;
import com.taobao.weex.ui.component.WXComponent;
import com.taobao.weex.ui.component.WXVContainer;
import com.taobao.weex.ui.view.WXFrameLayout;
import com.tianji.jingsuan.R;

/**
 * Created by chencentury on 2018/4/11.
 */

public class Preview extends WXVContainer<WXFrameLayout> {
    private final String TAG = "==========>";
    private static WXFrameLayout layout;
    private static View view;

    public Preview(WXSDKInstance instance, WXDomObject dom, WXVContainer parent) {
        super(instance, dom, parent);
    }

    @Override
    public WXFrameLayout initComponentHostView(@NonNull Context context) {
        layout = new WXFrameLayout(context);
        view = USBCamera.getPreviewView();
        if (view == null) {
            View nullView = new View(context);
            view = nullView;
        }
        layout.addView(view);
        USBCamera.changeInit();
        return layout;
    }

    public static View getPreviewView() {
        layout.removeView(view);
        return view;
    }

    @JSMethod
    public void setShowOrHide(Boolean show, Boolean lorr) {
        if (show) {
            USBCamera.hideAll(lorr ? 1 : 2);
            view.setVisibility(View.VISIBLE);
        } else {
            USBCamera.hideAll(3);
            view.setVisibility(View.INVISIBLE);
        }
//        USBCamera.onViewPasue(show);
    }
}
