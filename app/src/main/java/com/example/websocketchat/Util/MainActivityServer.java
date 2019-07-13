package com.example.websocketchat.Util;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.java_websocket.WebSocket;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.List;


public class MainActivityServer extends AppCompatActivity {
    //Variables for activity_mainserver.xml
    private static final String TAG="MainActivityServer";

    ServerUni serveruni;
    ChatActivity chatactivity;
    TextView infoip;
    static TextView msg;
    Button lalabutton;
    public EditText ettrial;
    int counter=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainserver);

        //Start Message Service.
        startService(new Intent(getApplicationContext(), MessageService.class));


        infoip = (TextView) findViewById(R.id.infoip);
        msg = (TextView) findViewById(R.id.msg);
        lalabutton=findViewById(R.id.lalabutton);
        ettrial=findViewById(R.id.etTrial);

        //TODO- Try to build a universal Server class not exclusive for MainActivityServer only!
        //TODO- But how to update UI elements if Server class were to be universal!?
        msg.setText("Please connect client to the above IP address and port number.\n");

       infoip.setText(getIpAddress() + ":" + serveruni.port);


       lalabutton.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               String st=ettrial.getText().toString();
               serveruni.ws.send(st);  //Can't call variables from another thread without calling the thread first.
               ettrial.setText("");

           }
       });


    }




    public static void updateUItext(String text){
        msg.setText(text);
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
