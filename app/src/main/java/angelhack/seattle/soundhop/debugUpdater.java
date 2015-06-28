package angelhack.seattle.soundhop;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;

import java.util.Date;

/**
 * Created by devanshk on 11/27/14.
 */
public class debugUpdater extends AsyncTask<Void,Integer,Void> {
    private final String TAG = "Async_Schedule_Updater";
    private static TextView textView;
    private static Activity parent;

    public debugUpdater(Activity p, TextView tv){
        parent=p;
        textView=tv;
    }


    @Override
    protected void onPreExecute(){
        super.onPreExecute();
    }

    @Override
    protected Void doInBackground(Void... params) {
        Log.e(TAG, "Started Updating Debug");
        while(parent!=null){
            parent.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try{
                        //textView.setText(""+MainActivity.mediaPlayer.getCurrentPosition());
                        Integer delay = MainActivity.prefs.getInt("delay",0);
                        textView.setText(""+(new Date().getTime()+delay));
                        //textView.setText(""+ new Date().getTime());
                    } catch(Exception e){e.printStackTrace();}
                }
            });
            try{
            Thread.sleep(10);} catch(Exception e){e.printStackTrace();}
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void v){
        //Log.e("timeUpdater","Stopped Updating Time.");
    }
}
