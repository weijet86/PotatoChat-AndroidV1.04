package com.example.websocketchat.Entities;


/**add implementation 'com.google.code.gson:gson:2.6.2' to build.gradle for gson library.**/

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**This call is equivalent to JSON.stringify in Javascript**/
public class MsgToJson {
    private List<String> list;
    private String imgString;
    private String json;
    public String MsgToJson(Message msg) {
        Gson gson = new Gson();

        //Create ArrayList to store information in msg.
        list = new ArrayList<String>();
        list.add(Integer.toString(msg.getmType()));
        list.add(msg.getmText());
        list.add(msg.getDstAddressReceive());
        list.add(Integer.toString(msg.getPortNoReceive()));
        list.add(msg.getDstAddressSend());
        list.add(msg.getRecipientChatName());
        list.add(msg.getChatName());

        //Get message type
        int msgType=msg.getmType();

        //Create a switch to handle different types of messages
        switch(msgType){
            case Message.TEXT_MESSAGE:

                json = gson.toJson(list);
                break;
            case Message.IMAGE_MESSAGE:
                //Retrieve Image base64 string
                imgString=msg.getBase64ImageString();
                json=gson.toJson(imgString);

                //Replace the text with Image String,
                /** list[1] position contains base64 image **/
                list.set(1,json);
                json=gson.toJson(list);
                break;
            case Message.LOGIN_MESSAGE:
                //Retrieve loginInfo
                list.set(1,gson.toJson(msg.getLoginInfo()));
                json=gson.toJson(list);

                break;
            case Message.SIGNUP_MESSAGE:
                list.set(1,gson.toJson(msg.getSignupInfo()));
                json=gson.toJson(list);
                break;
        }


        //Type type = new TypeToken<List<String>>(){}.getType();

        //Convert message embedded in List<String> to Json String
        //String json = gson.toJson(list);

        //Convert Json String to back to List<String> which Message is embedded.
        //List<String> fromJson = gson.fromJson(json, type);

        return json;
    }
}

/**
 *      Format produced by gson.toJson
 *
 *      String json = "["1","BigFamily","abcd","0","efgh","0"]"
 *
 *
 *
 *
 *
 *
 *
 * **/