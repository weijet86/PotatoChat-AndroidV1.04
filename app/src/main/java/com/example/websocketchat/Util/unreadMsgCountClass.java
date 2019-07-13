package com.example.websocketchat.Util;

public class unreadMsgCountClass {
    String username;
    int unreadMsgCount;
    public unreadMsgCountClass(String username, int unreadMsgcount){
        this.username=username;
        this.unreadMsgCount=unreadMsgcount;

    }

    public void setusername(String username){this.username=username;}
    public String getusername(){return this.username;}
    public void setUnreadMsgCount(int unreadMsgcount){this.unreadMsgCount=unreadMsgcount;}
    public int getUnreadMsgCount(){return this.unreadMsgCount;}
}
