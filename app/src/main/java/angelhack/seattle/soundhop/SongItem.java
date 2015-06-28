package angelhack.seattle.soundhop;

import android.net.Uri;

import java.util.UUID;

/**
 * Created by devanshk on 6/27/15.
 */
public class SongItem {
    private String name;
    private String artist;
    private long duration;
    private UUID id;
    private Uri uri;

    public SongItem(String n, String a, long d, Uri u){
        this.name = n;
        this.artist = a;
        this.duration = d;
        this.id = UUID.randomUUID();
        this.uri = u;
    }

    public String getName() {
        return name;
    }

    public String getArtist() {
        return artist;
    }

    public long getDuration() {
        return duration;
    }

    public UUID getID() {
        return id;
    }

    public Uri getUri(){ return uri;}
}