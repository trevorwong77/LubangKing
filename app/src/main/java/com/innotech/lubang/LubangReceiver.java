package com.innotech.lubang;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by itman on 15/3/2015.
 */
public class LubangReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("onReceive", "System reboot and received event...");
        if(intent.getAction().equalsIgnoreCase(Intent.ACTION_BOOT_COMPLETED)) {
            Intent myIntent = new Intent(context, LubangService.class);
            context.startService(myIntent);
        }
    }
}