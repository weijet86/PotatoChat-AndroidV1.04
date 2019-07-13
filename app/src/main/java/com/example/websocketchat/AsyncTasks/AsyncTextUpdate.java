package com.example.websocketchat.AsyncTasks;


import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.websocketchat.Entities.Message;
import com.example.websocketchat.Util.ChatActivity;
import com.example.websocketchat.Util.ChatActivityFragment;
import com.example.websocketchat.Util.ChatFragmentMaster;
import com.example.websocketchat.Util.ClientUni;
import com.example.websocketchat.Util.MainActivity;
import com.example.websocketchat.Util.MainActivityServer;
import com.example.websocketchat.Util.R;
import com.example.websocketchat.Util.Server;
import com.example.websocketchat.Util.UserLIstView;
import com.example.websocketchat.Util.tempUsersListClass;
import com.example.websocketchat.Util.unreadMsgCountClass;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.handshake.ServerHandshake;
import org.java_websocket.server.WebSocketServer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import static android.content.Context.NOTIFICATION_SERVICE;

public class AsyncTextUpdate extends AsyncTask<Message, Message, Message>{
    private static final String TAG = "AsyncTextUpdate";
    private Context mContext;
    private static final int SERVER_PORT = 8080; //Server sends and Client receives so both on the same port
    private boolean isMine;
    public static List<Integer> unreadMsgCount=new ArrayList<>();
    public static int counter=0;
    public static String tempUserName=""; //tempUserName can only store one user, need a list<String> to store tempUsers
    public static List<String> tempUsersList=new ArrayList<>();
    public static List<unreadMsgCountClass> unreadMsgCountClassList=new ArrayList<>();
    public static List<tempUsersListClass> tempUsersListClassList=new ArrayList<>();

    Button NotifyBut;
    Button ResetUnreadMsgCount;


