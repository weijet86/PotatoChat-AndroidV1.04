package com.example.websocketchat.Util;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.websocketchat.CustomAdapters.ChatAdapter;
import com.example.websocketchat.Entities.FileUtils;
import com.example.websocketchat.Entities.Image;
import com.example.websocketchat.Entities.Message;
import com.example.websocketchat.Entities.MsgToJson;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ChatActivityFragment extends Fragment {
    private static final String TAG = "ChatActivity";
    private static final int PICK_IMAGE = 1;
    private static final int TAKE_PHOTO = 2;
    private static final int CHOOSE_FILE = 5;
    private EditText edit;
    public ListView listView;
    public List<Message> listMessage;
    private ChatAdapter chatAdapter;

    //Variables for enter Chat Name
    private EditText EnterChatName;
    private Button SetChatName;
    public static String ChatName;
    public String recipientName;
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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);


    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // TODO Add your menu entries here

        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.caf_menu, menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int idItem = item.getItemId();
        switch(idItem){
            case R.id.chatname_set:
               //Do soemthing when set chat name logo is clicked
                Toast.makeText(getContext(), "set chat name logo is clicked!", Toast.LENGTH_SHORT).show();
                /**Create prompt user input for chat name to save space in UI.*/
                LayoutInflater layoutInflaterAndroid = LayoutInflater.from(getContext());
                View mView = layoutInflaterAndroid.inflate(R.layout.chatname_input_prompt, null);
                AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(getContext());
                alertDialogBuilderUserInput.setView(mView);
                final EditText userInputDialogEditText = (EditText) mView.findViewById(R.id.EnterChatNameA);
                alertDialogBuilderUserInput
                        .setCancelable(false)
                        .setPositiveButton("Send", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogBox, int id) {
                                // ToDo get user input here
                                String inputText=userInputDialogEditText.getText().toString();
                                Toast.makeText(getContext(), inputText, Toast.LENGTH_SHORT).show();
                                ChatName=inputText;
                                //TODO- This has to send a msg to server to request for change of chat name.
                                //TODO-Server will have to have the feature to change chat name then client refresh user list.
                            }
                        })

                        .setNegativeButton("Cancel",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialogBox, int id) {
                                        dialogBox.cancel();
                                    }
                                });

                AlertDialog alertDialogAndroid = alertDialogBuilderUserInput.create();
                alertDialogAndroid.show();
                /***/
                return true;


            default:return super.onOptionsItemSelected(item);

        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.activity_caf, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        Log.d(TAG,"ChatActivityFragment is started!");

        /**Initialize the adapter for the chat*/
        listView = (ListView) view.findViewById(R.id.messageList);
        listMessage = new ArrayList<Message>();
        //ChatAdapter pending to be built!
        chatAdapter = new ChatAdapter(view.getContext(), listMessage);
        listView.setAdapter(chatAdapter);
        /***/

        /**Message that shows chatAdapter is working*/
        CAmes = new Message(Message.TEXT_MESSAGE,"ChatAdapter working!!", null, 0,null,ChatName,ChatName);
        //CAmes.setChatName("I love chitChatting");
        //TODO (done0- let's test out functionality of JsonToMsg class(works for Text Message)
        String testJson=new MsgToJson().MsgToJson(CAmes);
        CAmes.setmText(testJson);
        refreshList(CAmes,true);
        /***/

        /**Click listView to hide keyboard*/
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,long id) {
                hideKeyboard(view.getContext());
            }
        });
        /***/


        /**Send message button*/
        button = (Button) view.findViewById(R.id.sendMessage);
        edit = (EditText) view.findViewById(R.id.editMessage);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if(!edit.getText().toString().equals("")){
                    Log.d(TAG, "Send message");
                    sendMessage(Message.TEXT_MESSAGE);
                }
            }
        });
        /***/




    }
    public void sendMessage(int type){
        //Build sendMessage function
        Log.d(TAG,"sendMessage");
        Message mes = new Message(type, null, null, 0,null,"potatoMan",ChatName);

        mes.setRecipientChatName(mes.getChatName()); //Set recipientChatName the same as chatName to have receive echo message.
        switch(type){
            case Message.TEXT_MESSAGE:
                mes.setmText(edit.getText().toString());
                edit.setText("");
                break;
            case Message.IMAGE_MESSAGE:
                Log.d(TAG,"sendMessage-Message.Image.message");

                Image image = new Image(getContext(), fileUri);
                Log.e(TAG, "Bitmap from url ok" + fileUri);

                String filePath=fileUri.getPath();
                //TODO- Finally found function FileUtils to be working perfectly!
                String realPath= FileUtils.getRealPath(getContext(),fileUri);
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
        mes.setRecipientChatName(recipientName); //Let message echo back to ownself for now.

        //Send text message with function "SendMessageServer" for Server to AysncTask thread.



            Log.d(TAG,"sendMessage-identity==2");
            refreshList(mes,true);
            clientuni.WSC.send(new MsgToJson().MsgToJson(mes));



        edit.setText("");
    }

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

    // Refresh the message list
    public void refreshList(Message message, boolean isMine){

        Log.d(TAG, "Message is refreshed!");
        message.setMine(isMine);
        listMessage.add(message);
        chatAdapter.notifyDataSetChanged(); //yet to build ChatAdapter


        //Scroll to the last element of the list
        listView.setSelection(listMessage.size()-1);
    }
    //Function to hide keyboard when listview is clicked!
    public static void hideKeyboard( Context context ) {

        try {
            InputMethodManager inputManager = ( InputMethodManager ) context.getSystemService( Context.INPUT_METHOD_SERVICE );

            View view = ( (Activity) context ).getCurrentFocus();
            if ( view != null ) {
                inputManager.hideSoftInputFromWindow( view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS );
            }
        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }
    public void isKeyBoardShow(Context context) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm.isActive()) {
            imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0); // hide
        } else {
            imm.toggleSoftInput(0, InputMethodManager.SHOW_IMPLICIT); // show
        }
    }





}
