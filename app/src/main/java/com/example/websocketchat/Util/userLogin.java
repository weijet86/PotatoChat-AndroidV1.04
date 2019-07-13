package com.example.websocketchat.Util;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.example.websocketchat.Entities.Message;
import com.example.websocketchat.Entities.MsgToJson;

import java.util.ArrayList;
import java.util.List;

public class userLogin extends AppCompatActivity {
    private static final String TAG = "userLogin";
    List<String> loginInfo=new ArrayList<String>();
    public static TextView responseMsg;
    public static final int successfulsignup=1;
    public static final int nosignup=2;
    public static int loginstatus=nosignup;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userlogin);
        loginInfo.add("");
        loginInfo.add("");
        loginInfo.add("");
        loginInfo.add("");

        final EditText usernameInput=findViewById(R.id.usernameInput);
        final EditText passwordInput=findViewById(R.id.passwordInput);
        Button loginBut=findViewById(R.id.loginBut);
        Button signupBut=findViewById(R.id.signupBut);
        CheckBox showpass=findViewById(R.id.ShowPass);
        responseMsg=findViewById(R.id.responseMsg);

        //If userLogin is called from successful signup!
        if(loginstatus==successfulsignup) {
            responseMsg.setText("Your signup was successful! Please proceed to login with your new account!");
        }

       showpass.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
           @Override
           public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {

               if (isChecked==true){
                   Log.d(TAG,"showpass checkbox is checked!");
                   //Change EditText passwordInput type from "textPassword" to "textPersonName"
                   passwordInput.setInputType(EditorInfo.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);

               }else
               if(isChecked==false){
                   Log.d(TAG,"showpass checkbox is unchecked!");
                   //Change EditText passwordInput type from "textPersonName" to "textPassword"
                   passwordInput.setInputType(InputType.TYPE_CLASS_TEXT |
                           InputType.TYPE_TEXT_VARIATION_PASSWORD);
                   passwordInput.setSelection(passwordInput.getText().length());
               }
            }
            }
        );


       loginBut.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {

                String username=usernameInput.getText().toString();
                loginInfo.set(2,usernameInput.getText().toString());
                loginInfo.set(3,passwordInput.getText().toString());
                Log.d(TAG, "acquired username is "+loginInfo.get(2));
               Log.d(TAG, "acquired password is "+loginInfo.get(3));

               //Create login message
               Message loginMsg=new Message(Message.LOGIN_MESSAGE,"", null, 0,null,null,username);

               if(loginInfo.get(2).equals("")||loginInfo.get(3).equals("")){
                   Toast.makeText(v.getContext(),"Username input or password input is empty!",Toast.LENGTH_SHORT).show();
               }else
               {
                   //Send login message to server
                   ChatActivityFragment.ChatName=username;
                   loginMsg.setLoginInfo(loginInfo);
                   ClientUni.WSC.send(new MsgToJson().MsgToJson(loginMsg));
                   Log.d(TAG,"Login message has been sent to server!");
                   responseMsg.setText("Waiting for instruction from server!");
               }
           }
       });

       signupBut.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               Log.d(TAG, "Signup button is clicked!");
               startUS(v.getContext());

           }
       });




    }

    /**Start userSignup*/
    public static void startUS(Context context){

        Intent intent=new Intent(context,userSignup.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }
}
