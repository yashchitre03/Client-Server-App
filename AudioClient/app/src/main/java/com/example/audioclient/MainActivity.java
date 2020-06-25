package com.example.audioclient;


import android.app.Activity;
import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ResolveInfo;
import android.os.Build;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.example.Intfaidl.*;
import java.util.List;


public class MainActivity extends Activity {

    protected  IMyAidlInterface AidlInterface;
    private Button startServiceBtn;
    private Button stopServiceBtn;
    private Button resumeBtn;
    private Button pauseBtn;
    private Button stopBtn;
    private List<String> songTitles;
    private ListView listView;
    ArrayAdapter<String> arrayAdapter;
    boolean playing = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        startServiceBtn = findViewById(R.id.button1);
        resumeBtn = findViewById(R.id.button3);
        pauseBtn = findViewById(R.id.button2);
        stopBtn = findViewById(R.id.button4);
        stopServiceBtn = findViewById(R.id.button5);
        listView = findViewById(R.id.songsList);
        resumeBtn.setEnabled(false);
        pauseBtn.setEnabled(false);
        stopBtn.setEnabled(false);
        stopServiceBtn.setEnabled(false);


        // start service button is used to bind to the music service
        startServiceBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(IMyAidlInterface.class.getName());
                ResolveInfo info = getPackageManager().resolveService(intent, 0);
                intent.setComponent(new ComponentName(info.serviceInfo.packageName, info.serviceInfo.name));

                bindService(intent, serviceConnection, Service.BIND_AUTO_CREATE);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    startForegroundService(intent);
                }
                else {
                    startService(intent);
                }
                stopServiceBtn.setEnabled(true);
                listView.setVisibility(View.VISIBLE);
            }
        });


        // play song via AIDL after selecting a song from the list
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                pauseBtn.setEnabled(true);
                resumeBtn.setEnabled(true);
                stopBtn.setEnabled(true);
                if(AidlInterface != null){
                    try {
                        AidlInterface.play(position);
                        playing = true;
                    }catch (RemoteException e){
                        e.printStackTrace();
                    }
                }
            }
        });


        // pause song through AIDL
        pauseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if(playing) {
                        AidlInterface.pause();
                        playing = false;
                    }
                    else {
                        Toast.makeText(MainActivity.this, "Song already paused!", Toast.LENGTH_SHORT).show();
                    }
                }catch(RemoteException e){
                    e.printStackTrace();
                }
            }
        });


        // resume playing from AIDL
        resumeBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                try {
                    if(playing) {
                        Toast.makeText(MainActivity.this, "Song already playing!", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        AidlInterface.resume();
                        playing = true;
                    }
                } catch (RemoteException e){
                    e.printStackTrace();
                }
            }
        });


        // stop player from AIDL
        stopBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pauseBtn.setEnabled(false);
                resumeBtn.setEnabled(false);
                try {
                    AidlInterface.stop();
                }catch(RemoteException e){
                    e.printStackTrace();
                }
                stopBtn.setEnabled(false);
            }
        });


        // stop service button is used to unbind from the music player service
        stopServiceBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(IMyAidlInterface.class.getName());
                ResolveInfo info = getPackageManager().resolveService(intent, 0);
                intent.setComponent(new ComponentName(info.serviceInfo.packageName, info.serviceInfo.name));
                unbindService(serviceConnection);
                listView.setVisibility(View.INVISIBLE);
                pauseBtn.setEnabled(false);
                resumeBtn.setEnabled(false);
                stopBtn.setEnabled(false);
                stopService(intent);
                stopServiceBtn.setEnabled(false);
            }
        });
    }


    // add songs to listView obtained from clip server
    private final ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder iBinder) {
            AidlInterface = IMyAidlInterface.Stub.asInterface(iBinder);
            try{
                songTitles = AidlInterface.songs(); // get songs from AIDL service
                arrayAdapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1, songTitles);
                ListView listView = findViewById(R.id.songsList);
                listView.setAdapter(arrayAdapter);
            }catch (RemoteException e){
                e.printStackTrace();
            }
        }
        @Override
        public void onServiceDisconnected(ComponentName name) {
            AidlInterface = null;
        }
    };


    //Unbind Service while destroying the activity
    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(serviceConnection);
    }
}