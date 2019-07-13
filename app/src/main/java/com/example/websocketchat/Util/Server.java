package com.example.websocketchat.Util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;


public class Server extends Thread {
    public static ArrayList<InetAddress> clients; //is this necessary?
    private static final String TAG = "Server";

    MainActivityServer activity;
    Context context;
    ChatActivity activity2;
    public WebsocketServer serverSocket;
    String message = "";
    String ClientMsg = "";
    static final int socketServerPORT = 8080;
    public int count = 0;
    String ReceivedText = "";
    public InetSocketAddress inetSockAddress;
    public WebSocket websocket;
    public ClientHandshake clienthandshake;
    public String msgText;


    //String ipAddress= "14.192.208.143"; //Key-in home internet external IP (14.192.208.143);
    //String ipAddress="192.168.1.101";
    //String ipAddress="100.102.140.117"; //Private IP assigned by 4G tower (should be similar to LAN IP but wider range!)
    //String ipAddress="113.210.119.208"; //Public IP of 4G tower internet connection.
    //String ipAddress="10.106.144.4"; //Useless stuff! Got this from router setting!It should be IP assigned by TM modem to wireless router (useless stuff)!
    String ipAddress="192.168.1.102";

    public Server(MainActivityServer activity) {
        //this.activity = activity;
        this.activity=activity;
        Thread socketServerThread = new Thread(new SocketServerThread());
        socketServerThread.start();
        clients = new ArrayList<InetAddress>();
    }

    private class SocketServerThread extends Thread {

        @Override
        public void run() {

                activity.msg.setText("2)SocketServerThread started!");



            InetSocketAddress inetSockAddress = new InetSocketAddress(8080); //Port number can be any number!
            //InetSocketAddress inetSockAddress = new InetSocketAddress(6000); //Try to setup server on android emulator and let host machine connects to it.


            serverSocket = new WebsocketServer(inetSockAddress) {

                @Override
                public void onOpen(WebSocket webSocket, ClientHandshake arg1) { //arg1 should be for identifying Client
                    count++;
                    websocket=webSocket;
                    clienthandshake=arg1;

                    //TODO - webSocket.getRemoteSocketAddress -> add to list -> use it to verify clients.


                    webSocket.getRemoteSocketAddress();
                    //webSocket.send("Hello my little friend! I'm the Potato Man!");
                    message += "#" + count + " from "
                            + webSocket.getRemoteSocketAddress()+ "\n"; //WebSocket can't get Port so no choice but to use fixed Port.

                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            activity.counter++;
                           // activity.goToChat();
                            activity.msg.setText(message); //This is only setting Text on Server side.

                        }
                    });


                }
                @Override
                public void onMessage(WebSocket webSocket, String text) {
                    message+= "Received: "+text+"\n";
                    msgText=text;
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            activity.msg.setText(message);

                        }
                    });
                   // webSocket.isClosed();
                }


            };
            serverSocket.start();

        }

    }
    public int getPort() {
        return socketServerPORT;
    }


    public String getIpAddress() {
        String ip = "";
        try {
            Enumeration<NetworkInterface> enumNetworkInterfaces = NetworkInterface
                    .getNetworkInterfaces();
            while (enumNetworkInterfaces.hasMoreElements()) {
                NetworkInterface networkInterface = enumNetworkInterfaces
                        .nextElement();
                Enumeration<InetAddress> enumInetAddress = networkInterface
                        .getInetAddresses();
                while (enumInetAddress.hasMoreElements()) {
                    InetAddress inetAddress = enumInetAddress
                            .nextElement();

                    if (inetAddress.isSiteLocalAddress()) {
                        ip += "Server running at : "
                                + inetAddress.getHostAddress();
                    }
                }
            }

        } catch (SocketException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            ip += "Something Wrong! " + e.toString() + "\n";
        }
        return ip;
    }


}
