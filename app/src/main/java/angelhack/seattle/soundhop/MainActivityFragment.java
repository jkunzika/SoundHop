package angelhack.seattle.soundhop;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.nineoldandroids.animation.Animator;
import com.nononsenseapps.filepicker.FilePickerActivity;

import java.util.ArrayList;
import java.util.Random;


/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {
    final static String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    Typeface Avenir_Light;
    static TextView groupTitle, tabTitle, tabArtist;
    View tabLayout, addSong;
    ImageView tabPlayPause;
    ListView songList;
    static SongAdapter playlistAdapter;
    MediaPlayer temp;

    public MainActivityFragment() {
    }

    private static String generateID() {
        Random r = new Random();
        String s = "";
        for(int i = 0; i < 5; i++){
            s += alphabet.charAt(r.nextInt(alphabet.length()));
        }
        return s;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Avenir_Light = Typeface.createFromAsset(getActivity().getAssets(), "Avenir-Light.ttf");
        View v = inflater.inflate(R.layout.fragment_main, container, false);

        //Set the font of all the TextViews to Avenir
        ((TextView)v.findViewById(R.id.group_title)).setTypeface(Avenir_Light);
        ((TextView)v.findViewById(R.id.tab_name)).setTypeface(Avenir_Light);
        ((TextView)v.findViewById(R.id.tab_artist)).setTypeface(Avenir_Light);

        //Map References
        groupTitle = (TextView)v.findViewById(R.id.group_title);
        tabLayout = v.findViewById(R.id.tab_layout);
        tabLayout.setVisibility(View.INVISIBLE);
        tabTitle = (TextView)v.findViewById(R.id.tab_name);
        tabArtist = (TextView)v.findViewById(R.id.tab_artist);
        tabPlayPause = (ImageView)v.findViewById(R.id.tab_play);
        addSong = v.findViewById(R.id.addSongButton);

        //Set up the song listview
        playlistAdapter = new SongAdapter(Globals.playlistArray);
        songList = (ListView)v.findViewById(R.id.songs_listview);
        songList.setAdapter(playlistAdapter);
        songList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SongItem cur = playlistAdapter.getItem(position);
                if (temp!=null)
                    temp.stop();
                tabPlayPause.setImageDrawable(getResources().getDrawable(R.drawable.play_ring));
                temp = null;
                respecTab(cur);
            }
        });

        songList.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        songList.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
            @Override
            public void onItemCheckedStateChanged(android.view.ActionMode actionMode, int i, long l, boolean b) {

            }

            @Override
            public boolean onCreateActionMode(android.view.ActionMode mode, Menu menu) {
                MenuInflater inflater = mode.getMenuInflater();
                inflater.inflate(R.menu.playlist_context, menu);
                return true;
            }

            @Override
            public boolean onPrepareActionMode(android.view.ActionMode actionMode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(android.view.ActionMode mode, MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menu_item_delete_song: //If they want to delete the items they selected
                        for (int i = Globals.playlistArray.size() - 1; i > -1; i--) {
                            if (songList.isItemChecked(i)) {
                                SongItem current = Globals.playlistArray.get(i);
                                if (current.getUri() == Globals.curUri) { //If the song's currently being played, let's stop it and have the bar slide out
                                    if (temp != null)
                                        temp.stop();
                                    tabPlayPause.setImageDrawable(getResources().getDrawable(R.drawable.play_ring));
                                    temp = null;
                                    respecTab(null);
                                }
                                Globals.playlistArray.remove(i);
                            }
                            playlistAdapter.notifyDataSetChanged();
                            mode.finish();
                        }
                        return true;
                    default:
                        return false;
                }
            }

            @Override
            public void onDestroyActionMode(android.view.ActionMode mode) {
                //Do Something
            }
        });

        tabPlayPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (temp==null) { //If first play, then prepare things
                    temp = new MediaPlayer();
                    temp.setAudioStreamType(AudioManager.STREAM_MUSIC);
                    try{
                        temp.setDataSource(getActivity().getApplicationContext(), Globals.curUri);
                        temp.prepare(); } catch(Exception e){e.printStackTrace();}
                }
                if (!temp.isPlaying()) { //If it's not playing, then start playing
//                    temp.start();
                    startMusicService();
                    tabPlayPause.setImageDrawable(getResources().getDrawable(R.drawable.pause_ring));
                }
                else{ //If it's playing, then pause it.
                    temp.pause();
                    tabPlayPause.setImageDrawable(getResources().getDrawable(R.drawable.play_ring));
                }
            }
        });

        //Listeners
        groupTitle.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId==6) {
                    groupTitle.clearFocus();
                    InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(
                            Context.INPUT_METHOD_SERVICE);
                    //txtName is a reference of an EditText Field
                    imm.hideSoftInputFromWindow(groupTitle.getWindowToken(), 0);
                }
                return false;
            }
        });
        addSong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickNewSong();
