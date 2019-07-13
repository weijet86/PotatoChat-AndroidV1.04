package com.example.websocketchat.AsyncTasks;

import android.content.Context;
import android.util.Log;

import com.example.websocketchat.Entities.Message;
import com.example.websocketchat.Util.ChatActivity;
import com.example.websocketchat.Util.MainActivityServer;
import com.example.websocketchat.Util.Server;
import com.example.websocketchat.Util.WebsocketServer;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class ReceiveMessageServer extends AbstractReceiver {
    private static final String TAG="ReceiveMessageClient";
    private static final int SERVER_PORT = 4445; //Client sends and Server receives so both on the same port.
    private Context mContext;
    private ServerSocket serverSocket;
    Server server;
    WebsocketServer websocketserver;



    public ReceiveMessageServer(Context context){
        mContext = context;
    }

    @Override
    protected Void doInBackground(Void... params) {

            Log.d(TAG,"doInBackground-ReceiveMessageServer");


                        Message message=new Message(Message.TEXT_MESSAGE,server.msgText,null,0,null,null,"Super Man"); //Convert Json String to message.
                        publishProgress(message);


        return null;
    }

    @Override
    protected void onCancelled() {

        super.onCancelled();
    }

    @Override
    protected void onProgressUpdate(Message... values) {
        super.onProgressUpdate(values);
        Log.d(TAG,"onProgresssUpdate-ReceiveMessageServer");
        //playNotification(mContext, values[0]);

        ChatActivity.refreshList(values[0], false);
    }

}