    public AsyncTextUpdate(Context context, boolean mine){
        mContext = context;
        isMine = mine;
    }
    public static void saveBitmapToStorage(Bitmap bitmap){
        String path = Environment.getExternalStorageDirectory().toString();
        File file = new File(path, "BlackSheep"+".jpg");
        try (FileOutputStream out = new FileOutputStream(file)) {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out); // bmp is your Bitmap instance
            // PNG is a lossless format, the compression factor (100) is ignored
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected Message doInBackground(Message[] msg) {
        Log.d(TAG, "doInBackground-server");

        //TODO-Save image/file to storage then set filePath/fileUri to message.
//        Bitmap tempBitmap=msg[0].byteArrayToBitmap(msg[0].getByteArray());
 //       saveBitmapToStorage(tempBitmap);
        publishProgress(msg);//update to onProgressUpdate


        return msg[0];

    }

    @Override
    protected void onProgressUpdate(Message... values) {
        super.onProgressUpdate(values);
        Log.d(TAG,"onProgressUpdate-server");

        //-When I receive message, it contains sender's name. Use sender's name to get user position in user list.
        String gotUserName=values[0].getChatName();
        Log.d(TAG,"got user name is "+gotUserName);
        int userPos=searchUserPos(gotUserName,ClientUni.userList);
        Log.d(TAG,"user position is "+userPos); /**User position becomes -1 on the 2nd time receive msg. There must be something wrong with searchUserPos function*/
        if(userPos>-1) {
            //notifyUser(gotUserName);
            notifyUserAlt(gotUserName);
        }

        //-Verify position matches userList
        for(int x = 0; x< ClientUni.userList.size(); x++) {
            //-If I altered username for notifications, I won't be able to find a match for chatname.
            //-I could use indexOf to find a match for chatname out of a modified string used for notification instead of looking for exact match for chatname.

                if(ClientUni.userList.get(x).indexOf(values[0].getChatName())>-1){ //-1 means modified username contains chatname at index position 0 or more.
                Log.d(TAG,"AsyncTextUpdate inside refreslist loop");
                //TODO-listfragmentSwap12
                //ChatFragmentMaster.listfragment.get(x).refreshList(values[0], false);
                //- "findCAFposInList" function doesn't have the ability to distinguish user with amended name for unread msg notification.
                int CAFposInList=ChatFragmentMaster.findCAFposInList(gotUserName);
                Log.d(TAG,"CAF position is "+String.valueOf(CAFposInList));
                ChatFragmentMaster.listCAFclass.get(CAFposInList).getCAF().refreshList(values[0],false);

                pushNotification(1); //Add notification msg to drop-down menu!

            }
        }



    }

    @Override
    protected void onPostExecute(Message result) {
//		Log.v(TAG, "onPostExecute");
        super.onPostExecute(result);
    }

    /**Function to add notification messages to drop-down menu!*/
    public void pushNotification(int clickInt){
        final int clickNotifyBut=1;
        final int clickResetUnreadMsgCount=2;


        NotifyBut = new Button(mContext);
        ResetUnreadMsgCount=new Button(mContext);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) { /**Only execute code when Android API is 26 or above*/


            // Sets an ID for the notification, so it can be updated.
            final int notifyID = 1;
            final String CHANNEL_ID = "my_channel_01";// The id of the channel.
            CharSequence name = mContext.getString(R.string.channel_name);// The user-visible name of the channel.
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
            // Create a notification and set the notification channel.
            final Notification notification = new Notification.Builder(mContext)
                    .setContentTitle("New Message")
                    .setContentText("You've received 1 new message.")
                    .setSmallIcon(R.drawable.ic_launcher_foreground)
                    .setChannelId(CHANNEL_ID)
                    .build();

            final NotificationManager mNotificationManager =
                    (NotificationManager) mContext.getSystemService(NOTIFICATION_SERVICE);
            mNotificationManager.createNotificationChannel(mChannel);

            //TODO-notification.contentIntent managed to start new CFM when clicked, but a new CFM without previous content!!
            Intent notificationIntent = new Intent(mContext, ChatFragmentMaster.class);
            //notificationIntent.putExtra("item_id", "1001"); // <-- HERE I PUT THE EXTRA VALUE
            PendingIntent contentIntent = PendingIntent.getActivity(mContext, 0, notificationIntent, 0);
            notification.contentIntent=contentIntent;

// Issue the notification.
            //mNotificationManager.notify(notifyID , notification);

            NotifyBut.setOnClickListener(new View.OnClickListener() { /**Click button to add unreadMsgCount to push notification message*/
            @Override
            public void onClick(View v) {
                ClientUni.UnreadMsgCount++;
                if (ClientUni.UnreadMsgCount == 1) {
                    mNotificationManager.notify(notifyID, notification);

                } else if (ClientUni.UnreadMsgCount > 1) {
                    Notification notification1 = new Notification.Builder(mContext)
                            .setContentTitle("New Messages")
                            .setContentText(String.format("You've received %S new messages.", ClientUni.UnreadMsgCount))
                            .setSmallIcon(R.drawable.ic_launcher_foreground)
                            .setChannelId(CHANNEL_ID)
                            .build();
                    mNotificationManager.notify(notifyID, notification1);

                }
            }
            });

            //TODO-click on notification msg to open ChatFragmentMaster



            ResetUnreadMsgCount.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ClientUni.UnreadMsgCount=0;
                    mNotificationManager.cancel(notifyID);
                }
            });


            if(clickInt==clickNotifyBut){
                NotifyBut.performClick();
            }else
                if(clickInt==clickResetUnreadMsgCount){
                    ResetUnreadMsgCount.performClick();
                }


        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) { /**If Android API is below 26.0*/


            Intent intent = new Intent(mContext, AsyncTextUpdate.class);
// use System.currentTimeMillis() to have a unique ID for the pending intent
            PendingIntent pIntent = PendingIntent.getActivity(mContext, (int) System.currentTimeMillis(), intent, 0);
            final Notification notification = new Notification.Builder(mContext)
                    .setContentTitle("New Message")
                    .setContentText("You've received 1 new message.")
                    .setSmallIcon(R.drawable.ic_launcher_foreground)
                    .setContentIntent(pIntent)
                    .setAutoCancel(true)
                    .build();

            NotificationManager notificationManager =
                    (NotificationManager) mContext.getSystemService(NOTIFICATION_SERVICE);

            notificationManager.notify(0, notification);
        }
    }

    //TODO-Build a function to search for user position in user list
    public static int searchUserPos(String username, List<String> userList){
        int userPos=-1; //If this functions returns -1 means username doesn't match any names on the user list

        for(int i=0;i<userList.size();i++){

            if(userList.get(i).indexOf(username)>-1){ //As long as userList string contains username, condition will be met.
                userPos=i;
            }

        }
        return userPos;
    }
    //TODO- Build alternative function of notifyUser independent of userPos but dependent username
    public static void notifyUserAlt(String gotUserName){
        //Search for position in unreadMsgCountClass to be updated
        int msgUnreadCountPos=searchPosunreadMsgCountClass(gotUserName,unreadMsgCountClassList);
        //Counter +1 for unreadMsgCountClassList
        unreadMsgCountClassList.get(msgUnreadCountPos).setUnreadMsgCount(unreadMsgCountClassList.get(msgUnreadCountPos).getUnreadMsgCount()+1);
        int counter=unreadMsgCountClassList.get(msgUnreadCountPos).getUnreadMsgCount();
        Log.d(TAG,"unreadMsgCountClassList counter is "+counter);

        String modifiedString=String.format(gotUserName+ " -got (%s) message!", counter);
        Log.d(TAG,"modifiedString is "+modifiedString);
        //ClientUni.userList.set(searchUserPos(gotUserName,ClientUni.userList),"");
        ClientUni.userList.set(searchUserPos(gotUserName,ClientUni.userList), modifiedString);
        UserLIstView.refreshBut.performClick();

        //TODO- unreadmsg count is only added to username once but not stored in a list.
        //TODO- When userlist is clear and reacquired from server, all the unreadmsgcount would be lost
    }

    //-Build a function to add notification message to username in list
    public static void notifyUser(String gotUserName){
        //Log.d(TAG, "user at userPos is " + ClientUni.userList.get(userPos));
        int userPos=searchUserPos(gotUserName,ClientUni.userList);

        updateUnreadCount(userPos);
        int counter=unreadMsgCount.get(userPos);
        //Log.d(TAG,"unreadMsgCount is "+counter);



        if(counter==1) {
            //-Use "tempUsersList" instead!
            storeTempUser(userPos);
            //tempUserName = ClientUni.userList.get(userPos); //Store username only on 1st message before clearing list
        }
        ClientUni.userList.set(userPos,""); //Set empty string to list to reset list at selected position
        ClientUni.userList.set(userPos, String.format(tempUsersList.get(userPos) + " -got (%s) message!", counter));
        UserLIstView.refreshBut.performClick();
    }
    //-Build a function to add back all notifications after new user refreshlist removed all notifications.
    public static void addBackNotifications(){
        for(int i=0;i<unreadMsgCountClassList.size();i++){
            if(unreadMsgCountClassList.get(i).getUnreadMsgCount()>0){
                //Username to add back notifications
                String username=unreadMsgCountClassList.get(i).getusername();
                //Search pos in Client.userList
                int userpos=searchUserPos(username,ClientUni.userList);
                //Add back notifications
                ClientUni.userList.set(userpos, String.format(ClientUni.userList.get(userpos) + " -got (%s) message!", unreadMsgCountClassList.get(i).getUnreadMsgCount()));

            }

        }
        /*
        //-All unreadMsgCount>0 need to add notifications
        for(int i=0;i<unreadMsgCount.size();i++){
         if(unreadMsgCount.get(i)>0){
             //Add back notificaitons
             ClientUni.userList.set(i, String.format(ClientUni.userList.get(i) + " -got (%s) message!", unreadMsgCount.get(i)));
         }
        }
        */
    }

    //-Build a function to remove notification message and revert username back to normal
    public static void revertUser(int userPos){
        //Get username from userPos
        String gotUserName=ClientUni.userList.get(userPos);
        //SubString username if got "-got"
        //I can't retrieve username without unreadmsgcount but I can't retrieve unreadmsgcount without username?!?!
        //That's not true because the userPos I click is definitely the user I select!
        if(gotUserName.indexOf("-got")>-1&&gotUserName.indexOf("message")>-1){
            gotUserName=ClientUni.userList.get(userPos).substring(0,gotUserName.indexOf("-got")-1);
            Log.d(TAG,"substring username is "+gotUserName);
        }

        int msgUnreadCountPos=searchPosunreadMsgCountClass(gotUserName,unreadMsgCountClassList);
        int counter=unreadMsgCountClassList.get(msgUnreadCountPos).getUnreadMsgCount();

        Log.d(TAG,"revertUser counter is "+counter);
        String targetString=String.format(" -got (%s) message!",counter);
        Log.d(TAG,"targetString is "+targetString);
        int lastUserStringPos=ClientUni.userList.get(userPos).indexOf(targetString); //Pick up the last position of username string
        Log.d(TAG,"Last position of username is "+lastUserStringPos);
        if(lastUserStringPos>-1) {
            String revisedString = ClientUni.userList.get(userPos).substring(0, lastUserStringPos);
            ClientUni.userList.set(userPos, revisedString);
            //unreadMsgCount.set(userPos,0);
            //- Before resetting UnreadMsgCount to zero for selected user, perhaps we could deduct ClientUni.unreadMsgCount accordingly.
            int noTodeduct=unreadMsgCountClassList.get(msgUnreadCountPos).getUnreadMsgCount();
            ClientUni.UnreadMsgCount=ClientUni.UnreadMsgCount-noTodeduct;
            unreadMsgCountClassList.get(msgUnreadCountPos).setUnreadMsgCount(0);

            UserLIstView.refreshBut.performClick();
        }
    }

    public static void updateUnreadCount(int userPos){
        int updatedCounter=unreadMsgCount.get(userPos)+1;
        unreadMsgCount.set(userPos,updatedCounter);
        //Log.d(TAG,"Unread message counter is "+unreadMsgCount.get(userPos));
    }

    public static void storeTempUser(int userPos){
        String storedString=ClientUni.userList.get(userPos);
        Log.d(TAG,"stored string is "+storedString);
        tempUsersList.set(userPos,storedString); //Got to .add before can .set!
    }

    public static int searchPosunreadMsgCountClass(String username,List<unreadMsgCountClass> unreadMsgCountClassList){
        int pos=-1;

        for(int i=0;i<unreadMsgCountClassList.size();i++){
            if(username.equals(unreadMsgCountClassList.get(i).getusername())){
                pos=i;
            }
        }


        return pos;
    }

    public static int searchPostempUsersListClass(String username,List<tempUsersListClass> tempUsersListClassList){
        int pos=-1;
        for(int i=0;i<tempUsersListClassList.size();i++){
            if(username.equals(tempUsersListClassList.get(i).getusername())){
                pos=i;
            }
        }


        return pos;
    }

}