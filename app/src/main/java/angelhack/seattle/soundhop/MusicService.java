package angelhack.seattle.soundhop;

import android.app.Service;
import android.content.Intent;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;

/**
 * Created by Jordan on 6/27/15.
 */
public class MusicService extends Service {

//    private Button receiveButton, stopButton;

    private AudioTrack speaker;

    public byte[] buffer;
    public static DatagramSocket socket;
    private int port=50005;         //which port??
    AudioRecord recorder;

    //Audio Configuration.
    private int sampleRate = 44100;      //How much will be ideal?
    private int channelConfig = AudioFormat.CHANNEL_CONFIGURATION_MONO;
    private int audioFormat = AudioFormat.ENCODING_PCM_16BIT;

    private boolean status = true;

    private final IBinder mBinder = new MyoBinder();

    public class MyoBinder extends Binder {
        MusicService getService() {
            return MusicService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public ArrayList<String> musicFiles = null;


    @Override
    public void onCreate() {
        super.onCreate();

//        setContentView(R.layout.main);
//
//        receiveButton = (Button) findViewById(R.id.receive_button);
//        stopButton = (Button) findViewById(R.id.stop_button);
//        findViewById(R.id.receive_label);
//
//        receiveButton.setOnClickListener(receiveListener);
//        stopButton.setOnClickListener(stopListener);

    }

    public void stop(){
        status = false;
        speaker.release();
        Log.d("VR", "Speaker released");
    }

    public void receive(){
        status = true;
        startReceiving();

    }


//    private final OnClickListener stopListener = new OnClickListener() {
//
//        @Override
//        public void onClick(View v) {
//            status = false;
//            speaker.release();
//            Log.d("VR", "Speaker released");
//
//        }
//
//    };
//
//
//    private final OnClickListener receiveListener = new OnClickListener() {
//
//        @Override
//        public void onClick(View arg0) {
//            status = true;
//            startReceiving();
//
//        }
//
//    };

    public void startReceiving() {

        Thread receiveThread = new Thread(new Runnable() {

            @Override
            public void run() {

                try {

                    DatagramSocket socket = new DatagramSocket(50005);
                    Log.d("VR", "Socket Created");


                    byte[] buffer = new byte[256];


                    //minimum buffer size. need to be careful. might cause problems. try setting manually if any problems faced
                    int minBufSize = AudioRecord.getMinBufferSize(sampleRate, channelConfig, audioFormat);

                    speaker = new AudioTrack(AudioManager.STREAM_MUSIC, sampleRate, channelConfig, audioFormat, minBufSize, AudioTrack.MODE_STREAM);

                    speaker.play();

                    while (status == true) {
                        try {


                            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                            socket.receive(packet);
                            Log.d("VR", "Packet Received");

                            //reading content from packet
                            buffer = packet.getData();
                            Log.d("VR", "Packet data read into buffer");

                            //sending data to the Audiotrack obj i.e. speaker
                            speaker.write(buffer, 0, minBufSize);
                            Log.d("VR", "Writing buffer content to speaker");

                        } catch (IOException e) {
                            Log.e("VR", "IOException");
                        }
                    }


                } catch (SocketException e) {
                    Log.e("VR", "SocketException");
                }


            }

        });
        receiveThread.start();
    }

    public void startStreaming(final ArrayList<String> ipArray) {


        Thread streamThread = new Thread(new Runnable() {

            @Override
            public void run() {
                try {


                    int minBufSize = AudioRecord.getMinBufferSize(sampleRate, channelConfig, audioFormat);
                    DatagramSocket socket = new DatagramSocket();
                    Log.d("VS", "Socket Created");

                    byte[] buffer = new byte[minBufSize];

                    Log.d("VS","Buffer created of size " + minBufSize);
                    DatagramPacket packet;

                    final InetAddress destination = InetAddress.getByName(ipArray.get(0));
                    Log.d("VS", "Address retrieved: " + destination.toString());


//                    recorder = new AudioRecord(MediaRecorder.AudioSource.MIC,sampleRate,channelConfig,audioFormat,minBufSize);
//                    Log.d("VS", "Recorder initialized");

//                    recorder.startRecording();

                    if(Globals.playlistArray != null && Globals.playlistArray.size() > 0) {
                        String path = Utils.getPath(Globals.playlistArray.get(0).getUri());
                        if(path != null) {
                            InputStream is = new FileInputStream(new File(path));
                            ByteArrayOutputStream bos = new ByteArrayOutputStream();
//                        byte[] b = new byte[1024];
                            int bytesRead;
                            while ((bytesRead = is.read(buffer)) != -1 && status) {
                                bos.write(buffer, 0, buffer.length);
                                minBufSize = bytesRead;

//                            minBufSize = recorder.read(buffer, 0, buffer.length);
//
//                        //putting buffer in the packet
                                packet = new DatagramPacket(buffer, buffer.length, destination, port);
//
                                socket.send(packet);

                            }
                        }
                    }


//                    while(status == true) {
//
//
//                        //reading data from MIC into buffer
//                        minBufSize = recorder.read(buffer, 0, buffer.length);
//
//                        //putting buffer in the packet
//                        packet = new DatagramPacket (buffer,buffer.length,destination,port);
//
//                        socket.send(packet);
//
//
//                    }
//


                } catch(UnknownHostException e) {
                    Log.e("VS", "UnknownHostException");
                } catch (IOException e) {
                    Log.e("VS", "IOException");
                }


            }

        });
        streamThread.start();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("MusicService", "onStartCommand");
        musicFiles = intent.getStringArrayListExtra("musicFiles");
        ArrayList<String> ipArray = new ArrayList<>();
        ipArray.add("192.168.144.211");
        if(Globals.role == 0){
            startStreaming(ipArray);
        } else {
            startReceiving();
        }
        // We want this service to continue running until it is explicitly
        // stopped, so return sticky.
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}

