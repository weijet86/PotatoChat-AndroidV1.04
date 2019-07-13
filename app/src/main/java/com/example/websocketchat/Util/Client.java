package com.example.websocketchat.Util;

import org.java_websocket.WebSocket;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft_10;
import org.java_websocket.handshake.ServerHandshake;

import java.io.IOException;
import java.net.InetAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;

public class Client extends Thread {
MainActivityClient activity;
WebSocketClient clientSocket;
    private static final int NORMAL_CLOSURE_STATUS = 1000;
    public Client(MainActivityClient activity) {
        this.activity = activity;
        Thread socketClientThread = new Thread(new Client.SocketClientThread());
        socketClientThread.start();

    }

    private class SocketClientThread extends Thread {

        @Override
        public void run () {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    activity.textsum+="2)SockClientThread started!\n";
                    activity.response.setText(activity.textsum);
                }
            });
            URI uri;
        try{
            //uri=new URI("10.106.144.4:32"); //Got this from Router setting
            //uri=new URI("100.102.140.117:1234"); //Public IP for 4G
            //uri=new URI("113.210.119.208:1234"); //Public IP for 4G
            //uri=new URI("ws://192.168.43.1:8080");//Websocket would work on local area network IP too if both Server and Client are on the same network.
            //uri=new URI("ws://echo.websocket.org"); //This websocket server is working!
            uri=new URI("ws://demos.kaazing.com/echo"); //This websocket server is working!
            //uri = new URI("ws://websockethost:8080"); //This websocket server isn't working!
            //uri = new URI("ws://14.192.208.143:8080");//This should work when Server/Client on the same LAN network.
            // I can't set up a websocket server with home ISP public IP because wireless router desn't have port forwarding set yet!!
            //uri=new URI("ws://192.168.1.102:8080");
        }catch (URISyntaxException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        return;}

            clientSocket = new WebsocketClient(uri){

                @Override
                public void onOpen(ServerHandshake arg1) {

                    clientSocket.send("Can I send text this way?");//yes you can!
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //Send message to server from here!

                            activity.textsum+="3)WebsocketClient started\n";
                            activity.response.setText(activity.textsum);
                            activity.wsClient.clientSocket.send("Hello i'm sending text! \nFinally the code is working!\nSo theory doens't lie after all!\n");
                            activity.wsClient.clientSocket.send("This is indeed another progression!\n");
                        }
                    });
                }

                @Override
                public void onMessage(String text) {
                    //Receive message from server from here!
                    activity.textsum+=text;
                    final String message=activity.textsum;
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            activity.response.setText("Receiving : " + message);
                        }
                    });
                }

            };
            clientSocket.connect(); //This is mandatory to start websocket!

        }
    }
}
