package com.example.websocketchat.CustomAdapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.websocketchat.AsyncTasks.AsyncTextUpdate;
import com.example.websocketchat.Util.ChatActivityFragment;
import com.example.websocketchat.Util.ChatFragmentMaster;
import com.example.websocketchat.Util.ClientUni;
import com.example.websocketchat.Util.R;
import com.example.websocketchat.Util.UserLIstView;

import java.util.List;

public class ListAdapter extends BaseAdapter {

    private static final String TAG = "ListAdapter";
    Context context;
    List<String> UserList;
    TextView tv;
    private LayoutInflater inflater;


    public ListAdapter(Context context, List<String> UserList){
        this.context=context;
        this.UserList=UserList;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }
    @Override
    public int getCount() {
        return UserList.size();
    }
    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        Log.d(TAG,"ListAdapter getView");
        convertView = inflater.inflate(R.layout.message, null);


        tv=convertView.findViewById(R.id.TX1);

        convertView.setTag(position);

        tv.setText(UserList.get(position));

        /**Add click listener to username to begin chat with selected user*/
        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(v.getContext(), "Your chat with "+UserList.get(position)+" begins here", Toast.LENGTH_SHORT).show();
                    //TODO-listfragmentSwap11
                    //ChatActivityFragment CAF= ChatFragmentMaster.listfragment.get(position);
                    int CAFposInList=ChatFragmentMaster.findCAFposInList(UserList.get(position));
                    ChatActivityFragment CAF=ChatFragmentMaster.listCAFclass.get(CAFposInList).getCAF();

                    //- listfragment and UserList are merely matched by position in lists.
                    //- This setup is vulnerable to user and its chat fragment mismatch if alignment is out!
                    //- Solution to circumvent this vulnerability could be making listfragment stores usernames too.
                    //- Current setup is very difficult for adding feature to sort username either by alphabetical order or other means.


                    //-UserList might contain notification message and becomes unfit to be used as recipientName
                    //- We got to filter off userlist to extract only username regardless of whether it contains notification msg or not.
                    int UNlastIndex=UserList.get(position).indexOf(" -got (");
                    String usernameonly=UserList.get(position);
                    if(UNlastIndex>-1) {
                        usernameonly = UserList.get(position).substring(0, UNlastIndex);
                    }else
                    if(UNlastIndex==-1){
                        usernameonly=UserList.get(position);
                    }
                    CAF.recipientName=usernameonly;
                    ChatFragmentMaster.position=position;
                    Log.d(TAG,"AsyncTextUpdate.unreadMsgCount is "+AsyncTextUpdate.unreadMsgCount.get(position)); //It shouldn't be zero before reset!
                    //-Store AsyncTextUpdate.unreadMsgCount before reset, to be used later.
                    int storedUMC=AsyncTextUpdate.unreadMsgCount.get(position);
                    AsyncTextUpdate.revertUser(position);
                    ChatFragmentMaster.openFrag.performClick();

                    //-When username is clicked, deduct ClientUni.UnreadMsgCount, for exact same unread messages comprised in this user fragment.
                    //-AsyncTextUpdate.unreadMsgCount.get(position) contains the respected unreadMsg for this fragment.
                    Log.d(TAG,"AsyncTextUpdate.unreadMsgCount is "+AsyncTextUpdate.unreadMsgCount.get(position)); //It's zero but it's been reset after username is clicked!

                    ClientUni.UnreadMsgCount=ClientUni.UnreadMsgCount-storedUMC;
                    Log.d(TAG,"ClientUni.UnreadMsgCount is "+ClientUni.UnreadMsgCount);
                    //-Update drop-down notification, by calling AsyncTextUpdate.pushNotification



            }
        });

        return convertView;
    }



}


