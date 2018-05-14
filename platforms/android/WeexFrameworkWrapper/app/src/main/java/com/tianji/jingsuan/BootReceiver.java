package com.tianji.jingsuan;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;

import com.tianji.jingsuan.activity.SplashActivity;

/**
 * Created by chencentury on 2018/5/2.
 */

public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            Intent mBootIntent = new Intent(context, SplashActivity.class);
            mBootIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(mBootIntent);
        }
    }

}