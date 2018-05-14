package com.tianji.jingsuan.Component;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;

import com.taobao.weex.WXSDKInstance;
import com.taobao.weex.dom.WXDomObject;
import com.taobao.weex.ui.component.WXComponent;
import com.taobao.weex.ui.component.WXVContainer;
import com.taobao.weex.ui.view.WXFrameLayout;
import com.tianji.jingsuan.R;
import com.zyao89.view.zloading.ZLoadingView;
import com.zyao89.view.zloading.Z_TYPE;

/**
 * Created by chencentury on 2018/5/7.
 */

public class loadingView extends WXComponent<View> {
    public loadingView(WXSDKInstance instance, WXDomObject dom, WXVContainer parent) {
        super(instance, dom, parent);
    }

    @Override
    protected View initComponentHostView(@NonNull Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.loading_view, null);
        ZLoadingView zLoadingView = (ZLoadingView) view.findViewById(R.id.loadingView_1);
        zLoadingView.setLoadingBuilder(Z_TYPE.SNAKE_CIRCLE);//设置类型
// zLoadingView.setLoadingBuilder(Z_TYPE.values()[type], 0.5); //设置类型 + 动画时间百分比 - 0.5倍
        zLoadingView.setColorFilter(Color.WHITE);//设置颜色
        return view;
    }
}
