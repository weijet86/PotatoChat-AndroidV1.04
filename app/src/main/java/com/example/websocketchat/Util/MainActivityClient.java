package com.example.websocketchat.Util;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivityClient extends AppCompatActivity {
    //Variables for activity_mainclient.xml
    TextView response;
    //EditText editTextAddress, editTextPort; //Do not define variables this way if you use setOnClickListener!
    EditText editTextAddress;
    EditText editTextPort;
    Button buttonConnect;
    Button buttonClear;
    String textsum;
    Client wsClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainclient);
        //UI objects for activity_mainclient.xml
        editTextAddress = (EditText) findViewById(R.id.addressEditText);
        editTextPort = (EditText) findViewById(R.id.portEditText);
        buttonConnect =findViewById(R.id.connectButton);
        buttonClear = (Button) findViewById(R.id.clearButton);
        response = (TextView) findViewById(R.id.responseTextView);

        textsum="1)MainActivityClient started!\n";
        response.setText(textsum);
        //wsClient=new Client(this); //Starts client thread
        //wsClient.start(); //This may not be necessary! Keep it in evaluation just in case Client thread stops when ChatActivity is started!

        new ClientUni(this); //Start ClientUni thread but not inside run() of thread
    }
}