package com.example.websocketchat.Util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;

import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupMenu;


import com.example.websocketchat.AsyncTasks.AsyncTextUpdate;
import com.example.websocketchat.CustomAdapters.ChatAdapter;
import com.example.websocketchat.Entities.FileUtils;
import com.example.websocketchat.Entities.JsonToMsg;
import com.example.websocketchat.Entities.Message;
import com.example.websocketchat.Entities.MsgToJson;
import com.example.websocketchat.Entities.Image;
import com.example.websocketchat.Entities.RealPathUtil;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


//Linking Classes from different packages


public class ChatActivity extends AppCompatActivity {
    //Variables for sending file types other than text (shall be reserved for later)
    private static final String TAG = "ChatActivity";
    private static final int PICK_IMAGE = 1;
    private static final int TAKE_PHOTO = 2;
    //private static final int RECORD_AUDIO = 3;
    //private static final int RECORD_VIDEO = 4;
    private static final int CHOOSE_FILE = 5;
    //private static final int DRAWING = 6;
    //private static final int DOWNLOAD_IMAGE = 100;
    //private static final int DELETE_MESSAGE = 101;
    //private static final int DOWNLOAD_FILE = 102;
    //private static final int COPY_TEXT = 103;
    //private static final int SHARE_TEXT = 104;
    //private static final int REQUEST_PERMISSIONS_REQUIRED = 7;

    //private IntentFilter mIntentFilter;
    private EditText edit;
    public static ListView listView;
    public static List<Message> listMessage;
    private static ChatAdapter chatAdapter;

    //Variables for enter Chat Name
    private EditText EnterChatName;
    private Button SetChatName;
    public String ChatName;
    private Message CAmes;
    private int setChatCount=0;

    ServerUni serveruni;
    ClientUni clientuni;

    public Button button;
    //Variables for sending file types other than text (shall be reserved for later)
    private Uri fileUri;
    //private String fileURL;
    private ArrayList<Uri> tmpFilesUri;
    private Uri mPhotoUri;

    String encodedImgString;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        Log.d(TAG,"ChatActivity is started!");


    //Initialize the adapter for the chat
        listView = (ListView) findViewById(R.id.messageList);
        listMessage = new ArrayList<Message>();
        //ChatAdapter pending to be built!
        chatAdapter = new ChatAdapter(this, listMessage); //Requires new ChatAdapter class
        listView.setAdapter(chatAdapter);



        CAmes = new Message(Message.TEXT_MESSAGE,"ChatAdapter working!!", null, 0,null,null,"SuperMan");
        CAmes.setChatName("I love chitChatting");
        //TODO (done0- let's test out functionality of JsonToMsg class(works for Text Message)
        String testJson=new MsgToJson().MsgToJson(CAmes);
        CAmes.setmText(testJson);


        ChatActivity.refreshList(CAmes,true);
        ChatActivity.refreshList(CAmes,true);

