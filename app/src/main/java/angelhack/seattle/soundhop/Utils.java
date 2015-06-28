package angelhack.seattle.soundhop;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.util.Log;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by Jordan on 6/27/15.
 */
public class Utils {

    public static Context mContext;

    protected static Context getContext() {
        return mContext;
    }

    protected static void setContext(Context c) {
        mContext = c;
    }

    protected static void getFacebookProfilePicture() {
        Bundle params = new Bundle();
        params.putBoolean("redirect", false);
        params.putString("height", "400");
        params.putString("width", "400");
        params.putString("type", "large");
//        params.putString("width", "200");
        /* make the API call */

        new GraphRequest(
                AccessToken.getCurrentAccessToken(),
                "/me/picture",
                params,
                HttpMethod.GET,
                new GraphRequest.Callback() {
                    public void onCompleted(GraphResponse response) {
            /* handle the result */
                        JSONObject profilePicObj = response.getJSONObject();
                        try {
                            Log.i("ProfilePictureObject", profilePicObj.toString());
                            String picUrl = profilePicObj.getJSONObject("data").getString("url");
//                            ParseUser.getCurrentUser().put("profilePic", picUrl);
                            if (ParseUser.getCurrentUser().getParseFile("profilePicture") == null) {
                                new SaveFacebookProfilePicToParseTask().execute(picUrl);
                            } else {
                                Log.i("ProfilePictureObject", "Profile picture already exists in user model.");
                            }
                        } catch (NullPointerException e) {
                            e.printStackTrace();
                        } catch(JSONException e1){
                            e1.printStackTrace();
                        }//

                    }
                }
        ).executeAsync();

    }

    public static String getIPAddress(Context c) {
        WifiManager wifiMan = (WifiManager) c.getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInf = wifiMan.getConnectionInfo();
        int ipAddress = wifiInf.getIpAddress();

        return String.format("%d.%d.%d.%d", (ipAddress & 0xff), (ipAddress >> 8 & 0xff), (ipAddress >> 16 & 0xff), (ipAddress >> 24 & 0xff));
    }

    public static String getIPAddress() {
        if (mContext != null) {
            WifiManager wifiMan = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
            WifiInfo wifiInf = wifiMan.getConnectionInfo();
            int ipAddress = wifiInf.getIpAddress();

            return String.format("%d.%d.%d.%d", (ipAddress & 0xff), (ipAddress >> 8 & 0xff), (ipAddress >> 16 & 0xff), (ipAddress >> 24 & 0xff));

        }
        return null;
    }

    public static void saveIPAddress(){
        ParseUser currentUser = ParseUser.getCurrentUser();
        if(currentUser != null){
            currentUser.put("IP", getIPAddress());
            currentUser.saveInBackground();
        }
    }

    public static Uri getAudioContentUri(Uri audioUri) {
        File audioFile = new File(audioUri.getPath());
        String filePath = audioFile.getAbsolutePath();
        Cursor cursor = getContext().getContentResolver().query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.Audio.Media._ID},
                MediaStore.Images.Media.DATA + "=? ",
                new String[]{filePath}, null);
        if (cursor != null && cursor.moveToFirst()) {
            int id = cursor.getInt(cursor.getColumnIndex(MediaStore.MediaColumns._ID));
            return Uri.withAppendedPath(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, "" + id);
        } else {
            if (audioFile.exists()) {
                ContentValues values = new ContentValues();
                values.put(MediaStore.Audio.Media.DATA, filePath);
                return getContext().getContentResolver().insert(
                        MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, values);
            } else {
                return null;
            }
        }
    }

    public static class SaveFacebookProfilePicToParseTask extends AsyncTask<String, Void, Uri> {

        @Nullable
        @Override
        public Uri doInBackground(String... params) {
//        // Specify image size
            try {
                Log.i("SaveFBPicToParseTask", "About to process profile pic in async task!");
                URL wallpaperURL = new URL(params[0]);
                URLConnection connection = wallpaperURL.openConnection();
                InputStream inputStream = new BufferedInputStream(wallpaperURL.openStream(), 10240);
                File cacheDir = getCacheFolder(getContext());
                File cacheFile = new File(cacheDir, "SoundHopProfilePic.jpg");
                FileOutputStream outputStream = new FileOutputStream(cacheFile);

                byte buffer[] = new byte[1024];
                int dataSize;
                int loadedSize = 0;
                Log.i("SaveFBPicToParseTask", "About to read data for inputStream in async task!");

                while ((dataSize = inputStream.read(buffer)) != -1) {
                    loadedSize += dataSize;
//                    publishProgress(loadedSize);
                    outputStream.write(buffer, 0, dataSize);
                }
                Log.i("SaveFBPicToParseTask", "Done reading data for inputStream in async task!");

                outputStream.close();
                return Uri.fromFile(cacheFile);
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        public static byte[] convertFileToByteArray(File f) {
            byte[] byteArray = null;
            try {
                InputStream inputStream = new FileInputStream(f);
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                byte[] b = new byte[1024 * 8];
                int bytesRead = 0;

                while ((bytesRead = inputStream.read(b)) != -1) {
                    bos.write(b, 0, bytesRead);
                }

                byteArray = bos.toByteArray();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return byteArray;
        }


        protected void onPostExecute(Uri source) {
            Log.i("SaveFBPicToParseTask", "About to save profile pic in async task!");

            File f = new File(source.getPath());
            byte[] data = convertFileToByteArray(f);
            ParseFile profilePicFile = new ParseFile("SoundHopProfilePic.jpg", data);
            ParseUser.getCurrentUser().put("profilePicture", profilePicFile);
            ParseUser.getCurrentUser().saveInBackground(new SaveCallback() {
                @Override
                public void done(@Nullable com.parse.ParseException e) {
                    if (e == null) {
                        Log.i("ProfilePicture", "Successfully saved profile picture!");
                    } else {
                        Log.e("ProfilePicture", "Saving Profile Picture failed: "
                                + e.getCode() + ", " + e.getMessage());
                    }
                }
            });

        }


        @Nullable
        public File getCacheFolder(Context context) {
            File cacheDir = null;
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                cacheDir = new File(Environment.getExternalStorageDirectory(), "cachefolder");
                if (!cacheDir.isDirectory()) {
                    cacheDir.mkdirs();
                }
            }

            if (cacheDir != null && !cacheDir.isDirectory()) {
                cacheDir = context.getCacheDir(); //get system cache folder
            }

            return cacheDir;
        }

        @Nullable
        public File getDataFolder(Context context) {
            File dataDir = null;
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                dataDir = new File(Environment.getExternalStorageDirectory(), "baeAppData");
                if (!dataDir.isDirectory()) {
                    dataDir.mkdirs();
                }
            }

            if (dataDir != null && !dataDir.isDirectory()) {
                dataDir = context.getFilesDir();
            }

            return dataDir;
        }

    }

}
