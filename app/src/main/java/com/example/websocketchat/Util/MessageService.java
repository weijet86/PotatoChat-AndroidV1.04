package com.example.websocketchat.Util;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import java.util.List;

//TODO- Start Service in MainActivityServer and make this service unkillable

public class MessageService extends Service {  //MessageService is responsible for receiving messages!
    private static final String TAG = "MessageService";
    ServerUni serveruni;
    MainActivityServer mas=new MainActivityServer();
    ChatActivity ca;
    public ComponentName componentInfo;
    public static String packageName="hahaha";



    /** This should keep MessageService alive and cannot be killed **/
    @Override
    public void onDestroy() { //Upon service being killed by OS or user, an Intent will be sent to BroadcastReceiver class to restart the service.
        Log.d(TAG,"onDestroy");
        Intent broadcastIntent = new Intent(this, MSBroadcastReceiver.class);
        //sendBroadcast(broadcastIntent);
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //Log.d(TAG,"onStartCommand");

        //TODO-If app is in ChatFragmentMaster, means Client Option has been selected and app shall start ClientUni
        if(getComponentInfo().equals("com.example.websocketchat.Util.MainActivity")) {
            new ClientUni(this);
        }

        //TODO-If app is in MainActivityServer, means Server option has been selected and app shall start ServerUni
        if(getComponentInfo().equals("com.example.websocketchat.Util.MainActivityServer")) {
            serveruni = new ServerUni(this, packageName);
        }

        return START_STICKY;
    }


    public String getComponentInfo(){
        //Log.d(TAG,"getComponentInfo");
        ActivityManager am = (ActivityManager) getApplicationContext().getSystemService(ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
        Log.d(TAG, "CURRENT Activity ::" + taskInfo.get(0).topActivity.getClassName());
        //componentInfo = taskInfo.get(0).topActivity;
        //componentInfo.getPackageName();
        //packageName = am.getRunningTasks(1).get(0).topActivity.getShortClassName();
        return taskInfo.get(0).topActivity.getClassName();

    }





}

