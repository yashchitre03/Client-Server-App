package com.example.clipserver;


import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.example.Intfaidl.IMyAidlInterface;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class ClipService extends Service {
    private MediaPlayer mPlayer;
    private static final int NOTIFICATION_ID = 1;
    ArrayList<Integer> songs = new ArrayList<>();
    private Notification notification ;
    private static String CHANNEL_ID = "Music player style" ;


    @Override
    public void onCreate() {
        super.onCreate();
        songs.add(R.raw.allthat);
        songs.add(R.raw.badass);
        songs.add(R.raw.moose);
        songs.add(R.raw.endlessmotion);
        songs.add(R.raw.dubstep);

        if (mPlayer != null) {
            mPlayer.setLooping(false);
            mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    stopSelf();
                    NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
                    notificationManager.cancelAll();
                }
            });
        }

        this.createNotificationChannel();
        final Intent notificationIntent = new Intent(getApplicationContext(), IMyAidlInterface.class);
        final PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                notificationIntent, 0) ;

        notification = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                        .setSmallIcon(android.R.drawable.ic_media_play)
                        .setOngoing(true).setContentTitle("Music Player")
                        .setContentText("Use client app to change music")
                        .setTicker("Song's playing!")
                        .setFullScreenIntent(pendingIntent, false)
                        .build();

        startForeground(NOTIFICATION_ID, notification);
    }


    private void createNotificationChannel() {
        // creates the Notification Channel
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Music player notification";
            String description = "Music player notification channel";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startid) {

        return START_NOT_STICKY;
    }


    @Override
    public void onRebind(Intent intent) {

        super.onRebind(intent);
    }


    @Override
    public boolean onUnbind(Intent intent) {

        return true;
    }


    @Override
    public void onDestroy() {
        if (mPlayer != null) {
            mPlayer.stop();
            mPlayer.release();
        }
    }


    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }


    // AIDL
    private final IMyAidlInterface.Stub mBinder = new IMyAidlInterface.Stub() {

        @Override
        public void stop() {
            mPlayer.stop();
            mPlayer = null;
        }

        @Override
        public List<String> songs() {
            return Arrays.asList(getResources().getStringArray(R.array.name));
        }

        @Override
        public void pause() {
            mPlayer.pause();
        }

        @Override
        public void play(int id) {
            if (mPlayer != null) {
                mPlayer.stop();
            }
            mPlayer = MediaPlayer.create(getApplicationContext(), songs.get(id));
            mPlayer.start();
        }

        @Override
        public void resume() {
                if (mPlayer.isPlaying()) {
                    mPlayer.pause();
                } else {
                    mPlayer.start();
                }
        }
    };
}
