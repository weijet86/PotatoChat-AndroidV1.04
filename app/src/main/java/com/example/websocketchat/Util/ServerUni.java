package com.example.websocketchat.Util;

import android.content.Context;
import android.content.Intent;

import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import java.net.InetSocketAddress;


import com.example.websocketchat.AsyncTasks.AsyncTextUpdate;
import com.example.websocketchat.Entities.JsonToMsg;
import com.example.websocketchat.Entities.Message;
import com.example.websocketchat.Entities.MsgToJson;



//TODO- Use MessageService to start ServerUni
public class ServerUni extends Thread {
    private static final String TAG = "ServerUni";
    final Context context;
    final String classname;
    MainActivityServer mas;
    public WebsocketServer wss;
    public static WebSocket ws;
    public static int port=8080;
    Message recMsg;



    public ServerUni(final Context context, final String classname) { //TODO - How to update context without starting serveruni 3 times? Dowan to use fragment.
        this.context = context;
        this.classname = classname;


        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() { //ChatActivity only opens once when websocket is connected.
            @Override
            public void run() {
                // Do something after 5s = 5000ms
                startCA(context); //CA won't start inside onOpen.
            }
        }, 5000);




        Log.d(TAG, "WebSocketServerThread");
        wss = new WebsocketServer(new InetSocketAddress(port)) {
            @Override
            public void onOpen(WebSocket websocket, ClientHandshake arg1) {
                Log.d(TAG, "onOpen");
                //TODO-Build a client list to store websocket, client identity, and client text.
                ws = websocket;
/*
                //TODO-start ChatActivity 5seconds after onMessage receives text
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() { //ChatActivity only opens once when websocket is connected.
                    @Override
                    public void run() {
                        // Do something after 5s = 5000ms
                        startCA(context);
                    }
                }, 5000);
*/
            }

            @Override
            public void onMessage(WebSocket websocket, String text) {
                Log.d(TAG, "onMessage");
                //TODO - Convert received Json string back to message
                recMsg=new JsonToMsg().JsonToMsg(text);
                //Message mes = new Message(Message.TEXT_MESSAGE, text, null, 0,null,0,"SuperMan");
                new AsyncTextUpdate(context, false).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, recMsg);

            }
        };
        wss.start();


    }
    public static void startCA (Context context){
        Log.d(TAG,"startChat");
        Intent intent=new Intent(context,ChatActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

}