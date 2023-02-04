package com.example.audiostreamapp.data.model;


import android.app.Activity;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.audiostreamapp.MainActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class currentMediaPlayer {
    private static MediaPlayer mediaPlayerOnPlay;
    private static String mediaName;
    private static String currentRepoName;
    private static MainActivity mainActivity;
    //public static int listPosition=0;
    public static boolean fromList=false;
    public static void reset(){
        mediaPlayerOnPlay.reset();
    }

    public static void create(Activity activity, int i){
        mediaPlayerOnPlay=MediaPlayer.create(activity,i);
        mediaName="Welcome.mp3";
        currentRepoName="musicRepo";
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

    public static String getRepoName() {
        return currentRepoName;
    }
    public static String getMediaName() {
        return mediaName;
    }
    private static boolean prepared;
    public static boolean changeMedia(String repoName,String newSongName) {
        String lastSong = getMediaName().
                replace(".mp3", "");

        StorageReference storageRef = FirebaseStorage.getInstance().getReference().child(repoName+"/"+newSongName);

        prepared=false;
        storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        // Download url of file
                        String url = uri.toString();
                        Log.e("URL",url);

                        DatabaseReference mDatabase  = FirebaseDatabase.getInstance("https://audiostreamapp-6a52b-default-rtdb.europe-west1.firebasedatabase.app/").getReference().child("recommend/"+ lastSong);
                        currentMediaPlayer.setMediaPlayerURL(url,(String) newSongName);
                        currentRepoName=repoName;
                        Map<String, Object> updates = new HashMap<>();
                        updates.put(getMediaName().
                                        replace(".mp3", ""),
                                ServerValue.increment(1));
                        mDatabase.updateChildren(updates);
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
