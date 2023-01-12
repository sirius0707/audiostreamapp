package com.example.audiostreamapp;

import static com.example.audiostreamapp.data.model.currentMediaPlayer.getMediaName;
import static com.example.audiostreamapp.ui.home.HomeFragment.audioFiles;

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
import android.widget.Button;
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

import com.example.audiostreamapp.databinding.ActivityMainBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    TextView playerPosition,playerDuration;
    SeekBar seekBar;
    ImageView btRew,btPlay,btPause,btFf;

    MediaPlayer mediaPlayer;
    Handler handler = new Handler();
    Runnable runnable;

    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private DatabaseReference mDatabase = FirebaseDatabase.getInstance("https://audiostreamapp-6a52b-default-rtdb.europe-west1.firebasedatabase.app/").getReference();;

    private Activity currentActivity;
    private ActivityMainBinding binding;

    private String CHANNEL_ID = "ChannelID";
    int notificationId = 1;

    static ArrayList<AudioFile> favList = new ArrayList<AudioFile>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        currentActivity = this;

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        BottomNavigationView navView = findViewById(R.id.nav_view);
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
                Map<String,Object> message = (Map<String,Object>) snapshot.getValue();
                int latest_number = message.size();
                int i = 0;
                for(DataSnapshot ds : snapshot.getChildren()){
                    i++;
                    if(i==latest_number){
                        if(user.getUid().equals(ds.child("Receiver").getValue().toString())) {
                            showSnackbar("You have a new message!");
                            createNotificationChannel();
                            Intent intent = new Intent(currentActivity, DisplayProfileActivity.class);
                            intent.putExtra("USERID", ds.child("Sender").getValue().toString());
                            PendingIntent pendingIntent = PendingIntent.getActivity(currentActivity, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

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

    public void startLiveRoomActivity(){
        Intent liveRoom_intent = new Intent(currentActivity,LiveRoomActivity.class);
        startActivity(liveRoom_intent);
    }

    public void resetDurationOfAudioPlayer(){
        if (mediaPlayer.isPlaying())
        {
            btPlay.setVisibility(View.GONE);
            btPause.setVisibility(View.VISIBLE);
            //Start media player
            handler.postDelayed(runnable,0);
        }
        seekBar.setMax(mediaPlayer.getDuration());

    }

    private String convertFormat(int duration) {
        return String.format("%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(duration),
                TimeUnit.MILLISECONDS.toSeconds(duration) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(duration)));
    }

    // Show message
    private void showSnackbar(String errorMessageRes) {
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



    public void addToFavorite(View view){
//
//        Toast.makeText(this,"button is clicked",Toast.LENGTH_SHORT).show();

        //传item的audio name进来，拿这串string和audiofiles里的比较，相同就加到playlist。
        String SongName = getMediaName();
//        String SongName = view.getContentDescription().toString();
        for (AudioFile af : audioFiles) {
            boolean matcher = SongName.equals(af.getName());
            if (matcher) {
                favList.add(new AudioFile(af.getName()));
            }
        }
        }

    public void gotoFavariteActivity(View view){
        Intent intent= new Intent(this,FavoriteActivity.class);
        startActivity(intent);

        for (int i = 0; i < MainActivity.favList.size(); i++) {
            Log.d("testfavview", MainActivity.favList.get(i).getName());
        }
    }
    }