     //Hide keyboard when listview is clicked,swipe up,swipe down,!
        //TODO can't scroll listview when setontouchlistener is used.
        /*
        listView.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {

                    case MotionEvent.ACTION_UP: {
                        //Swipe up to perform action

                        break;
                    }
                    case MotionEvent.ACTION_DOWN: {
                        //Swipe down to perform action

                        break;
                    }
                    case MotionEvent.ACTION_BUTTON_PRESS: {
                        //Press button to perform action
                        //hideKeyboard(ChatActivity.this);
                        break;
                    }
                    case MotionEvent.ACTION_BUTTON_RELEASE: {
                        //Release button to perform action
                        //hideKeyboard(ChatActivity.this);
                        break;
                    }

                }return true;
            }
        });*/
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,long id) {
                hideKeyboard(ChatActivity.this);
            }
        });
    //Enter Chat Name
        EnterChatName=findViewById(R.id.EnterChatName);
        SetChatName=findViewById(R.id.ButtonChatName);
        final ViewGroup.LayoutParams params2 = EnterChatName.getLayoutParams();

    //SetChatName
        SetChatName.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) { //Click SetChatName button to set name and hide editText
                setChatCount++;
                ChatName = EnterChatName.getText().toString();

                if(setChatCount==1) {
                    CAmes.setChatName(ChatName);

                    //Change editText's height in chatNameZone to 0
                    //EditText et= findViewById(R.id.EnterChatName);
                    params2.height = 0;
                    EnterChatName.setLayoutParams(params2);
                    isKeyBoardShow(ChatActivity.this);

                }else
                    if(setChatCount==2){ //Click SetChatName button to reveal editText
                        EnterChatName.setText("");
                        //Change editText's height in chatNameZone to 0
                        params2.height = 130;
                        EnterChatName.setLayoutParams(params2);
                        setChatCount=0;

                        EnterChatName.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                EnterChatName.requestFocus();
                                EnterChatName.setFocusableInTouchMode(true);
                                InputMethodManager lManager = (InputMethodManager)ChatActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
                                lManager.showSoftInput(EnterChatName, 0);
                            }
                        });
                        EnterChatName.performClick();

                    }
            }
        });


    //Send message
        //TODO (Done) 1) Create a class to identify each client's dstAddress,port, and chat name when one connects to server socket.
        //TODO (Done) 2) Create a client identity class to store all the above info.
        //TODO 3) Modify all existing send messages functions and classes to include sender/receiver identity instead of server-client.
        //TODO 4) Create a switch class to divert messages between sender and receiver.
        //TODO 5) Server will stop sending messages by its own but receive/divert messages only.
        button = (Button) findViewById(R.id.sendMessage);
        edit = (EditText) findViewById(R.id.editMessage);



        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if(!edit.getText().toString().equals("")){
					Log.d(TAG, "Send message");
					sendMessage(Message.TEXT_MESSAGE);

                    /** Convert recMsg to Json String before send it out **/
					/*
                    String typedString=edit.getText().toString();
                    Message recMsg = new Message(Message.TEXT_MESSAGE, typedString, null, 0, null, 0,ChatName);

                    String jsonString=new MsgToJson().MsgToJson(recMsg);
                    serveruni.ws.send(jsonString);
                    edit.setText("");

                    ChatActivity.refreshList(recMsg, true);
                    */
                }
            }
        });

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.d(TAG,"onCreateOptiosnMenu");
        getMenuInflater().inflate(R.menu.chat, menu);
        return true;
    }

    public void isKeyBoardShow(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        if (imm.isActive()) {
            imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0); // hide
        } else {
            imm.toggleSoftInput(0, InputMethodManager.SHOW_IMPLICIT); // show
        }
    }
    //Function to open keyboard when listview is clicked!
    public static void openKeyboard( Context context ) {

        try {
            InputMethodManager inputManager = ( InputMethodManager ) context.getSystemService( Context.INPUT_METHOD_SERVICE );

            View view = ( ( Activity ) context ).getCurrentFocus();
            if ( view != null ) {
                inputManager.showSoftInput(view,InputMethodManager.HIDE_NOT_ALWAYS);
            }
        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }
    //Function to hide keyboard when listview is clicked!
    public static void hideKeyboard( Context context ) {

        try {
            InputMethodManager inputManager = ( InputMethodManager ) context.getSystemService( Context.INPUT_METHOD_SERVICE );

            View view = ( ( Activity ) context ).getCurrentFocus();
            if ( view != null ) {
                inputManager.hideSoftInputFromWindow( view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS );
            }
        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }
    public void talkTo(String destination){
        edit.setText("@" + destination + " : ");
        edit.setSelection(edit.getText().length());
    }
    public void sendMessage(int type){
    //Build sendMessage function
        Log.d(TAG,"sendMessage");
        Message mes = new Message(type, null, null, 0,null,null,"SuperMan");
        Message mes1 = new Message(type, null, null, 0,null,null,"SuperMan");
        mes.setRecipientChatName(mes.getChatName()); //Set recipientChatName the same as chatName to have receive echo message.
        switch(type){
            case Message.TEXT_MESSAGE:
                mes.setmText(edit.getText().toString());
                edit.setText("");
                break;
            case Message.IMAGE_MESSAGE:
                Log.d(TAG,"sendMessage-Message.Image.message");

                Image image = new Image(this, fileUri);
                Log.e(TAG, "Bitmap from url ok" + fileUri);

                String filePath=fileUri.getPath();
                //TODO- Finally found function FileUtils to be working perfectly!
                String realPath= FileUtils.getRealPath(this,fileUri);
                //String realPath=getRealPathFromURI(this,fileUri);
                if(realPath==null){Log.d(TAG,"realPath is a null.");}else
                {
                Log.d(TAG,"real path is "+realPath);}

                mes.setFileUri(fileUri);
                mes.setFilePath(filePath);
                //TODO (done)- image is converted to Bitmap then to byteArray.
                mes.setByteArray(image.bitmapToByteArray(image.getBitmapFromUri()));
                /**Convert image bitmap to base64 string and from base64 back to bitmap*/
                //TODO (done)- try to convert image straight to base64 string.
                encodedImgString=image.getBase64FromUri(image.getBitmapFromUri());
                mes.setBase64ImageString(encodedImgString);
                //encoded base64 string successfully decoded back to bitmap image and set to listview.
                //Bitmap decodedImg=mes1.base64StringToBitmap(encodedImgString);
                //mes1.setByteArray(image.bitmapToByteArray(decodedImg));
                //TODO (done) - try to put a raw base64 image string to listview (Proven that base64 image by Android and by JS are the same!!)
                String rawBase64String="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";
                String sadPotatoString="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";
                String fatBurger="/9j/4AAQSkZJRgABAQAAAQABAAD/2wCEAAkGBxISEhUSEhMWFhUXGBcYGBUXFRUXGBcYGBUWFxUYGBgYHSggGB0lGxUYITEiJSkrLi4uGB8zODMsNygtLisBCgoKDg0OGxAQGy4lICYyLTAwLy83LTUtMC8tLS0tLS0tLzctLS0tLS0tLS4tLS0vLS0tLS0tLS0tLS0tLS0tLf/AABEIAMIBAwMBIgACEQEDEQH/xAAcAAABBQEBAQAAAAAAAAAAAAAAAQMEBQYCBwj/xAA6EAABAwIEAwYEBQQCAgMAAAABAAIRAyEEEjFBBVFhBhMicYGRMqGx8AdCUsHRFCNi4XLxFqIzQ4L/xAAaAQACAwEBAAAAAAAAAAAAAAAAAwECBAUG/8QALxEAAgIBAwIEBQQCAwAAAAAAAAECAxEEITESQRMiUfAyYYGhsRSR0fEFwSNx4f/aAAwDAQACEQMRAD8A9xQhCABCEIAEIQgAQhCABCEIAELlzwNVFr40izWk9dAqymo8llFvgmIVdW4gBaQPUJKuLtIcCOhS/HgW8KRZIVVhqziA4uBtG9zzSDEVWkzdsSCIUeOsZwT4TLZCp6XFXEwGT63/ANJGcUqZsrgGnck2joFH6qv1J8CZcoVIOLucctOCb62BA5XTz+MZSGubBMdAPPkpWorfch0z9C1QoNfibGRm30i6eoY6m8BwcIPO31TFOLeMlHCSWcEhCSUquVBCEIAEIQgAQhCABCEIAEIQgAQhCABCEIAEIQgAQkUbFY5lP4jfkNVWUlFZbJSbeESiVV43i2USxpcBq7Rvod1C4jxQublAyg+56KoqF9SGSTsBK5uo16T6azbTpM7zJOLx1csDyQ1pNiIneyXhmMc6Wl8u1HM8wLiFCeHmKZI8EiCWgCLGSo1KkDUh1TJ/mASJ6R9Vz/Gn1p7/AFNypi4tbfQscdNMipTu5vxNDPhtMv2/7UnhnE21MwccroB8RaBJ1yjlbzuqPH4v8jDAFi8T/csLmfWyj/0oYGVHjMx8+EO8U3ifZSr5KeY8d/QnwE4ebnt6lvxHHNaWjMXuNntDjl0tEb6WEpcPjKghoJGb8tRsNAA/UdfJUPeOp1RUDAGgy1jgYjzOvmp3EWYhzm1u7MWygHvBpOl+SFY22/wS6UsL8mgweNaGuL3NBkgGCGkcgSIO+i4rUc9jZxEgdOh0Kz+PGIfTpufBa74WN10JktHSb7SinxquMpMQObQJHK23kryti9pp4FqiXMWslgMA8ODh4YMy74TG3XyXOIx9J73Oe14tALTy8xoomF4sAKneaTmayHGJ1DXT4f8AakgUnjM1wI56Ef8AIG4VM7eT7l3F586/YXiOMbUNNtAONjLYMg20v5+yiU8U+m+7RafC79wlfhgJe14hpjMD9F0xlN9Oo99SXtywS7byOqhTlJ57lumEVjt9y3wLz3ZP9xrwM0CYLT+mbKZguMvDATFS4BAs4E2AvYqgwxquALHucGCAWzYHy8t+Ser98aRaIa1hh0Rc2IJ66LVVqJLjJmsoi3vg2GGxzH9DplOoKlLCnHszNd3f5Ydf836gRutJguJHwAiQ7RwM9YK6FOpU9mYrdO4botkLkOnRdLUZgQhCABCEIAEIQgAQhCABCEIARCFDx9cjwjU78pVZzUVlloxcnhDWO4hHhbrueSp6rM0HOCZuDcxN1zVBBIOotZJRYAZOsGDtqJ9Vw775WS839HSrrUFsM4gXJUe4Mi3XdSaqjuasTRrhwNSA4O1gzB38+a5IFRznOOWQXacgIaAF2Wymn2QX/J1hsMxvirNflMQNJPKf4T+DwzXF9UO7trDma0awBJhxNzY+6bq12PGao95fpEAx5Ta6SvxIPohjmeISR4gGg3gw2xgHRMj0Lnj36FJdb9/yJi6rMQ4ZqxbTbEZ9SSDcNaAPdQHNfTdYubc5HEFhI5gbWKmODmUbtEVIh9iY1hvKVCxWKL8pc4nLMSfQ+aibzzyMrXZcE+h3xDauamwNcRnholzhcOtoSfcgqRxvCF7AfG97PzNyhoDnTLgNgAmHVsKMo8ZaGFzgM3iqGA31Azctl1R8NA1qBe06PAGa41HhG06n5JyXbn6iXnKklj02IvFOB901rg8vzRADHGZ5FsjS/oomDwLgO8ObJJa4t+LqIXfDuIOpOZc5QRvNjrr0U7F0Ac76NQ91UnOILi0k+Lwm5O/NUShLeK+g1ynHyyf1/wBE5zjVpCmykchgSCXZdDIBi+hvuusfjn0qrabyx1h4nMILR1jVRK1AsYAHOLSBMtLZsIkHTyVfinUzTJIcasgTIylnWeV1Lsksp8+9hUaoy37fz3OR3QqPIrVGlxkdy0tZfUQ4yb+icp48MpubVa7vMwsQGkt2PnvooL8G9rRUb8Mgggixm2hsZ5qeDiKtBxc4Zf8AJoLnGdGmJJm3nZEZN8odOMfXb38h3DvpVB4XgP8A0EEHyGxKedUeS1rnGAY8ufmoXDuDZu876WBjQTaTJ0kaxCXCVizL3ziaTmy1w8RB2E6jy2TI5Xy99xUkm3h599jTcMxXdPDTUDmO3B0PXktIsRg8X4SWAOZNiRofvZajg+M7xmkEWhdTS2p+U5mpra8xYISJVsMgIQhAAhCEACEIQAJEIQAFU+OY4y6PvRW7lU4ht1j1nw4H0bSKstQ5pF+n7hSalMFxIsJsOlktSjaVyHW9zf1kGJXLmqXTpT0AuSdgoov9+yr0bZGKWSM9c0g0uGf4d4TlURKZlU7jlujtuBD6pZTMtABzHyv7FccMxoYScoc11iCOUiykuxAFMhjYeRBcHH1NrnyUA0w0Q0QByVniLTjyQk5JqXHvc643ie8imweBhGUwZMNLT0i/JVDaRJAAJJ0AurnBMo+I1nQBo0fE4nlvso2CxRpHPlBMWBOl+m6JLqfU+42D6U4xXBVubFuWqs+z2OyVMrnuYxwMkRrFiZBjzUalhXuzEAuuXEhuhcSTppdWWJpMfhqf91jGscC8EDNJOUmZuBm0i/yU1xaeV2Jtmmul9yobka8geJjHwJnxNa7n1A+alYus6vWzUmZTYNaI2HomeJik14bScXCLvOkzoLck9wmtlqAw06gh2l/oo79L4CXHWucdy3wlWpWp06VYZHTLyRfNfLI2H+lW8SwxpvNN2ojTSDoVOqYxr3AeLOJmSCC0GIB1cQSPrKhYptydz7+6m5p/P5/L0FU5T9F6CcMoscH94x0CHd40SWR9/JOYjjj35S0ZXXzxo47EAg5T97JKTXnDVIrMa0yXNmHkRBFxuOWqqKdlKbjFIv0Kcm32NI2symKTnOdmfrUcCJbBMO1DhMDfVZ/GYZzDlcCGmXNOxadCPRT8bi2PpUWS8uZYmBAB1jmQI9lxxB7CQ2mXFgFs067xOmyvJpopWnF/v/4ReH4h1Mm9jqNiNvZbXs2+XGNC39x/JWIDYWu7GO+IcgI8if8AXzT9I82JCdbFdDZqkIQu0cUVCRKgAQhCABIhCABCEIAQqHXpqWuKrZSrYdccFovDKp48RA0gGevJI+lN1Ke1chuvkuY692mausq8QRJA0EQfQH5FR3FSK7LqG6m6dYHPfyH8rHJvJthjAmIZMa/JRskTPzCm1dVErvCq8DoeglM9JUiue9c2mxrWCdTBJgaTsoheEzUrhSpYLOGXkdx+ENN2VxGkiDqFDEGYOhj5D+UxRxeZ7gAIDZJ6k2/ddipb7jr81DazsMUZJYZIo4x9OQx5bOsR6ajqq94uZMySSTcyTKteFYNtTO+o7LTYLm3Xc+SpP6gOaSJ3idTex9lLTwskwx1PHPcVgkSlptIKd4Q6hnJxDiGNEhozS4yLeG6KT2uzEAhuZ2UG5Ddp9FPSsFnLdrBNZSaMlTMDctI/TOmnMtHurGjgO9J8bWx+rfyVFVNmf82/UH9lb4gAjSVTMe6EyT7MrMQ0NcWy10HVpkabHdRi1P1KfRc5ZUZXYdnCJVPCNNLOHeIGC3kDoQuRStJB1iNd4nyVq3BUqQYHkl1QCAB8JcRf75KP3cHX7mxTJQawZlbnOCC/DuBgiPMLR9kWEPcf8f3CrntdUdJuea03AcJkbmOp+i16OtuzK4Rl1Vv/AB4fJbJVylXZOUKlSIQAqEIQAiEIQAJClSIAAghKAlQBmu0GPqUKjXNGZpF284dDoPOHBTMLxCnUbmYc2oMXgjYgaFQO3nBquIpNdSqZHUnd4WkS17QPE0wCRbSN15lwHiDqdQvp1C0ifCYDjGzmmcw8lydTOVdjfK/B0aK42wxw19z1aqJAMXOx1HmodZhCgDtZSNPO5pD2kB1ODJsLtja+6sW8QouhjXtLnAObJAJkSLeRFtVmahP4WXSnDlEJ2sbqJVF1a16G2+v+1CxGFOoWedUl2NVdkSuqOhVuPrFolWpwbpUTHYHMWCLZhI5iZKok+5pjOORjBUDTpEn4qhDiOTY8I+p9Vy+pCsMXTJJUCrhjZS2SpJ7sYqVswymYkEtm0jQkbpmpWspJw8bJh+FMX9kZGJxGGuU3CApxmBgAdB7qZh6ACFkXZbHB0/DZqbgNYkdCLhSsK7NRY7cgH5J7DUZtMaj1S0qJptDHbAR1G30TVW8GN2orqlErqnhpU8UxukeWg9bW6lWjR6kSv9AbhpgmTERrtp9FJeBItfn7JvDudcxIIsqviXaGlRJayKlSYIB8LTvmP7CT5JyjFLLEdUpPCJvEMZ3bYbAe4PLecNE6LXcHxgrUWVG6OaD/ACPQyF5TiOI1alRtR1nsjKGgxztJ33XpnZJ4NAEAAEkwNBJkx0mT6rXo7U5uKFaqrpgmy3SocxAauiYBQlXK6CABCVCAEQhCAEKRKUhQArU1iqhAsnGlcYlwhLtz0PBK5Mx2w487DYcvmHOcGN6F2p9ACV5RSrljagGaXxAaASXAkC1yBZ1xe/qr38SeIGtUNNt20p9/zH9vRY3B56hkEtgBpIAB31cLi5dryXOrbfxHXjp/DpU+7f8ARdYPjo8JqOJIaBJItoJcNeYtoIW2q8LbWYyoyC2NS4eHf4rfYXlnaBjQMrJqVI8LQ0yY+Jsak6m3VT+A499Atoue8tqOAMxDHxewMgTLfIHS6z36OLi5pe/kWVryomxr8Xfh5ipnJv48zoPQgj205KHR7fAf/Jh3TzpuBHQgOIj3Kr+JYdzzHzN4tsmf6ZsAR5rFVf0x3eTTKhPsbh3HcP3NOu54YyoYGYgQb+F1/CbFPsxTKgmnUa4f4lrh9fuVgMXwwO1H31VBi8L3RJpuLXaS1xb6SForthMQ6Wj2EsmbfW4IsmnsAm3svKOAYrGOr0qNPEVGgmBLy5rQdbGRstZiuJ4yiCxvduLJzF7XSbgnQjkNk6VaW4vzZwaSpQibT7ff/a5DG8jPp7ffNZil+IDBatSfN4NLK8CRNw4i8xzUh3bfAFmYveHXblNOoHXGoDZAFtZUeA+yJc5Lkv8AvG2sb+VkoxDRa08jr1VX/wCTYMtDhXYMxgSHAtNrlpbIb18+SZqdosA58iuQ4AsDhTeQCZGecsHTUWuFCg12Izk0uDaXtJDhImW+ij1eIPcWiJgEA7lo1PXSyx/F+2DGNy4YOquMgufTLG6QSIIdyOmxWYpcf4gHBwruB8LbMpxYACxaRffzTFHbnBXpeeD12nRfU8WYhsdPOTKU1KNKBUqtzTIaCMxEmDE/NeVmnWdUH9Y59UCwDnkBkC0Rb2ASVe1DWNczD02QJgBwG0kkC5sNeilVNvbcq9uWaPj3aWu9z6VM93TFrDxnSZdt6c9VQ/19MNzOgEnyvvos/Q440mp3jnOcfgsIvrMXm4HRQHBznmTeSZBnQFsAz5D1T/0udpMhXKPwmwr8daT/AGyc2WCYMTMeljHovafw8pkYGnO8n0JsvA+H4IvdTpnRxGYbmXQBbbX3XuvB6z6QABlvIq9MIVzyhV9k5QwzWITWHrh4kJwlb8mEQtXSEIAEJEIAEIQgBCkSlIgDh74USoealPpymK9IxZZ7urGwyDSPH+1uGFOrVbqC4n0df5SsjQxXdkNO7ukGRI18vkvTPxJwGXJWizxkPR4uJ8x9F59X4f3rIkNIFncultlghs8SPRwfiabb20NcXqFwa4NBd+WCLXiIjSICq61cABzvG4uF7mbgz1uB8tVzxFrqcMqgggGY3BtmDhsYskqYzvBQLhAEtGwgAEybRBNtee61pehy55i3ksuE9pzm7msdwG1DqZHha4coHxeUrc9muB9/UzPtTaRIm7iTYeXVectrNyOLB45icrBY+G521jTndbbg9V9I0xP9sxDgdw0HLO1yPsrnanTRjLxFH/tGmm6UouGd+zNLx7g72lxpsJbybcj01Xn+NwT3uMDTb+ZXsr8VTyGrmGXc315Eag9Fnu0PFML3RMh7yCGwIcDzkiyU6K68yrmvXH8E13Tl5ZRz77nl9XCOpEHMARcRYg7GQtXwxtTE0XOccxblaXbukE+M9MsZtfFvCzeJyl/9wkA8o9FK4LxZ2He5jDZ4LXGNYgtMHQgg+6jeUNzROC7ciP4KGEk3eZJcBYE8h7J6n2ekAlgBIzX3B0PrzVq/GDSyl8S4+6q2nTjLlABjciw+UWWWN1kk+p4f5LzjxhGddwdo1akbwwcloa1Elv3dVPE8Y2jTLnm33AAVYXSm8Lko4JbsGYfCYdpq1jmMfDeAddj5KiPaemJ/stBcfDBBy/p6ys9xTiheCcvgOgJk6gSY0Oo9VWYeHOgiBzJ+p2813qtMunM+TmTufViLLrjHHe88D5aTq4XvfXTNdVmGqSJnXpyF/S/yTr2U3OjaTGhPnpzhPvw1Jojn8WwsPPdaYqMVhCW5N5ZDGHhwiOZ5W1M8ohWbS1sNcZy6RI5GfdQqkuMNnLG1hvv7aK/7PdnauKdp4RALtgOQ5lROWC0a5Yzjb1L3sVR7yp37h4GQGwDd0ft+4XomD4iBrPsUvC8EyjSawMDQ0bfM33UmqGRI1WG5zj5osXOXUWfBsZLx1WiWNwtQghw9lrMJiA9ocFo0N7nFxk9xE0OhKhC3lAQhCAEQlSIACuV0kQByU2+onCE0WctEuxyx5S0cFRxzANxNF9F2jtD+k7EeS8sxvDXUagpObBG3McxzBXrlWuc0RAUPjPDKeJYA9txdrhqPL+Fym05bco62k1Lp2l8L+xlKnBsPiaAbWYDAs64c235TtpppzWC4/wBgqtKm91IsfSN4dAe3qAbT1aZ6L03F8Oq02mWkiBBYJNgZzM1vbSVie03F3Pb3bSQBqNPdaZWxS+Zt09crniLzH3+x5pjG1hNoJMkhoF/Dpa3wjRT8F2jqMaGPBME32Mi5IEdNE/iKZUYUpMc+ajxVJeYfZ/jFDeDwa3gva5j2mlVdBmzhJtycNfVTK9IPGZhDm82mR8lhMTgWg6A9Qm8LXqUXh9NzmkdZBHIg2IWKzRVyfVDZ/YiMLYLfdGuqYNv5zMbKHVY4PzN1Bn13/hWHAMZ/VB2ZoD2RMTBB0InS4NlYuwMO01WSdvhPpfJZQ6t2VIGYh5MTEzz9FIrY2m23eMJFoBzGfIXVJ2nx7hUNGmYa344sXP3EjYWEc56LOh5BlsgjktlejVsVOW3yMdmodbcYmpq9tnMdlZTLtpcYHsAfqsxxPidWvUz1HG3wtEhrfTf6rvE45jozvggWBmwvp81xgsK7FOLKANRzWzYRDZ1vA1K306aqreMcfM59t0p7NjbMWG3ILtLnUeV0r8Ox2YklpEmJsTILQQRY3hWtXs1Vw7e9rBolwaMzxEnS33orfhvZFteXZhMAeEi+muo5/JMlNIooZTbZjKBJNgQedrfZVvQwjSczrnroPdei8M/D+gIztc7/APZH0V9Q7AYE/Fh83/J9Q+3ispeWXhbXXullmI4BwSi+H1ajY2pgyT5xoOi1nDeKUp7ui2zbA5YB5ho1Me3VaXC9lMHTgtw7BHKf5VlhuFUGfDSYLRZo05JEqbGtmkKt1ErHuUmBrtqGIDulnXGvSQrY8OnTwjWP9KxZTDbNaAOgj6LsmNbBKho8Z65ZE9RBp8Pa0KThHim6J12TDsew/C4ef8IZSm6WrYxmlSkwfBfoTOFfLQnV2EJFQhCkASJUIARIlQgDkpHFdQuKgsoYECrSkyNDuniWtElV/Ey/JlY0ztawSBriBms6BI62mFyJTak2kbejKWWSW49j3ZAD6rH9v+B1qwDqQa//ABytDx1zm58lom08rs0J81ud0tWNrzDEuiWYHi+Ba1jyzF4Ks4D4nUhUzsnfJ8Lh5R6rTHsXg8ThnV8DUe8wcjScozgSGvBbmbqJtuvRw8SBHxCZ8tvmm3tY05hY9PLkNbJuYYGrWahcTf5PAeIcFxdJ2R+FqE86YD2+9j8k3jOzWKY3M6i4DlYuHm0Ele/06789oc3rYjU2IF/+k3i8M12oSLb3FLpXv9zQv8hdHZ7+/oeGdi8W2niHU3Wc5nwkEHwnWCOq21avY92C50GLEwdiei0OK7L0HuD3Uw5wuHRceR2UjG4B5a1jAAGgCNSVzdQ42T68PPp6llrW+TwftFgnU6tPD05fXqGSSN3H9zN+hXoPGfwlY5oqYWoWvAByOJc0mL5XG49Z9Fq6HAS6qH1g0ZcpBMTYjbZbIMgLrV3WTiu2DmWyfVzk+YuJdhMTLnE+KbtcIuLRZXv4aUzgDiX4qlUktY1gZTdULrvLoy2As25jVb78ScJin1KQw8NZlOZxvebAjb956KH2dGJbPeU2ECIIddx0gA6X6pq1U0sSwyVQpLqR5t+IPE6+Jcxz6FShRbORr2PEk6ucSMs2AA/lZ/A4iqwzTe+nP6HuaPkV9O4fHtAIrU25dDHi+RFwoNbsVwjEuFRtGmOYpHuwSby5jCL+YlPjqFJbCpQcXueL8N4zxV0MoVqz3cg1r/KZafmvUexuE4uQHYutAP5XU6WcRpBYIv1WppnB4JgZTDWgCA1oBcfPcnqVDpdrGOdldTLRMZpBjqbKk7lxnBaNUmspEviWONFusuOn7krOf+X1W16bHZO7ecswZBMxedJgabpe0eMFRzcjgcoIMaazY7rJ8WNpn15LJO6anhM1w08fDy1ueg1+JYgucxm27Wz/ACoeFfVc/wDuVMzZkEm4PQctVn+Bce72kGh8PLmNfG4LokdI+q3opMgF+UD0Hss1vXa+iTf+jn8MZpYcZp58lZU2KGMg+Fx8pVpg2AhadHp2pZRVslYdkBdpWBKu4hYiEqEAKhCEACRKhACJEqEAc5VBxVNxOk8v3VgkhLsgprDLRl0vJThvMQmHshXVWgCq7EYVw0v9Vz7dJJbx3NMLk+SI5+nQQPv0TNSoeeydfGht5pssCxSUkaY4IT3u1B32m2klc0uIPJykn91KqUVErYUiw1lIlFoeuiSwydTw9X4u8lpEiABGltL+fVO18YGgQ4dcpieh6KFUe4NyyYbaOh1lV9Qmbeyb4ijtFC40p8k+tiSb2ELqvxeqYyua3aQJ+p9VXzG+v362TTgVTrkuGN8KL5HMVi3PnMb3G3yUVtYNgR9b/cJSzwj195KiYtkFvMSfKxGnr8lGWxkYRWw5iMcSIJJv05KqJkmec9fv+SpFUBMuZeeask2NSijlz7xyXZbdMue0GNeg1+il4fB1amjco5nX2T4UTnwhVl9cOWM1qzWCXGFQcVbVrEtaIZzgy7/S3uF7OjVwk8yrOlwEfpW+rSKO75OZdq3PZcHlmB4G8G0hajA4TEEAF7iOpJW2o8B6BWWG4S1uy0upPlGTJl8Bw6qImVreHEgAFSGYYBOikFeMEuCMnYKVIAlVyAQlQgAQhCABCEIARCVIgAQhCAESELpIgBmrhmu1AUOrwhh0Lm+R/lWKFSUIy5RZTa4KSpwZ/wCWr7tB/hMU+EYhrp71juhZH0K0MolKemr9Bnjz9TO4rAVibMaZ/wAv2IVfW4fiNqUnnmZ1jfqtlKSUt6KtsvHVTRhjw7FXij5eNtly7hmKP/1f+7T93W6lEqP0NRb9ZMwlThOLNhTbfm8D6Smj2dxjpkUx1zOP7BegSiVZaKpEfq7DAM7G1z8VYD/iz+SVModhWfne9/m6B7Cy2kpQU6NMI8IXK+cuWUmC7L0afwtA9FZU+GsGylZkSmYFZOW0GjQLvKEgSqSAhKkSoAEISoARKhCABCEIAEIQgAQhCABCEIAEIQgBEIQgAQhCABCEIAEiEIAEqEIAREIQgBISgIQgBYRCEIAEIQgAQhCAFQhCABCEIAEIQgD/2Q==";
                //Bitmap decodedRawbase64Img=mes1.base64StringToBitmap(fatBurger);
                //mes1.setByteArray(image.bitmapToByteArray(decodedRawbase64Img));


                //mes.setFileName(image.getFileName());
                //mes.setFileSize(image.getFileSize());
                Log.e(TAG, "Set byte array to image ok"+image.getFileSize()+"-"+image.getFileName());
                break;

            case Message.FILE_MESSAGE:
                //TODO-Take file from URI, convert it to base64 String, send it out
                String mypath = fileUri.getPath();
                File myfile = new File(mypath); //TODO - how to put file to listView?
                String encodedBase64=encodeFileToBase64Binary(myfile);
                mes.setFilePath(mypath);
                mes.setFileUri(fileUri);
                break;
        }

        mes.setChatName(ChatName);
        mes.setRecipientChatName(ChatName); //Let message echo back to ownself for now.

        //Send text message with function "SendMessageServer" for Server to AysncTask thread.

        if(MainActivity.identity==1) {
            Log.d(TAG,"sendMessage-identity==1");
            refreshList(mes,true);

            //Send message with websocket.
            //TODO-Try to send base64 image string to web app.
            serveruni.ws.send(new MsgToJson().MsgToJson(mes));
            //new SendMessageServer(ChatActivity.this, true).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, mes); //Fixed port no.
        }
        if(MainActivity.identity==2){
            Log.d(TAG,"sendMessage-identity==2");
            refreshList(mes,true);
            clientuni.WSC.send(new MsgToJson().MsgToJson(mes));
           // new SendMessageClient(ChatActivity.this, Client.dstAddress).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, mes); //Fixed port no.
        }

        edit.setText("");
    }
    // Refresh the message list
    public static void refreshList(Message message, boolean isMine){

        Log.d(TAG, "Message is refreshed!");
        message.setMine(isMine);
//		Log.e(TAG, "refreshList: message is from :"+message.getSenderAddress().getHostAddress() );
//		Log.e(TAG, "refreshList: message is from :"+isMine );
        listMessage.add(message);
        chatAdapter.notifyDataSetChanged(); //yet to build ChatAdapter

//    	Log.v(TAG, "Chat Adapter notified of the changes");

        //Scroll to the last element of the list
        listView.setSelection(listMessage.size()-1);
    }

    // Handle click on the menu
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int idItem = item.getItemId();
        switch(idItem){
            case R.id.send_image:
                showPopup(edit);
                return true;

            case R.id.send_file: //TODO (done) - start intent to pick up file
                Log.v(TAG, "Start activity to choose file");
                //Intent chooseFileIntent = new Intent(this, FilePickerActivity.class); //Activity FilePickerActivity is responsible for file input.
                Intent chooseFileIntent=new Intent(Intent.ACTION_PICK);
                chooseFileIntent.setType("*/*");
                chooseFileIntent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(chooseFileIntent, CHOOSE_FILE);
                return true;

            /*
            case R.id.send_audio:
                Log.v(TAG, "Start activity to record audio");
                startActivityForResult(new Intent(this, RecordAudioActivity.class), RECORD_AUDIO);
                return true;

            case R.id.send_video:
                Log.v(TAG, "Start activity to record video");
                Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                takeVideoIntent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0);
                if (takeVideoIntent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(takeVideoIntent, RECORD_VIDEO);
                }
                return true;



            case R.id.send_drawing:
                Log.v(TAG, "Start activity to draw");
                Intent drawIntent = new Intent(this, DrawingActivity.class);
                startActivityForResult(drawIntent, DRAWING);
                return true;
*/
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    //Show the popup menu
    public void showPopup(View v) {
        PopupMenu popup = new PopupMenu(this, v);
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch(item.getItemId()){
                    case R.id.pick_image:
                        Log.e(TAG, "Pick an image");
                        Intent intent = new Intent(Intent.ACTION_PICK);
                        intent.setType("image/*");
                        intent.setAction(Intent.ACTION_GET_CONTENT);

                        // Prevent crash if no app can handle the intent
                        if (intent.resolveActivity(getPackageManager()) != null) {
                            startActivityForResult(intent, PICK_IMAGE);
                        }
                        break;

                    case R.id.take_photo:
                        Log.e(TAG, "Take a photo");
//
                        // This way seems to work very reliably

                        mPhotoUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, new ContentValues());
                        Intent intent4 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        intent4.putExtra(MediaStore.EXTRA_OUTPUT, mPhotoUri);
                        startActivityForResult(intent4, TAKE_PHOTO);

                        break;
                }
                return true;
            }
        });
        popup.inflate(R.menu.send_image);
        popup.show();
    }

    // Handle the data sent back by the 'for result' activities (pick/take image, record audio/video)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {

            case PICK_IMAGE:
                if (resultCode == RESULT_OK && data.getData() != null) {
                    fileUri = data.getData();
                    sendMessage(Message.IMAGE_MESSAGE);
                    //String actualPath= RealPathUtil.getRealPath(this,fileUri);
                    //if(actualPath==null){Log.d(TAG,"actualPath is a null");}
                    //else{Log.d(TAG,actualPath);}
                }
                break;
            case TAKE_PHOTO:
                if (resultCode == RESULT_OK) {

                    fileUri = mPhotoUri;
                    sendMessage(Message.IMAGE_MESSAGE);
                    tmpFilesUri.add(fileUri);

                } else if (resultCode == RESULT_CANCELED) {
                    // User cancelled the image capture
                } else {
                    // Image capture failed, advise user
                }
                break;
            case CHOOSE_FILE:
                if(resultCode==RESULT_OK){
                    fileUri=data.getData();
                    //Test if I could pick up image file first(successful)
                    sendMessage(Message.FILE_MESSAGE);
                }

                break;
        }
    }

    //TODO (done) - Encode file to base64 string
    private static String encodeFileToBase64Binary(File fileName) {

        int size = (int) fileName.length();
        byte[] bytes = new byte[size];
        try {
            BufferedInputStream buf = new BufferedInputStream(new FileInputStream(fileName));
            buf.read(bytes, 0, bytes.length);
            buf.close();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        String encodedString = Base64.encodeToString(bytes,Base64.DEFAULT);

        return encodedString;
    }


    public String getRealPathFromURI(Context context, Uri contentUri) {
        String [] proj={MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(contentUri, proj,  null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String path = cursor.getString(column_index);
        cursor.close();
        //String path="testing";


        return path;
    }


}
