package com.example.websocketchat.AsyncTasks;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;

import com.example.websocketchat.Entities.Message;
import com.example.websocketchat.Util.ChatFragmentMaster;
import com.example.websocketchat.Util.R;
import com.example.websocketchat.Util.MainActivity;


public class AbstractReceiver extends AsyncTask<Void, Message, Void>{
    private static final String TAG = "AbstractReceiver";
    @Override
    protected Void doInBackground(Void... params) {
        return null;
    }

    protected void playNotification(Context context, Message message){
        Log.d(TAG,"playNotification function is called!");
        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        //Intent exactly like Android intent to launch the app => the app is simply brought back to front
        Intent intent = new Intent(context, ChatFragmentMaster.class);
        intent.setAction(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);


        PendingIntent pIntent = PendingIntent.getActivity(context, 0, intent, 0);

        Notification mNotification = new Notification.Builder(context)
                .setContentTitle(message.getChatName())
                .setContentText(message.getmText())
                .setSmallIcon(R.drawable.icon_notification)
                .setContentIntent(pIntent)
                .setSound(notification)
                .build();

//		.addAction(R.drawable.ic_launcher, "View", pIntent)
//		.addAction(0, "Remind", pIntent)

        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        mNotification.flags |= Notification.FLAG_AUTO_CANCEL;
        mNotification.defaults |= Notification.DEFAULT_VIBRATE;

        //Check if the app is in foreground or not. Display the notification only if the app is in background
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        if(!pref.getBoolean("isForeground", false)){
            mNotificationManager.notify(0, mNotification);
        }
    }
}

