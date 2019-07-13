package com.example.websocketchat.AsyncTasks;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.UserHandle;
import android.widget.Toast;

import com.example.websocketchat.Entities.Message;
import com.example.websocketchat.Util.ChatFragmentMaster;
import com.example.websocketchat.Util.ClientUni;
import com.example.websocketchat.Util.UserLIstView;

public class AsyncUIelements extends AsyncTask<Integer,Integer,Integer> {
    private static final String TAG = "AsyncUIelements";
    Context context;
    Message recMsg;

    public AsyncUIelements(Context context,Message recMsg){
        this.context = context;
        this.recMsg=recMsg;


    }

    @Override
    protected Integer doInBackground(Integer[] instruction) {
        publishProgress(instruction);
        return instruction[0];
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);

        switch(values[0]){
            case 1:
                UserLIstView.refreshBut.performClick();
                final Message frecmsg=recMsg;
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        new AsyncTextUpdate(context, false).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, frecmsg);
                    }
                },2200);

                break;
            case 2:
                UserLIstView.refreshlist(ClientUni.userList);
                ChatFragmentMaster.CAFCounter--;
                break;
        }

    }
    @Override
    protected void onPostExecute(Integer result) {

        super.onPostExecute(result);
    }


}
