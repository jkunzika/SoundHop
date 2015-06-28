package angelhack.seattle.soundhop;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.parse.ParseFacebookUtils;
import com.parse.ui.ParseLoginBuilder;


public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Utils.setContext(this);
        ParseLoginBuilder builder = new ParseLoginBuilder(MainActivity.this);
        getSupportActionBar().hide();
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
            } else if(resultCode == RESULT_CANCELED){
                Log.e("FBLOGIN", "login failed");
            }
        }
        ParseFacebookUtils.onActivityResult(requestCode, resultCode, data);
    }
}
