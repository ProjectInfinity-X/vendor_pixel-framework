package com.google.android.settings.fuelgauge.powermonitor;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

public final class BootBroadcastReceiver extends BroadcastReceiver {
    static boolean sIsDebugMode = Build.IS_ENG;

    private void setPowerMonitorReceiverEnabled(Context context) {
        Log.d("BootBroadcastReceiver", "Enable power monitor broadcast receiver.");
        context.getPackageManager().setComponentEnabledSetting(new ComponentName(context, (Class<?>) PowerMonitorBroadcastReceiver.class), 1, 1);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (sIsDebugMode && "android.intent.action.BOOT_COMPLETED".equals(intent.getAction())) {
            Log.d("BootBroadcastReceiver", "onReceive:" + intent.getAction());
            setPowerMonitorReceiverEnabled(context);
        }
    }
}
