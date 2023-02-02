package com.example.audiostreamapp;

import android.app.Activity;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.audiostreamapp.ui.home.HomeFragment;

public class SyncRoomAudioChooseActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sync_room_audio_choose);
//        if (savedInstanceState == null) {
//            getSupportFragmentManager().beginTransaction()
//                    .replace(R.id.container, HomeFragment.newInstance())
//                    .commitNow();
//        }

    }
}
