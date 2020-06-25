// IMyAidlInterface.aidl
package com.example.Intfaidl;

interface IMyAidlInterface {
     void play(int id);
     void pause();
     void stop();
     List<String> songs();
     void resume();
}
