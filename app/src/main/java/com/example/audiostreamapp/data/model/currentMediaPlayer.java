package com.example.audiostreamapp.data.model;


import android.app.Activity;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.audiostreamapp.MainActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class currentMediaPlayer {
    private static MediaPlayer mediaPlayerOnPlay;
    private static String mediaName;
    private static MainActivity mainActivity;
    //public static int listPosition=0;
    public static boolean fromList=false;
    public static void reset(){
        mediaPlayerOnPlay.reset();
    }

    public static void create(Activity activity, int i){
        mediaPlayerOnPlay=MediaPlayer.create(activity,i);
        mediaName="Welcome.mp3";
    }

    public static void setMainActivity(MainActivity mainActivitys){
        mainActivity=mainActivitys;
    }

    public static MainActivity getMainActivity(){
        return mainActivity;
    }

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


    public static String getMediaName() {
        return mediaName;
    }

    private static boolean prepared;
    public static boolean changeMedia(String newSongName) {
        StorageReference storageRef = FirebaseStorage.getInstance().getReference().child("musicRepo/"+newSongName);
        prepared=false;
        storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        // Download url of file
                        String url = uri.toString();
                        Log.e("URL",url);

                        currentMediaPlayer.setMediaPlayerURL(url,(String) newSongName);

                        currentMediaPlayer.getMediaPlayer().setOnPreparedListener(new MediaPlayer.OnPreparedListener() {

                            @Override
                            public void onPrepared(MediaPlayer mp) {

                                mp.start();

                                try {
                                    TimeUnit.MILLISECONDS.sleep(10);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                mainActivity.resetDurationOfAudioPlayer();
                                prepared=true;

                            }
                        });
                    }
                })

                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.i("TAG", e.getMessage());
                    }
                });

        try {
            TimeUnit.MILLISECONDS.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return prepared;
    }
    public static Boolean changeToMedia(String newSongName) {
        StorageReference storageRef = FirebaseStorage.getInstance().getReference().child("musicRepo/"+newSongName);
        storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        // Download url of file
                        String url = uri.toString();
                        Log.e("URL",url);
                        currentMediaPlayer.setDataSource(url,(String) newSongName);
                    }
                })

                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.i("TAG", e.getMessage());
                    }
                });
        return false;
    }

    private static void setDataSource(String url, String newSongName) {
        mediaPlayerOnPlay.reset();
        try {
            mediaPlayerOnPlay.setDataSource(url);
//            mediaPlayerOnPlay.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // wait for media player to get prepare
        setName(newSongName);

    }



    public static boolean isFromList() {
        return fromList;
    }

    /*public static void nextFromList(int listPosition){
        changeMedia(MainActivity.favList.get(listPosition).getName());

        currentMediaPlayer.listPosition++;
    }*/

}
