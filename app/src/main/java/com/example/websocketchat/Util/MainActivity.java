package com.example.websocketchat.Util;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

import com.example.websocketchat.Util.R;
import com.example.websocketchat.Util.WebsocketServer;

import java.net.InetSocketAddress;

public class MainActivity extends AppCompatActivity {
    public static final String DEFAULT_CHAT_NAME = "";
    //Variables for activity_main.xml
    Button serverbutton;
    Button clientbutton;
    public static int identity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //UI objects for activity_main.xml
        serverbutton = findViewById(R.id.startServer);
        clientbutton = findViewById(R.id.startClient);


        //Click serverbutton to start server, display IP address and port number
        serverbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                identity = 1;
                Intent intent = new Intent(MainActivity.this, MainActivityServer.class);
                startActivity(intent);
            }
        });

        //Click clientbutton to start client, enter IP address and port number
        clientbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                identity = 2;
                /*
                Intent intent = new Intent(MainActivity.this, MainActivityClient.class);
                startActivity(intent);
                */
                //TODO-Bypass MainActivityClient which seems to be useless for now.
                //Start Message Service.
                startService(new Intent(getApplicationContext(), MessageService.class));

            }
        });

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
}
