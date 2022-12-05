package com.example.audiostreamapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.audiostreamapp.data.model.currentMediaPlayer;
import com.example.audiostreamapp.databinding.ActivityMainBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.concurrent.TimeUnit;

public class LiveRoomActivity extends AppCompatActivity {
    TextView playerPosition,playerDuration;
    SeekBar seekBar;
    ImageView btRew,btPlay,btPause,btFf;
    Handler handler = new Handler();
    Runnable runnable;
    MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_room);
        playerPosition = findViewById(R.id.player_position);
        playerDuration = findViewById(R.id.player_duration);
        seekBar = findViewById(R.id.seek_bar);
        btRew = findViewById(R.id.bt_rew);
        btPlay = findViewById(R.id.bt_play);
        btPause = findViewById(R.id.bt_pause);
        btFf = findViewById(R.id.bt_ff);
        runnable = new Runnable() {
            @Override
            public void run() {
                //set progress on seek bar
                seekBar.setProgress(mediaPlayer.getCurrentPosition());
                //handler post delay for 0.5 second
                handler.postDelayed(this,30);
            }
        };
        //Get init Status of Media Player
        mediaPlayer = currentMediaPlayer.getMediaPlayer();
        if (mediaPlayer.isPlaying())
        {
            btPlay.setVisibility(View.GONE);
            btPause.setVisibility(View.VISIBLE);
            //Start media player
            handler.postDelayed(runnable,0);
        }
        seekBar.setMax(mediaPlayer.getDuration());

        //Get duration
        int duration = mediaPlayer.getDuration();
        //Convert millisecond to minute and second
        String sDuration = convertFormat(duration);
        Log.e("Duration",sDuration);
        //Set duration on text view
        playerDuration.setText(sDuration);
        playerPosition.setText(convertFormat(mediaPlayer.getCurrentPosition()));
        btPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Hide play button and show pause button
                btPlay.setVisibility(View.GONE);
                btPause.setVisibility(View.VISIBLE);
                //Start media player
                mediaPlayer.start();
                seekBar.setMax(mediaPlayer.getDuration());
                handler.postDelayed(runnable,0);
            }
        });


        btPause.setOnClickListener(new View.OnClickListener() {
                                       @Override
                                       public void onClick(View view) {
                                           btPause.setVisibility(View.GONE);
                                           btPlay.setVisibility(View.VISIBLE);
                                           mediaPlayer.pause();
                                           handler.removeCallbacks(runnable);
                                       }
                                   }
        );

        btFf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int currentPosition = mediaPlayer.getCurrentPosition();
                int duration = mediaPlayer.getDuration();
                if(mediaPlayer.isPlaying() && duration != currentPosition){
                    currentPosition = currentPosition + 5000;
                    playerPosition.setText(convertFormat(currentPosition));
                    mediaPlayer.seekTo(currentPosition);
                }
            }
        });

        btRew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int currentPosition = mediaPlayer.getCurrentPosition();
                int duration = mediaPlayer.getDuration();
                if(mediaPlayer.isPlaying() && currentPosition > 5000){
                    currentPosition = currentPosition - 5000;
                    playerPosition.setText(convertFormat(currentPosition));
                    mediaPlayer.seekTo(currentPosition);
                }
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(fromUser){
                    mediaPlayer.seekTo(progress);
                }
                playerPosition.setText(convertFormat(mediaPlayer.getCurrentPosition()));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                btPause.setVisibility(View.GONE);
                btPlay.setVisibility(View.VISIBLE);
                mediaPlayer.seekTo(0);
            }
        });
    }

    private String convertFormat(int duration) {
        return String.format("%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(duration),
                TimeUnit.MILLISECONDS.toSeconds(duration) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(duration)));
    }
}