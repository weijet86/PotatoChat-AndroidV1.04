package com.example.websocketchat.Util;

public class tempUsersListClass {

    String username;
    String tempStoredString;

    public tempUsersListClass(String username, String tempStoredString){
        this.username=username;
        this.tempStoredString=tempStoredString;
    }

    public void setusername(String username){this.username=username;}
    public String getusername(){return this.username;}
    public void setTempStoredString(String tempStoredString){this.tempStoredString=tempStoredString;}
    public String getTempStoredString(){return this.tempStoredString;}
}
