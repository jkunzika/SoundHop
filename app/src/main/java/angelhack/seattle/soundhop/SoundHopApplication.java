package angelhack.seattle.soundhop;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseFacebookUtils;

/**
 * Created by Jordan on 6/27/15.
 */
public class SoundHopApplication extends Application {

    @Override
    public void onCreate(){
        Parse.initialize(this, "kv7TVEAgsGcUW0lwDIls8EP42aZHEBlmjaUsPDNM", "lcsVKA4dDq16sVPzGG0JALdUOmlE0xx5xdEQtC1q");

        Parse.setLogLevel(Parse.LOG_LEVEL_DEBUG);

        ParseFacebookUtils.initialize(this);

    }
}
