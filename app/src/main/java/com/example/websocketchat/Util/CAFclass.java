/**This will be a class object that stores ChatActivityFragment and username*/
package com.example.websocketchat.Util;

public class CAFclass {
    String username;
    ChatActivityFragment CAF;

    public CAFclass(String username, ChatActivityFragment CAF){
        this.username=username;
        this.CAF=CAF;

    }

    public void setUsername(String username){this.username=username; }
    public String getUsername(){return this.username;}
    public void setCAF(ChatActivityFragment CAF){this.CAF=CAF;}
    public ChatActivityFragment getCAF(){return this.CAF;}

}
