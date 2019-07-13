package com.example.websocketchat.Util;

import android.nfc.Tag;
import android.util.Log;

import org.java_websocket.WebSocket;
import org.java_websocket.WebSocketListener;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft;
import org.java_websocket.drafts.Draft_10;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.handshake.ServerHandshake;
import org.java_websocket.server.WebSocketServer;

import java.net.InetSocketAddress;
import java.net.URI;

public class WebsocketClient extends WebSocketClient {
String tag;
    public WebsocketClient(URI uri) {
        super(uri);

    }

        //private static final int NORMAL_CLOSURE_STATUS = 1000;

        @Override
        public void onOpen(ServerHandshake arg1) {
            Log.d(tag,"Connected!");
        }

        @Override
        public void onMessage(String text) {
            //Log.d(tag, String.format("Got string message! %s", text));
        }


        @Override
        public void onClose(int arg1, String arg2, boolean arg3) {
            //Log.d(tag, String.format("Disconnected! Code: %d Reason: %s", arg1, arg2));
        }

        @Override
        public void onError(Exception arg1) {
            Log.e(tag, "Error!", arg1);
        }
    }


