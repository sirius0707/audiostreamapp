package com.example.audiostreamapp.data.model;

import android.app.Activity;
import android.media.MediaPlayer;

import com.example.audiostreamapp.MainActivity;

import java.io.IOException;

public class currentMediaPlayer {
    private static MediaPlayer mediaPlayerOnPlay;
    private static String mediaName;
    public static void reset(){
        mediaPlayerOnPlay.reset();
    }

    public static void create(Activity activity, int i){
        mediaPlayerOnPlay=MediaPlayer.create(activity,i);
        mediaName="Welcome.mp3";
    };

    public static MediaPlayer getMediaPlayer(){
        return mediaPlayerOnPlay;
    }

    public static void setName(String newName){
        mediaName=newName;
    }
    public static void setMediaPlayerURL(String url,String newName){
        mediaPlayerOnPlay.reset();
        try {
            mediaPlayerOnPlay.setDataSource(url);
            mediaPlayerOnPlay.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // wait for media player to get prepare
        setName(newName);
    }


}
