package angelhack.seattle.soundhop;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.parse.ParseFacebookUtils;
import com.parse.ui.ParseLoginBuilder;


public class MainActivity extends ActionBarActivity {
    String audioID, artist_name, artist_band;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Utils.setContext(this);
        ParseLoginBuilder builder = new ParseLoginBuilder(MainActivity.this);
        getSupportActionBar().hide();
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment,new JoinGroupFragment()).commit();
        startActivityForResult(builder.build(), Globals.FBLOGIN);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == Globals.FBLOGIN){
            if(resultCode == RESULT_OK){
                Utils.getFacebookProfilePicture();
                Utils.saveIPAddress();
                JoinGroupFragment.animateTransition();
            } else if(resultCode == RESULT_CANCELED){
                Log.e("FBLOGIN", "login failed");
            }
        }
        else if (requestCode == Globals.PICKSONG) { //When a song is picked
            if (resultCode == RESULT_OK) { //If they selected a media file
                Uri mediaUri = data.getData();
                String[] proj = { MediaStore.Audio.Media._ID,
                        MediaStore.Audio.Media.DATA,
                        MediaStore.Audio.Media.TITLE,
                        MediaStore.Audio.Artists.ARTIST };

                Cursor tempCursor = managedQuery(mediaUri,
                        proj, null, null, null);

                tempCursor.moveToFirst(); //reset the cursor
                int col_index=-1;
                int numSongs=tempCursor.getCount();
                int currentNum=0;
                do{
                    col_index = tempCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE);
                    artist_name = tempCursor.getString(col_index);
                    col_index = tempCursor.getColumnIndexOrThrow(MediaStore.Audio.Artists.ARTIST);
                    artist_band = tempCursor.getString(col_index);
                    //do something with artist name here
                    //we can also move into different columns to fetch the other values

                    currentNum++;
                }while(tempCursor.moveToNext());

                Globals.playlistArray.add(new SongItem(artist_name,artist_band,500, mediaUri));
                MainActivityFragment.playlistAdapter.notifyDataSetChanged();
            }
        }
        ParseFacebookUtils.onActivityResult(requestCode, resultCode, data);
    }
}