//                Globals.playlistArray.add(new SongItem(generateID(),generateID(),500,null));
//                playlistAdapter.notifyDataSetChanged();
//                Intent intent = new Intent();
//                intent.setAction(android.content.Intent.ACTION_PICK);
//                intent.setData(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
//                getActivity().startActivityForResult(intent, Globals.PICKSONG);
            }
        });

        //Configure things depending on whether you're host or not
        if (Globals.role==1) { //1 == Client
            groupTitle.setFocusable(false);
            startMusicService();
        }




        return v;
    }

    void respecTab(SongItem s){
        if (s == null && tabLayout.getVisibility()!=View.INVISIBLE){
            YoYo.with(Techniques.SlideOutDown).duration(500).withListener(new Animator.AnimatorListener() {
                @Override public void onAnimationStart(Animator animation) {}

                @Override
                public void onAnimationEnd(Animator animation) {
                    tabLayout.setVisibility(View.INVISIBLE);
                }

                @Override public void onAnimationCancel(Animator animation) {}
                @Override public void onAnimationRepeat(Animator animation) {}
            }).playOn(tabLayout);
        }
        else {
            tabTitle.setText(s.getName());
            tabArtist.setText(s.getArtist());
            Globals.curUri = s.getUri();
            if (tabLayout.getVisibility() == View.INVISIBLE) {
                tabLayout.setVisibility(View.VISIBLE);
                YoYo.with(Techniques.SlideInUp).duration(500).playOn(tabLayout);
            }
        }
    }

    public void pickNewSong(){
        Intent i = new Intent(getActivity(), FilePickerActivity.class);
        // This works if you defined the intent filter
        // Intent i = new Intent(Intent.ACTION_GET_CONTENT);

        // Set these depending on your use case. These are the defaults.
        i.putExtra(FilePickerActivity.EXTRA_ALLOW_MULTIPLE, false);
        i.putExtra(FilePickerActivity.EXTRA_ALLOW_CREATE_DIR, false);
        i.putExtra(FilePickerActivity.EXTRA_MODE, FilePickerActivity.MODE_FILE);
        i.setData(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
//        i.setAction(android.content.Intent.ACTION_PICK);


        // Configure initial directory by specifying a String.
        // You could specify a String like "/storage/emulated/0/", but that can
        // dangerous. Always use Android's API calls to get paths to the SD-card or
        // internal memory.
        i.putExtra(FilePickerActivity.EXTRA_START_PATH, Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC));

        getActivity().startActivityForResult(i, Globals.FILE_CODE);

    }

    public void startMusicService(){
        Intent intent = new Intent(getActivity(), MusicService.class);
        ArrayList<String> musicFiles = new ArrayList<>();
        for(int i = 0; i < Globals.playlistArray.size(); i++){
            musicFiles.add(Globals.playlistArray.get(i).getUri().getPath());
        }
        intent.putStringArrayListExtra("musicFiles", musicFiles);
        getActivity().startService(intent);
    }

    public class SongAdapter extends ArrayAdapter<SongItem> {
        public SongAdapter(ArrayList<SongItem> songItems){
            super (getActivity(),0,songItems);
        }
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            convertView = getActivity().getLayoutInflater().inflate(R.layout.unit_song, null);

            //Set up textview references so we can access them later
            TextView nameView, artistView;
            nameView = (TextView)convertView.findViewById(R.id.unit_name);
            artistView = (TextView)convertView.findViewById(R.id.unit_artist);

            //Make them all use Avenir
            nameView.setTypeface(Avenir_Light);
            artistView.setTypeface(Avenir_Light);

            //Get the current song item
            SongItem current = getItem(position);

            //Make them show the right strings
            nameView.setText(current.getName());
            artistView.setText(current.getArtist());

            convertView.setTag(current.getID());

            return convertView;
        }
    }
}
