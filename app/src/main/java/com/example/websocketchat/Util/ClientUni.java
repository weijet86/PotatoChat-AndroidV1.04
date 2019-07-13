package com.example.websocketchat.Util;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.websocketchat.AsyncTasks.AsyncTextUpdate;
import com.example.websocketchat.AsyncTasks.AsyncUIelements;
import com.example.websocketchat.Entities.JsonToMsg;
import com.example.websocketchat.Entities.Message;
import com.example.websocketchat.Entities.MsgToJson;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Timer;

import static android.content.Context.ACTIVITY_SERVICE;


public class ClientUni extends Thread {
    private static final String TAG = "ClientUni";
    Context context;
    //Message recMsg;
    public static WebsocketClient WSC;
    public static List<String> userList=new ArrayList<String>();
    public static URI uri;
    public static int UnreadMsgCount=0;

    public static final int alphabeticalOrder=1;
    public static final int reverseAlphaOrder=2;
    public static final int shortNameToLong=3;
    public static final int longNameToShort=4;




    public ClientUni(final Context context){
        this.context=context;

        final Handler handler=new Handler();

        /**5seconds delay to start ChatActivity**/
        /**Any app which calls for ServerUni or ClientUni will start ChatActivity if I put startCA here**/
        /*
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() { //ChatActivity only opens once when websocket is connected.
            @Override
            public void run() {
                // Do something after 5s = 5000ms
                startCA(context); //CA won't start inside onOpen.
            }
        }, 5000);

        */


        //TODO- start websocket client
        try {
            uri=new URI("ws://10.0.2.2:8080");
            WSC=new WebsocketClient(uri){
                //onOpen
                @Override
                public void onOpen(ServerHandshake arg1) {
                    //Log.d(TAG,"websocketclient onOpen");

                    //Message mes = new Message(Message.TEXT_MESSAGE, null, null, 0,null,"potatoMan","potatoMan");
                    //WSC.send(new MsgToJson().MsgToJson(mes));
                    handler.postDelayed(new Runnable() { //ChatActivity only opens once when websocket is connected.
                        @Override
                        public void run() {
                            // Do something after 5s = 5000ms
                            //startCFM(context); //CA won't start inside onOpen.
                            startuserLog(context);
                        }
                    }, 0);


                }
                //onMessage
                @Override
                public void onMessage(String text){

                    //Log.d(TAG,"websocketclient onMessage");
                    //- There is error for node js server sending msg back(should be format issue)
                    Message recMsg=new JsonToMsg().JsonToMsg(text);

                    //Log.d(TAG,Integer.toString(recMsg.getmType()));
                    if(recMsg.getmType()==1) {

                        Log.d(TAG,"checkSender is "+checkSender(recMsg));
                        //-Every time app receives message, check if sender is currently on userlist?

                        if(checkSender(recMsg)==1){
                            updateReceivedTextMsg(context,recMsg);
                        }else
                        if(checkSender(recMsg)==2) {
                            new AsyncUIelements(context,recMsg).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,1);
                        }

                    }else
                    if(recMsg.getmType()==2){
                        //ChatActivity.refreshList(recMsg,false);
                        //new AsyncTextUpdate(context, false).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, recMsg);

                    }else
                    if(recMsg.getmType()==9){
                        //-Only revise userList when there is a new user
                        if(recMsg.getUserList().size()>userList.size()) {
                            userList.clear();
                            int netDif=recMsg.getUserList().size()-userList.size();

                            //TODO-Try to get the userList sent by server
                            for (int x = 0; x < netDif; x++) {
                                //Log.d(TAG, recMsg.getUserList().get(recMsg.getUserList().size()-netDif+x));
                                userList.add(recMsg.getUserList().get(recMsg.getUserList().size()-netDif+x));
                                String username=recMsg.getUserList().get(recMsg.getUserList().size()-netDif+x);
                                //Log.d(TAG, userList.get(recMsg.getUserList().size()-netDif+x));
                                AsyncTextUpdate.unreadMsgCount.add(0);
                                AsyncTextUpdate.tempUsersList.add(""); //Add empty String to initialise

                                AsyncTextUpdate.unreadMsgCountClassList.add(new unreadMsgCountClass(username,0));
                                AsyncTextUpdate.tempUsersListClassList.add(new tempUsersListClass(username,""));
                            }
                            //TODO-notifications will be hidden if there is a new user added to user list.
                            //TODO-Add back all notifications!!
                            AsyncTextUpdate.addBackNotifications();

                        }

                    }else
                    if(recMsg.getmType()==11) {

                      Log.d(TAG,"Received type 11 message from server!");
                      Log.d(TAG,"Type 11 message says "+recMsg.getmText());

                      switch(recMsg.getmText()){
                          case "You have signed in successful!":
                              Log.d(TAG,"You have signed in successfully!");
                              userLogin.responseMsg.setText("You have signed in successfully!");
                              //TODO-start chat fragment master
                              startCFM(context);

                              break;
                          case "Wrong username/password!Please try again!":
                              Log.d(TAG,"Wrong username/password!Please try again!");
                              userLogin.responseMsg.setText("Wrong username/password!Please try again!");
                              break;
                          case "Username doens't exist!Please try again!":
                              Log.d(TAG,"Username doens't exist!Please try again!");
                              userLogin.responseMsg.setText("Username doens't exist!Please try again!");
                              break;
                      }
                    }else
                        if(recMsg.getmType()==12){
                            Log.d(TAG, "Received type 12 message from server!");
                            if(recMsg.getmText().equals("Your new signup is successful!")){
                                userLogin.loginstatus=userLogin.successfulsignup;
                                startuserLog(context);
                            }else
                                if(recMsg.getmText().equals("Username already exists!Please choose another name!")){

                            }
                        }


                    else
                    if(recMsg.getmType()==14) { //Type 14 msg is sent by server to broadcast to all online users of logged-out users
                        Log.d(TAG,"Type 14 msg has been received!");
                        Log.d(TAG,recMsg.getmText()+" has logged out!");
                        //TODO remove logged-out user from userlist and remove its respective ChatActivityFragment
                        for(int i=0;i<ClientUni.userList.size();i++){
                            if(ClientUni.userList.get(i).equals(recMsg.getmText())){
                                //TODO-listfragmentSwap10
                                //ChatFragmentMaster.listfragment.remove(i);
                                /**Search for CAF position in listCAFclass for removal*/
                                int removeCAFpos=ChatFragmentMaster.findCAFposInList(ClientUni.userList.get(i));
                                ChatFragmentMaster.listCAFclass.remove(removeCAFpos);


                                ClientUni.userList.remove(i);
                                Log.d(TAG,"ClientUni.userList is"+ClientUni.userList.get(0));
                                new AsyncUIelements(context,recMsg).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,2);
                            }
                        }

                    }
                }

            };
            WSC.connect(); //Connect websocket to server
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }



    }
    /**Check if sender's name is in user list*/
    public static int checkSender(Message recMsg) {
        //Log.d(TAG,"Sender's name is "+recMsg.getChatName());
        for(int i=0;i < userList.size();i++) {
            if ( userList.get(i).indexOf(recMsg.getChatName())>-1 ) {
                Log.d(TAG,"username is "+userList.get(i));
                return 1;

            }else
                if(i==userList.size()-1){
                    return 2;
                }
        }
        return 3;
    }
    /**UpdateReceivedTextMsg*/
    public void updateReceivedTextMsg(Context context,Message recMsg) {
        if(getComponentInfo(context).equals("com.example.websocketchat.Util.ChatFragmentMaster")) {
            //Log.d(TAG, "I'm currently in ChatFragmentMaster");
            new AsyncTextUpdate(context, false).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, recMsg);

        }
    }

    /**Start ChatFragmentMaster*/
    public static void startCFM(Context context){
        //Log.d(TAG,"start ChatFragmentMaster");
        Intent intent=new Intent(context,ChatFragmentMaster.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    /**Start userLogin*/
    public static void startuserLog(Context context){
        Log.d(TAG,"start user login");
        Intent intent=new Intent(context,userLogin.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);


    }

    /**Get current activity's name*/
    public String getComponentInfo(Context context){
        //Log.d(TAG,"getComponentInfo");
        ActivityManager am = (ActivityManager) context.getApplicationContext().getSystemService(ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
        //Log.d(TAG, "CURRENT Activity ::" + taskInfo.get(0).topActivity.getClassName());
        return taskInfo.get(0).topActivity.getClassName();

    }

    /**Sort userList*/
    public static List<String> sortUserList(List<String> userList,int sortType){
        //sort type 1= alphabetical order
        //sort type 2= reverse alphabetical order
        //sort type 3= from shortest name to longest
        //sort type 4= from longest name to shortest
        Comparator<String> ALPHABETICAL_ORDER = new Comparator<String>() {
            public int compare(String str1, String str2) {
                int res = String.CASE_INSENSITIVE_ORDER.compare(str1, str2);
                if (res == 0) {
                    res = str1.compareTo(str2);
                }
                return res;
            }
        };

        //This Android client already has the capability to pick the right chat fragment based purely on chatname instead of listview position
        switch(sortType){
            case alphabeticalOrder:
                Collections.sort(userList,ALPHABETICAL_ORDER);
                break;
            case reverseAlphaOrder:
                Collections.sort(userList,ALPHABETICAL_ORDER);
                Collections.sort(userList,Collections.reverseOrder());
                break;
            case shortNameToLong:

                break;
            case longNameToShort:

                break;
        }

        return userList;
    }


}
