package com.example.audiostreamapp;


import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.audiostreamapp.data.model.Message;
import com.example.audiostreamapp.data.model.currentMediaPlayer;
import com.example.audiostreamapp.ui.dashboard.DashboardFragment;
import com.example.audiostreamapp.ui.home.AudioFile;
import com.example.audiostreamapp.ui.home.notifications.NotificationsFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.audiostreamapp.data.model.currentMediaPlayer;
import com.example.audiostreamapp.databinding.ActivityMainBinding;
import com.example.audiostreamapp.ui.home.HomeFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    TextView playerPosition,playerDuration;
    SeekBar seekBar;
    ImageView btRew,btPlay,btPause,btFf,btPre,btNext,iv;
    ObjectAnimator mAnimator;
    MediaPlayer mediaPlayer;
    Handler handler = new Handler();
    Runnable runnable;
    public static TextView play_name;

    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private DatabaseReference mDatabase = FirebaseDatabase.getInstance("https://audiostreamapp-6a52b-default-rtdb.europe-west1.firebasedatabase.app/").getReference();

    private Activity currentActivity;
    private ActivityMainBinding binding;

    private String CHANNEL_ID = "ChannelID";
    int notificationId = 1;

    public static ArrayList<AudioFile> favList = new ArrayList<AudioFile>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        currentActivity = this;

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        BottomNavigationView navView = findViewById(R.id.nav_view);

        mDatabase = FirebaseDatabase.getInstance("https://audiostreamapp-6a52b-default-rtdb.europe-west1.firebasedatabase.app/").getReference();

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);

        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);

        currentMediaPlayer.setMainActivity(this);



    }

    @Override
    protected void onStart() {
        super.onStart();
        playerPosition = findViewById(R.id.player_position);
        playerDuration = findViewById(R.id.player_duration);
        seekBar = findViewById(R.id.seek_bar);
        btRew = findViewById(R.id.bt_rew);
        btPlay = findViewById(R.id.bt_play);
        btPause = findViewById(R.id.bt_pause);
        btFf = findViewById(R.id.bt_ff);
        btPre = findViewById(R.id.bt_pre);
        btNext = findViewById(R.id.bt_next);
        iv = findViewById(R.id.iv);
        play_name= findViewById(R.id.play_name);


        initAnimator();

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
        resetDurationOfAudioPlayer();
        presentCurrentName();

        //Get duration



        btPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Count play times of a song
                Map<String, Object> updates = new HashMap<>();
                if(HomeFragment.contentMode.getCheckedRadioButtonId() == R.id.musicBtn) {
                    updates.put("music/" + currentMediaPlayer.
                            getMediaName().
                            replace(".mp3", "") + "/playedTimes", ServerValue.increment(1));
                }else if (HomeFragment.contentMode.getCheckedRadioButtonId() == R.id.audiobookBtn) {
                    updates.put("audiobooks/" + currentMediaPlayer.
                            getMediaName().
                            replace(".mp3", "") + "/playedTimes", ServerValue.increment(1));
                }else {
                    Log.e("Storage error","Specified storage is not found");
                }
                mDatabase.updateChildren(updates);

                //Hide play button and show pause button
                btPlay.setVisibility(View.GONE);
                btPause.setVisibility(View.VISIBLE);
                //Start media player
                mAnimator.resume();
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
                mAnimator.pause();
                mediaPlayer.pause();
                handler.removeCallbacks(runnable);
            }
        });

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

        btPre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int pos = getAudioPos(currentMediaPlayer.getMediaName(),favList);

                if(currentMediaPlayer.isFromList() && pos!=0) {
                    pos=pos-1;
                    mediaPlayer.reset();
                    currentMediaPlayer.changeMedia("musicRepo",favList.get(pos).getName());
                    currentMediaPlayer.changeMedia("audioBooks",favList.get(pos).getName());
                    seekBar.setMax(mediaPlayer.getDuration());
                    play_name.setText(favList.get(pos).getName().replace(".mp3",""));
                }else{
                    mediaPlayer.seekTo(0);
                }

            }
        });

        btNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int pos = getAudioPos(currentMediaPlayer.getMediaName(),favList);
                if(!currentMediaPlayer.isFromList() && favList.size()==0 || pos==favList.size()-1){mediaPlayer.seekTo(0);}
                else if(currentMediaPlayer.isFromList() && pos!=favList.size() || !currentMediaPlayer.isFromList() && favList.size()!=0 ) {
                    pos=pos+1;
                    mediaPlayer.reset();
                    currentMediaPlayer.changeMedia("musicRepo",favList.get(pos).getName());
                    currentMediaPlayer.changeMedia("audioBooks",favList.get(pos).getName());
                    seekBar.setMax(mediaPlayer.getDuration());
                    play_name.setText(favList.get(pos).getName().replace(".mp3",""));
                }else{
                    mediaPlayer.seekTo(0);
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
                int pos=getAudioPos(currentMediaPlayer.getMediaName(),favList);
                if(currentMediaPlayer.isFromList() && pos!=favList.size()-1){
                    pos=pos+1;
                    currentMediaPlayer.changeMedia("musicRepo",favList.get(pos).getName());
                    currentMediaPlayer.changeMedia("audioBooks",favList.get(pos).getName());
                    currentMediaPlayer.fromList=true;
                }else if(!currentMediaPlayer.isFromList() && !favList.isEmpty()){
                    pos=0;
                    currentMediaPlayer.changeMedia("musicRepo",favList.get(pos).getName());
                    currentMediaPlayer.changeMedia("audioBooks",favList.get(pos).getName());
                    currentMediaPlayer.fromList=true;
                }
                //else if(currentMediaPlayer.isFromList() && currentMediaPlayer.listPosition) {}
                else{
                        lastSongEnd();

                    }
                }

        });



        LinearLayout audioPlayerLayout = (LinearLayout )findViewById(R.id.audioPlayerLayout);

        audioPlayerLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startLiveRoomActivity();
            }
        });

        // Get the latest status of Realtime Database
        mDatabase.child("message/" + user.getUid()).limitToLast(20).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                DataSnapshot ds = snapshot.child("latest message");
                if(user.getUid().equals(ds.child("Receiver").getValue().toString())) {
                    showSnackbar("You have a new message!");
                    createNotificationChannel();
                    Intent intent = new Intent(currentActivity, DisplayProfileActivity.class);
                    intent.putExtra("USERID", ds.child("Sender").getValue().toString());
                    PendingIntent pendingIntent = PendingIntent.getActivity(currentActivity, 0, intent, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);

                    NotificationCompat.Builder builder = new NotificationCompat.Builder(currentActivity, CHANNEL_ID)
                            .setSmallIcon(R.mipmap.ic_launcher)
                            .setContentTitle("New Message Received")
                            .setContentText(ds.child("Context").getValue().toString())
                            .setColor(Color.RED)
                            .setNumber(12)
                            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                            .setContentIntent(pendingIntent)
                            .setAutoCancel(true);
                    NotificationManagerCompat notificationManager = NotificationManagerCompat.from(currentActivity);

                    // notificationId is a unique int for each notification that you must define
                    notificationManager.notify(notificationId, builder.build());
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
//
    private void presentCurrentName() {
        if (mediaPlayer.isPlaying()) {
            play_name.setText(currentMediaPlayer.getMediaName().replace(".mp3", ""));
        }
    }

    private void initAnimator() {
        mAnimator = ObjectAnimator.ofFloat(iv,"rotation",0.0f,360.0f);
        mAnimator.setDuration(3000);//设定转一圈的时间
        mAnimator.setRepeatCount(Animation.INFINITE);//设定无限循环
        mAnimator.setRepeatMode(ObjectAnimator.RESTART);//循环模式
        mAnimator.setInterpolator(new LinearInterpolator());//匀速
        mAnimator.start();
        mAnimator.pause();
    }

    public void startLiveRoomActivity(){
        Intent liveRoom_intent = new Intent(currentActivity,LiveRoomActivity.class);
        startActivity(liveRoom_intent);
    }

    public void resetDurationOfAudioPlayer(){
        int duration = mediaPlayer.getDuration();
        //Convert millisecond to minute and second
        String sDuration = convertFormat(duration);
        seekBar.setMax(mediaPlayer.getDuration());
        if (mediaPlayer.isPlaying())
        {
            btPlay.setVisibility(View.GONE);
            btPause.setVisibility(View.VISIBLE);
            //Start media player
            handler.postDelayed(runnable,0);
        }
        seekBar.setMax(mediaPlayer.getDuration());
        playerDuration.setText(sDuration);
        playerPosition.setText(convertFormat(mediaPlayer.getCurrentPosition()));

    }

    private String convertFormat(int duration) {
        return String.format("%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(duration),
                TimeUnit.MILLISECONDS.toSeconds(duration) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(duration)));
    }

    // Show message
    public void showSnackbar(String errorMessageRes) {
        Toast.makeText(getApplicationContext(), errorMessageRes, Toast.LENGTH_SHORT).show();
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }




    public void gotoFavariteActivity(View view){
        Intent intent= new Intent(this,FavoriteActivity.class);
        startActivity(intent);

        for (int i = 0; i < MainActivity.favList.size(); i++) {
            Log.d("testfavview", MainActivity.favList.get(i).getName());
        }
    }

    public void lastSongEnd(){
        btPause.setVisibility(View.GONE);
        btPlay.setVisibility(View.VISIBLE);
        mediaPlayer.seekTo(0);
    }

    public static int getAudioPos(String audioName, List<AudioFile> audioList){
        int pos=-1;
        for (AudioFile af :audioList){
            if (af.getName().equals(audioName)){
                pos=audioList.indexOf(af);
            }
        }
        return pos;
    }

    }
