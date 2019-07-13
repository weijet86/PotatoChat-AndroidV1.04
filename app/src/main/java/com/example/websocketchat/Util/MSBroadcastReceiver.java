package com.example.websocketchat.Util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;


//TODO - BroadcastReceiver is now restarting MessageService (Excellent job)!
public class MSBroadcastReceiver extends BroadcastReceiver {
    private static final String TAG = "MSBroadcastReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG,"onReceive");

        /**API below 26 **/
        //context.startService(new Intent(context, MessageService.class));
        /**API 26 and above**/
        //context.startForegroundService(new Intent(context, MessageService.class));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(new Intent(context, MessageService.class));
        } else {
            context.startService(new Intent(context, MessageService.class));
        }

    }
}
