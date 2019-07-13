package com.example.websocketchat.Util;


import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.example.websocketchat.CustomAdapters.ChatAdapter;
import com.example.websocketchat.CustomAdapters.ListAdapter;
import com.example.websocketchat.Entities.JsonToMsg;
import com.example.websocketchat.Entities.Message;
import com.example.websocketchat.Entities.MsgToJson;

import java.util.ArrayList;
import java.util.List;

public class UserLIstView extends Fragment {
    private static final String TAG = "UserListView";
    public static ListView userListView;
    public static List<String> userList=new ArrayList<>();
    //public static List<String> userList=ClientUni.userList;
    public static ListAdapter listAdapter;
    public static Button refreshBut;
    private int counter=0;
    public static Button myButton;
    public static Button sortAlpha;
    public static Button sortAlphaReverse;
    public static int sortCode=-1; //sort code would be either 1,2,3 or 4 if sort button has ever been clicked!


    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);


    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // TODO Add your menu entries here

        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.userlistmenu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int idItem = item.getItemId();
        switch(idItem){
            case android.R.id.home:
                Log.d(TAG,"back button is pressed!");
                return true;
            case R.id.refresh_userlist:
            Log.d(TAG,"Refresh button logo has been clicked!");
             refreshBut.performClick();
             return true;
            case R.id.sort_alphabetically:
             Log.d(TAG,"Sort alphabetically button has been clicked");
             sortAlpha.performClick();
                return true;
            case R.id.sort_alphabetically_reverse:
            Log.d(TAG,"Sort reverse-alphabetically button has been clicked!");
            sortAlphaReverse.performClick();
                return true;

            default:return super.onOptionsItemSelected(item);

        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.userlist, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        sortAlpha=new Button(view.getContext());
        sortAlphaReverse=new Button(view.getContext());
        refreshBut=new Button(view.getContext());
        //Initialize the adapter for the chat
        userListView = view.findViewById(R.id.userlistview);

        listAdapter = new ListAdapter(view.getContext(), ClientUni.userList);

        userListView.setAdapter(listAdapter);

        /**sort button to sort Client.userList*/
        sortAlpha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Log.d(TAG,"SortAlpha button has been clicked!");
                List<String> sortedUserList= ClientUni.sortUserList(ClientUni.userList,ClientUni.alphabeticalOrder);
                for(int i=0;i<sortedUserList.size();i++) {
                    Log.d(TAG, "Sorted user list is " +sortedUserList.get(i));
                }
                //Refresh userList after sorting!!
                //refreshlist(ClientUni.userList);
                listAdapter.notifyDataSetChanged();

                //Set sortCode
                sortCode=ClientUni.alphabeticalOrder;
            }
        });


        /**sort Client.userList in reverse alphabetical order*/
        sortAlphaReverse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<String> sortedUserList= ClientUni.sortUserList(ClientUni.userList,ClientUni.reverseAlphaOrder);
                for(int i=0;i<sortedUserList.size();i++) {
                    Log.d(TAG, "Sorted user list is " +sortedUserList.get(i));
                }
                listAdapter.notifyDataSetChanged();
                sortCode=ClientUni.reverseAlphaOrder;
            }
        });


        /**refresh button to refresh user list*/
        refreshBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Log.d(TAG,"refreshBut button has been clicked!");
//                Toast.makeText(v.getContext(), String.valueOf(counter), Toast.LENGTH_SHORT).show();
                counter++;
                //Simply send a msg to server to retrieve updated user list
                Message upMsg = new Message(Message.TEXT_MESSAGE, "I want userList", null, 0, null, ChatActivityFragment.ChatName, ChatActivityFragment.ChatName);
                ClientUni.WSC.send(new MsgToJson().MsgToJson(upMsg));



                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() { //ChatActivity only opens once when websocket is connected.
                    @Override
                    public void run() {
                        // Do something after 5s = 5000ms
                        ChatFragmentMaster.refreshCAFBut.performClick();
                        refreshlist(ClientUni.userList);
                        //resort userList if any sorting buttons have been clicked before!

                    }
                }, 2000);
            }
        });

    }



    public static void refreshlist(List<String> newuserList){
        switch(sortCode){
            case -1:
                listAdapter.notifyDataSetChanged();
                break;
            case ClientUni.alphabeticalOrder:
                sortAlpha.performClick();
                //sortAlpha would call for refreshlist function. Writing code as such would cause infinite loop!
                break;
            case ClientUni.reverseAlphaOrder:
                sortAlphaReverse.performClick();
                break;
            case ClientUni.shortNameToLong:
                break;
            case ClientUni.longNameToShort:
                break;

        }
        /*
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() { //ChatActivity only opens once when websocket is connected.
            @Override
            public void run() {
                listAdapter.notifyDataSetChanged();
            }
        }, 2000);

*/
    }


}
