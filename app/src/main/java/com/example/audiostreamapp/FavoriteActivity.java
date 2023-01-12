package com.example.audiostreamapp;

import static android.app.PendingIntent.getActivity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import com.example.audiostreamapp.ui.home.AudioFileAdapter;

public class FavoriteActivity extends AppCompatActivity {
    RecyclerView favList;
    SongAdapter songAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite);
        favList = findViewById(R.id.recycleView);

//
//        RecyclerView favList = (RecyclerView) this.getView().findViewById(R.id.recycleView);
//        Activity currentActivity=this.getActivity();

        songAdapter = new SongAdapter(MainActivity.favList);

        favList.setAdapter(songAdapter);

        favList.setLayoutManager(new LinearLayoutManager(this));
        }

        public void removeAll(View view)
        {
            MainActivity.favList.clear();
            songAdapter.notifyDataSetChanged();
        }

    public void removeALl(View view) {
        MainActivity.favList.clear();
        songAdapter.notifyDataSetChanged();
    }
}
