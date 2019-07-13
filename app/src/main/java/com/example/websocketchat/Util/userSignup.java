package com.example.websocketchat.Util;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.websocketchat.Entities.Message;
import com.example.websocketchat.Entities.MsgToJson;

import java.util.ArrayList;
import java.util.List;

public class userSignup extends AppCompatActivity {
    private static final String TAG = "userSignup";
    Button usersignupBut;
    EditText firstnameInput;
    EditText lastnameInput;
    EditText usernameInputSignup;
    EditText passwordInputSignup;
    List<String> usersignuplist=new ArrayList<>(4);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usersignup);
    usersignupBut=findViewById(R.id.usersignupbut);
    firstnameInput=findViewById(R.id.firstnameInput);
    lastnameInput=findViewById(R.id.lastnameInput);
    usernameInputSignup=findViewById(R.id.usernameInputSignup);
    passwordInputSignup=findViewById(R.id.passwordInputSignup);


    usersignupBut.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            usersignuplist.clear();
            usersignuplist.add(firstnameInput.getText().toString());
            usersignuplist.add(lastnameInput.getText().toString());
            usersignuplist.add(usernameInputSignup.getText().toString());
            usersignuplist.add(passwordInputSignup.getText().toString());

            //Create login message
            Message signupMsg=new Message(Message.SIGNUP_MESSAGE,"", null, 0,null,null,null);
            signupMsg.setSignUpInfo(usersignuplist);



            if(usersignuplist.get(0).equals("")){
                Log.d(TAG,"first name entry is empty");
            }else
                if (usersignuplist.get(1).equals("")) {
                Log.d(TAG, "last name entry is empty");
            }else
                if (usersignuplist.get(2).equals("")) {

                    Log.d(TAG,"username entry is empty");
                }else if (usersignuplist.get(3).equals("")) {

                    Log.d(TAG,"password entry is empty");
                }else
                {
                    //Send usersignup message to server
                    ClientUni.WSC.send(new MsgToJson().MsgToJson(signupMsg));
                }

        }
    });




    }



}
