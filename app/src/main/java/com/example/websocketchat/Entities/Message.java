package com.example.websocketchat.Entities;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.Serializable;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class Message implements Serializable{
    private static final String TAG = "Message";
    public static final int TEXT_MESSAGE = 1;
    public static final int IMAGE_MESSAGE = 2;
    public static final int VIDEO_MESSAGE = 3;
    public static final int AUDIO_MESSAGE = 4;
    public static final int FILE_MESSAGE = 5;
    public static final int DRAWING_MESSAGE = 6;
    public static final int USERLIST_MESSAGE=9;
    public static final int LOGIN_MESSAGE=11;
    public static final int SIGNUP_MESSAGE=12;
    public static final int BROADCAST_MESSAGE=14;

    private int mType;
    private String mText;
    private String chatName;
    private byte[] byteArray;
    private InetAddress senderAddress;
    private String fileName;
    private long fileSize;
    private String filePath;
    private boolean isMine;
    private String dstAddressReceive;
    private int portNoReceive;
    private String dstAddressSend;
    private int portNoSend;
    private String recipientChatName;
    private String base64ImageString;
    private Uri fileUri;
    private List<String> userList;
    private List<String> loginInfo;
    private List<String> signupInfo;

    //// MARK: 16/06/2018 stores a record of all users this message been to
    private ArrayList<String> user_record;

    //Getters and Setters

    public void setFileUri(Uri fileUri){this.fileUri=fileUri;}
    public Uri getFileUri(){return this.fileUri;}
    public void setUserList(List<String> userList){this.userList=userList;}
    public List<String> getUserList(){return userList;}
    public void setLoginInfo(List<String> loginInfo){this.loginInfo=loginInfo;}
    public List<String> getLoginInfo(){return this.loginInfo;}
    public void setSignUpInfo(List<String> signupInfo){this.signupInfo=signupInfo;}
    public List<String> getSignupInfo(){return this.signupInfo;}

    public String getmText() { return mText; }
    public void setmText(String mText) { this.mText = mText; }
    public int getmType() { return mType; }
    public void setmType(int mType) { this.mType = mType; }

    public String getChatName() { return chatName; }
    public void setChatName(String chatName) { this.chatName = chatName; }
    public byte[] getByteArray() { return byteArray; }
    public void setByteArray(byte[] byteArray) { this.byteArray = byteArray; }
    public InetAddress getSenderAddress() { return senderAddress; }
    public void setSenderAddress(InetAddress senderAddress) { this.senderAddress = senderAddress; }
    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }
    public long getFileSize() { return fileSize; }
    public void setFileSize(long fileSize) { this.fileSize = fileSize; }
    public String getFilePath() { return filePath; }
    public void setFilePath(String filePath) { this.filePath = filePath; }
    public boolean isMine() { return isMine; }
    public void setMine(boolean isMine) { this.isMine = isMine; Log.d(TAG,"setMine function is called!"); }
    public ArrayList<String> getUser_record() {
        return user_record;
    }
    public void setUser_record(String user_name) {
        this.user_record.add(user_name);
    }
    public String getDstAddressReceive(){return dstAddressReceive;}
    public int getPortNoReceive() {return portNoReceive;}
    public String getDstAddressSend(){return dstAddressSend;}

    public void setRecipientChatName(String recipientChatName){this.recipientChatName=recipientChatName;}
    public String getRecipientChatName(){return recipientChatName;}
    public void setBase64ImageString(String base64ImageString) {this.base64ImageString=base64ImageString;}
    public String getBase64ImageString() {return base64ImageString;}

    //public Message(int type, String text, InetAddress sender, String name){
    /**Modify message variables to include message receiving member's dstAddress and portNoReceive**/
    public Message(int type, String text, String dstAddressReceive, int portNoReceive, String dstAddressSend, String recipientChatName, String ChatName){
        Log.d(TAG,"Message function is called!");
        this.mType = type;
        this.mText = text;
        this.dstAddressReceive=dstAddressReceive;
        this.portNoReceive=portNoReceive;
        this.dstAddressSend=dstAddressSend;
        this.recipientChatName=recipientChatName;
        this.chatName=ChatName;
        //user_record = new ArrayList<>();
    }

    //TODO- Base64 image string to bitmap
    public Bitmap base64StringToBitmap(String base64ImgString){
    //decode base64 string to image
        byte[] imageBytes = Base64.decode(base64ImgString, Base64.DEFAULT);
        Bitmap decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);

        return decodedImage;
    }

    public Bitmap byteArrayToBitmap(byte[] b){
        Log.v(TAG, "Convert byte array to image (bitmap)");
        return BitmapFactory.decodeByteArray(b, 0, b.length);
    }

    public void saveByteArrayToFile(Context context){
        Log.v(TAG, "Save byte array to file");
        switch(mType){
            case Message.AUDIO_MESSAGE:
                filePath = context.getExternalFilesDir(Environment.DIRECTORY_MUSIC).getAbsolutePath()+"/"+fileName;
                break;
            case Message.VIDEO_MESSAGE:
                filePath = context.getExternalFilesDir(Environment.DIRECTORY_MOVIES).getAbsolutePath()+"/"+fileName;
                break;
            case Message.FILE_MESSAGE:
                filePath = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath()+"/"+fileName;
                break;
            case Message.DRAWING_MESSAGE:
                filePath = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES).getAbsolutePath()+"/"+fileName;
                break;
        }

        File file = new File(filePath);

        if (file.exists()) {
            file.delete();
        }

        try {
            FileOutputStream fos=new FileOutputStream(file.getPath());

            fos.write(byteArray);
            fos.close();
            Log.v(TAG, "Write byte array to file DONE !");
        }
        catch (java.io.IOException e) {
            e.printStackTrace();
            Log.e(TAG, "Write byte array to file FAILED !");
        }
    }
}
