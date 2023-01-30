package com.example.audiostreamapp;

import static android.app.PendingIntent.getActivity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.audiostreamapp.ui.home.AudioFile;
import com.example.audiostreamapp.ui.home.AudioFileAdapter;

import java.util.ArrayList;
import java.util.Objects;

public class FavoriteActivity extends AppCompatActivity {
    RecyclerView favList;
    SongAdapter songAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_favorite);
        favList = findViewById(R.id.recycleView);
        setTitle("Playlist");

        songAdapter = new SongAdapter(MainActivity.favList);

        favList.setAdapter(songAdapter);

        favList.setLayoutManager(new LinearLayoutManager(this));
        }

    public void removeALl(View view) {
        MainActivity.favList.clear();
        songAdapter.notifyDataSetChanged();
    }



}
