package com.example.websocketchat.Util;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class ChatFragmentMaster extends AppCompatActivity {
public static Button transBut;
public static Button refreshCAFBut;
public static Button openFrag;
public static Button testBut;
public static int position;
public static int AltCount=1;
public final FragmentManager fm=getSupportFragmentManager();
public static int CAFCounter=0;
public static List<ChatActivityFragment> listfragment=new ArrayList<>();
public static List<CAFclass> listCAFclass=new ArrayList<>();
public static UserLIstView fragment;
private static final String TAG = "ChatFragmentMaster";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_fragment_master);

        //Create back logo button on menu
        ActionBar actionBar = this.getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);

        refreshCAFBut=new Button(this);
        transBut=new Button(this);
        openFrag=new Button(this);
        //TODO-listfragmentSwap6
        //Log.d(TAG,"listfragment size is "+String.valueOf(listfragment.size()));
        Log.d(TAG,"listfragment size is "+String.valueOf(listCAFclass.size()));
        startNewCAF(fm);
        //TODO-listfragmentSwap7
        //hideAllFragments(listfragment,fm);
        hideAllFragmentsCAFclass(listCAFclass,fm);


        final FragmentTransaction transaction = fm.beginTransaction();
        fragment = new UserLIstView();
        transaction.add(R.id.chat_fragment_container, fragment);
        transaction.commit();

        openFrag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fm.beginTransaction()
                        .hide(fragment)
                        .commit();
                //TODO-listfragmentSwap8
                //hideAllFragments(listfragment,fm);
                hideAllFragmentsCAFclass(listCAFclass,fm);
                Log.d(TAG,"position is "+position);
                //TODO-listfragmentSwap13
                //showFragment(position,fm);
                showFragmentCAFclass(ClientUni.userList.get(position),fm);


            }
        });

        refreshCAFBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startNewCAF(fm);
            }
        });



        transBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO-listfragmentSwap9
                //hideAllFragments(listfragment,fm);
                hideAllFragmentsCAFclass(listCAFclass,fm);
                fm.beginTransaction()
                        .hide(fragment)
                        .commit();
                if(AltCount==1){

                    showFragment(0,fm);


                    //Toast.makeText(ChatFragmentMaster.this, "Changed to ChatActivityFragment", Toast.LENGTH_SHORT).show();
                    AltCount=2;
                }else
                if(AltCount==2){
                    fm.beginTransaction()
                        .show(fragment)
                        .commit();


                    //Toast.makeText(ChatFragmentMaster.this, "Changed to UserListView", Toast.LENGTH_SHORT).show();
                    AltCount=1;
                    ChatActivityFragment.hideKeyboard(v.getContext());
                }
            }
        });



    }
    public static void showFragment(int position,FragmentManager fm){

        fm.beginTransaction()
                .show(listfragment.get(position))
                .commit();
        AltCount=2;
    }
    public static void showFragmentCAFclass(String username,FragmentManager fm){
        //TODO-Got to substring amended username that has included unread message count.
        int gotPos=username.indexOf("-got");
        //Substring off -got (1) message if gotPos is more than -1
        String amendedString=username;
        if(gotPos>-1) {
            amendedString = username.substring(0, gotPos - 1);

        }

        //Search for ChatActivityFragment position in listCAFclass
        int CAFpos=-1;
        for(int i=0;i<listCAFclass.size();i++){
            if(amendedString.equals(listCAFclass.get(i).getUsername())){
                CAFpos=i;
            }
        }
        fm.beginTransaction()
           .show(listCAFclass.get(CAFpos).getCAF())
           .commit();
        AltCount=2;

    }

    private static void hideAllFragments(List<ChatActivityFragment> listFragment,FragmentManager fragmentManager){

        for(int x=0;x<listFragment.size();x++){
            fragmentManager.beginTransaction()
            .hide(listFragment.get(x))
            .commit();
        }

    }
    private static void hideAllFragmentsCAFclass(List<CAFclass> listCAFclass,FragmentManager fragmentManager){
        for(int x=0;x<listCAFclass.size();x++){
            fragmentManager.beginTransaction()
             .hide(listCAFclass.get(x).getCAF())
             .commit();
        }
    }

    public static void startNewCAF(FragmentManager fragmentManager){
        Log.d(TAG,"ClientUni.userList size is "+String.valueOf(ClientUni.userList.size()));
        //TODO-listfragmentSwap1
        //Log.d(TAG,"listfragment size is "+String.valueOf(listfragment.size()));
        Log.d(TAG,"listCAFclass size is "+String.valueOf(listCAFclass.size()));
        //TODO-listfragmentSwap2
        //if(ClientUni.userList.size()>listfragment.size()) {
        if(ClientUni.userList.size()>listCAFclass.size()) {
            //TODO-listfragmentSwap3
            //int netSize=ClientUni.userList.size() - listfragment.size();
            int netSize=ClientUni.userList.size() - listCAFclass.size();
            Log.d(TAG,"netSize is "+String.valueOf(netSize));
            //userList and listfragment should ha ChatFragmentMaster CFM=new ChatFragmentMaster();
            for (int x = 0; x <netSize; x++) {
                Log.d(TAG,"x equals to "+String.valueOf(x));
                //Create sufficient number of fragments to match with number of users.
                ChatActivityFragment CAF=new ChatActivityFragment();
                //TODO-listfragmentSwap4
                //listfragment.add(CAF);
                //- listCAFclass should add a new class object that stores usernames and CAFs instead of just CAF.
                CAFclass CAFclass=new CAFclass(ClientUni.userList.get(ClientUni.userList.size()-netSize+x),CAF);
                listCAFclass.add(CAFclass);

                //TODO-listfragmentSwap5
                /*
                fragmentManager.beginTransaction()
                        .add(R.id.chat_fragment_container, listfragment.get(CAFCounter))
                        .hide(listfragment.get(CAFCounter))
                        .commit();
                */
                fragmentManager.beginTransaction()
                        .add(R.id.chat_fragment_container, listCAFclass.get(CAFCounter).getCAF())
                        .hide(listCAFclass.get(CAFCounter).getCAF())
                        .commit();


                CAFCounter++;
            }
            //-test if listCAFclass stores all the information as it's supposed to.
            //- replace all list_fragment uses with listCAFclass.get(i).getCAF
            for(int i=0;i<listCAFclass.size();i++){
                Log.d(TAG,listCAFclass.get(i).getUsername()+"'s ChatActivityFragment is available!");
            }

        }
    }
    @Override
    public void onBackPressed(){
       //-When AltCount=2, I'm in Chat fragment. When AltCount=1, I'm in user list.
       //When I'm in chat fragment, back press leads to user list or transBut click
        Log.d(TAG,"AltCount during back press is "+AltCount);
        if(AltCount==2){
            transBut.performClick();
            ChatActivityFragment.hideKeyboard(this);
        }else


        /**This intent will send app back to home*/
        if(AltCount==1) {
            Intent a = new Intent(Intent.ACTION_MAIN);
            a.addCategory(Intent.CATEGORY_HOME);
            a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(a);
        }
        /***/
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int idItem = item.getItemId();
        switch(idItem){
            case android.R.id.home:
                Log.d(TAG,"back button is pressed!");
                onBackPressed();
                return true;


            default:return super.onOptionsItemSelected(item);

        }

    }

    /**Search for CAF position in listCAFclass*/
    public static int findCAFposInList(String username){ //username is the one will be come "smallbear -got (1) message!
        Log.d(TAG,"username is "+username);
        //Detect position of -got
        int gotPos=username.indexOf("-got");
        //Substring off -got (1) message if gotPos is more than -1
        String amendedString=username;
        if(gotPos>-1) {
            amendedString = username.substring(0, gotPos - 1);
            Log.d(TAG, "Amended username is " + amendedString);
        }

        int CAFposInList=-1;

       // if ( userList.get(i).indexOf(recMsg.getChatName())>-1 ) {
        for(int y=0;y<listCAFclass.size();y++){
            Log.d(TAG,"username in listCAFclass is "+ChatFragmentMaster.listCAFclass.get(y).getUsername());
            Log.d(TAG,"index position is "+ChatFragmentMaster.listCAFclass.get(y).getUsername().indexOf(username));

            if(amendedString.equals(ChatFragmentMaster.listCAFclass.get(y).getUsername())){
                CAFposInList=y;
                Log.d(TAG,"CAF position is "+CAFposInList);
            }
        }

        return CAFposInList;
    }




}
