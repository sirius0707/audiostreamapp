package com.example.audiostreamapp.syncFunction;

import static com.example.audiostreamapp.data.model.currentMediaPlayer.getMediaName;
import static com.example.audiostreamapp.data.model.currentMediaPlayer.getRepoName;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.audiostreamapp.LiveRoomActivity;
import com.example.audiostreamapp.R;
import com.example.audiostreamapp.SyncRoomAudioChooseActivity;
import com.example.audiostreamapp.data.model.ActionWithFirebase;
import com.example.audiostreamapp.data.model.currentMediaPlayer;
import com.example.audiostreamapp.liveComment.LiveComment;
import com.example.audiostreamapp.liveComment.LiveCommentAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class SyncRoomActivity extends AppCompatActivity {
    TextView playerPosition,playerDuration;
    SeekBar seekBar;
    ImageView btRew,btPlay,btPause,btFf;
    Handler handler = new Handler();
    Runnable runnable;
    ObjectAnimator mAnimator;
    MediaPlayer mediaPlayer;
    Button btSendMs;
    EditText liveComment;
    boolean onButtom=true;
    String role;
    String newSyncRoomKey;
    ValueEventListener valueListner;
    ImageView phonogramLogo;
    private Activity currentActivity;

    private DatabaseReference mDatabase = FirebaseDatabase.getInstance("https://audiostreamapp-6a52b-default-rtdb.europe-west1.firebasedatabase.app/").getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sync_room);
        currentActivity = this;



        role = getIntent().getStringExtra("Role");

        playerPosition = findViewById(R.id.player_position);
        playerDuration = findViewById(R.id.player_duration);
        seekBar = findViewById(R.id.seek_bar);
        btRew = findViewById(R.id.bt_rew);
        btPlay = findViewById(R.id.bt_play);
        btPause = findViewById(R.id.bt_pause);
        btFf = findViewById(R.id.bt_ff);
        btSendMs = findViewById(R.id.send_livechat_button);
        liveComment = findViewById(R.id.input_livechat_box);
        phonogramLogo = findViewById(R.id.phonogram_logo);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String uid=user.getUid();
        String userName = user.getDisplayName();
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
        if (mediaPlayer.isPlaying())
        {
            mAnimator.start();
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
                mAnimator.resume();
                mediaPlayer.start();
                seekBar.setMax(mediaPlayer.getDuration());
                handler.postDelayed(runnable,0);
                setCurrentStatus();
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
                                           setCurrentStatus();
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
                setCurrentStatus();
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
                setCurrentStatus();
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(fromUser){
                    mediaPlayer.seekTo(progress);
                    setCurrentStatus();
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


        //first Action after enter Sync Room
        if (role.equals("Host")){
            //create new syncRome with State
            newSyncRoomKey=mDatabase.child("syncRoom/").push().getKey();
            ActionWithFirebase.sendMessage(FirebaseAuth.getInstance().getCurrentUser().getUid(),getIntent().getStringExtra("VisiorID"),
                    "$%Welcomes you to Sync Room%$:"+newSyncRoomKey,new EditText(this));

        }
        else{
            newSyncRoomKey = getIntent().getStringExtra("RoomID");
        }
        RecyclerView commentList = this.findViewById(R.id.live_comment_list);
        ArrayList<LiveComment> items = new ArrayList<>();
        LiveCommentAdapter adapter = new LiveCommentAdapter(items,this);
        commentList.setAdapter(adapter);
        commentList.setLayoutManager(new LinearLayoutManager(this));

        // set send message button
        // send message to database
        btSendMs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String singleComment = liveComment.getText().toString();
                long timeStamp = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis());
                String key = mDatabase.child("/syncRoom/" + newSyncRoomKey +"/livechat").push().getKey();
                Map<String, Object> liveCommitAttribute = new HashMap<>();
                liveCommitAttribute.put("TimeStamp",timeStamp);
                liveCommitAttribute.put("Context",singleComment);
                liveCommitAttribute.put("userID",uid);
                liveCommitAttribute.put("userName",userName);
                if(singleComment!=null && singleComment.length() != 0){
                    mDatabase.child("/syncRoom/" + newSyncRoomKey +"/livechat/" + key).setValue(liveCommitAttribute).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            liveComment.setText("");
                        };
                    });
                }
            }
        });

        //enter audio choose activity
        phonogramLogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent audioChoose_intent = new Intent(currentActivity, SyncRoomAudioChooseActivity.class);
                startActivity(audioChoose_intent);
            }
        });

        //display live comments from database
        commentList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (!recyclerView.canScrollVertically(1)) {
                    onButtom=true;
                }
                else{
                    onButtom=false;
                }
            }
        });

        //display live comments
        mDatabase.child("syncRoom/" + newSyncRoomKey + "/livechat")
                .limitToLast(6).addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                        // A new comment has been added, add it to the displayed list
                        if (snapshot != null) {
                            Map<String, Object> comment = (Map<String, Object>) snapshot.getValue();
                            items.add(new LiveComment(comment.get("userID").toString(),
                                    Long.parseLong(comment.get("TimeStamp").toString()),
                                    comment.get("Context").toString(),
                                    comment.get("userName").toString()));
                            adapter.notifyItemRangeInserted(items.size() - 1, 1);
                            if (onButtom)
                                commentList.scrollToPosition(items.size() - 1);

                        }
                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

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

    private void initAnimator() {
            mAnimator = ObjectAnimator.ofFloat(phonogramLogo,"rotation",0.0f,360.0f);
            mAnimator.setDuration(3000);//设定转一圈的时间
            mAnimator.setRepeatCount(Animation.INFINITE);//设定无限循环
            mAnimator.setRepeatMode(ObjectAnimator.RESTART);//循环模式
            mAnimator.setInterpolator(new LinearInterpolator());//匀速
            mAnimator.start();
            mAnimator.pause();
    }

    private String convertFormat(int duration) {
        return String.format("%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(duration),
                TimeUnit.MILLISECONDS.toSeconds(duration) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(duration)));
    }

    @Override
    protected void onStart() {
        super.onStart();
        //if (role.equals("Host")){
            //create new syncRome with State
            setCurrentStatus();

        //}

        resetDurationOfAudioPlayer();

        Map<String, Object> updates = new HashMap<>();

        updates.put("syncRoom/"+ newSyncRoomKey+"/visitorStatus",
                ServerValue.increment(1));

        mDatabase.updateChildren(updates);
        mDatabase.child("syncRoom/"+ newSyncRoomKey+"/visitorStatus").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue()!=null && snapshot.getValue(Long.class)==2){
                    setCurrentStatus();
                    setTitle("Friend Come :)");

                }
                else
                if (snapshot.getValue()!=null &&  snapshot.getValue(Long.class)==0)
                    mDatabase.child("syncRoom/" + newSyncRoomKey).removeValue();
                else
                    setTitle("Friend has not come :(");
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
        //get Sync from other Person
        valueListner= new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue()!=null) {


                    String newSongName=snapshot.child("currentMusicName").getValue(String.class);
                    String repoName=snapshot.child("repoName").getValue(String.class);
                    StorageReference storageRef = FirebaseStorage.getInstance().getReference().child(repoName+"/"+newSongName);
                    if (getMediaName().equals(newSongName))
                    {

                        mediaPlayer.start();
                        mediaPlayer.seekTo(snapshot.child("pos").getValue(Long.class).intValue());
                        handler.postDelayed(runnable,0);
                        playerPosition.setText(convertFormat(mediaPlayer.getCurrentPosition()));
                        Boolean playStatus = snapshot.child("playStatus").getValue(Boolean.class);

                        if (!playStatus && mediaPlayer.isPlaying()){
                            mAnimator.pause();
                            btPause.setVisibility(View.GONE);
                            btPlay.setVisibility(View.VISIBLE);
                            mediaPlayer.pause();
                            handler.removeCallbacks(runnable);
                        }

                        if (playStatus){
                            btPlay.setVisibility(View.GONE);
                            btPause.setVisibility(View.VISIBLE);

                            mAnimator.resume();

                        }
                        return;
                    }
                    if (mediaPlayer.isPlaying())
                        handler.removeCallbacks(runnable);
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
                                            currentMediaPlayer.getMainActivity().resetDurationOfAudioPlayer();
                                            if (mp.isPlaying())
                                            {
                                                btPlay.setVisibility(View.GONE);
                                                btPause.setVisibility(View.VISIBLE);
                                                //Start media player
                                                handler.postDelayed(runnable,0);
                                            }
                                            seekBar.setMax(mp.getDuration());
                                            Log.e("musicPos",snapshot.child("pos").getValue(Integer.class).toString());
                                            mp.seekTo(snapshot.child("pos").getValue(Long.class).intValue());
                                            playerPosition.setText(convertFormat(mp.getCurrentPosition()));
                                            Boolean playStatus = snapshot.child("playStatus").getValue(Boolean.class);

                                            if (!playStatus){
                                                btPause.setVisibility(View.GONE);
                                                btPlay.setVisibility(View.VISIBLE);
                                                mp.pause();
                                                handler.removeCallbacks(runnable);
                                            }

                                            resetDurationOfAudioPlayer();

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



                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        };
        mDatabase.child("syncRoom/"+ newSyncRoomKey+"/status").addValueEventListener(valueListner);

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



    private void setCurrentStatus(){
        HashMap<String,Object> newState=new HashMap<>();
        newState.put("currentMusicName",getMediaName());
        newState.put("pos",mediaPlayer.getCurrentPosition());
        newState.put("playStatus",mediaPlayer.isPlaying());
        newState.put("repoName",getRepoName());
        mDatabase.child("syncRoom/"+ newSyncRoomKey+"/status").setValue(newState);
    }

    @Override
    protected void onStop() {
        super.onStop();


        mDatabase.child("syncRoom/"+ newSyncRoomKey+"/status").removeEventListener(valueListner);
        Map<String, Object> updates = new HashMap<>();

        updates.put("syncRoom/"+ newSyncRoomKey+"/visitorStatus",
                ServerValue.increment(-1));
        mDatabase.updateChildren(updates);



    }
}