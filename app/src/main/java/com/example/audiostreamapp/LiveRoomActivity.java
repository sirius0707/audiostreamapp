package com.example.audiostreamapp;

import static com.firebase.ui.auth.AuthUI.getApplicationContext;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.audiostreamapp.data.model.currentMediaPlayer;
import com.example.audiostreamapp.liveComment.LiveComment;
import com.example.audiostreamapp.liveComment.LiveCommentAdapter;
import com.example.audiostreamapp.ui.home.HomeFragment;
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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class LiveRoomActivity extends AppCompatActivity {
    TextView playerPosition,playerDuration;
    SeekBar seekBar;
    ImageView btRew,btPlay,btPause,btFf,iv;
    Handler handler = new Handler();
    Runnable runnable;
    ObjectAnimator mAnimator;
    MediaPlayer mediaPlayer;
    Button btSendMs;
    EditText liveComment;
    private DatabaseReference mDatabase;
    boolean onButtom=true;

    private static final String TAG = "LiveRoomActivity";

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
        btSendMs = findViewById(R.id.send_livechat_button);
        liveComment = findViewById(R.id.input_livechat_box);
        iv = findViewById(R.id.iv);

        initAnimator();

        mDatabase = FirebaseDatabase.getInstance("https://audiostreamapp-6a52b-default-rtdb.europe-west1.firebasedatabase.app/").getReference();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String uid = user.getUid();
        String userName = user.getDisplayName();

        mDatabase.child("music/"+ currentMediaPlayer.getMediaName().replace(".mp3","")+"/livechatNum").addValueEventListener(new ValueEventListener() {
            // livechatNum数据改变时触发，展示播放人数
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // snapshot: DataSnapshot{key = livechatNum, value = ?}
                setTitle(currentMediaPlayer.getMediaName().replace(".mp3","")+"("+snapshot.getValue().toString()+" Person on Live)");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        // Player function
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

        // Button: Play
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
                    mDatabase.updateChildren(updates);
                }else {
                    Log.e("Storage error","Specified storage is not found");
                }

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

        // Button: Pause
        btPause.setOnClickListener(new View.OnClickListener() {
                                       @Override
                                       public void onClick(View view) {
                                           btPause.setVisibility(View.GONE);
                                           btPlay.setVisibility(View.VISIBLE);
                                           mAnimator.pause();
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




        RecyclerView commentList = (RecyclerView) this.findViewById(R.id.live_comment_list);
        ArrayList<LiveComment> items = new ArrayList<>();
        LiveCommentAdapter adapter = new LiveCommentAdapter(items,this);
        commentList.setAdapter(adapter);
        commentList.setLayoutManager(new LinearLayoutManager(this));
        final String[] user_status = {"unblocked"};
        //get the isBlocked status
        mDatabase.child("reports/" + user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //Initialize Admin Status
                if(snapshot.getValue()!=null){
                    user_status[0] = snapshot.child("isBlocked").getValue().toString();
                }
                Log.d("user_status", user_status[0]);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        // 点击发送评论按钮
        btSendMs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String singleComment = liveComment.getText().toString();
                String singleCommentChanged = new String();
                try {
                    singleCommentChanged = Filter(singleComment);
                } catch (IOException e) {
                    e.printStackTrace();

                }
                long timeStamp = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis());
                String key = mDatabase.child("music/"+ currentMediaPlayer.
                        getMediaName().
                        replace(".mp3","")+"/livechat").push().getKey();
                Map<String, Object> liveCommitAttribute = new HashMap<>();
                liveCommitAttribute.put("TimeStamp",timeStamp);
                liveCommitAttribute.put("Context",singleCommentChanged);
                liveCommitAttribute.put("userID",uid);
                liveCommitAttribute.put("userName",userName);
                if (!user_status[0].equals("blocked")){
                    if(singleComment!=null && singleComment.length() != 0){
                    mDatabase.child("music/"+ currentMediaPlayer.getMediaName().replace(".mp3","") + "/livechat/"+key)
                            .setValue(liveCommitAttribute).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    liveComment.setText("");
                                };
                            });

                }

                }else showSnackbar("You are blocked!");

            }
        });

        //display live comments

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

        //init audiofile list from Firebase Storage
        // 数据库music/歌曲.mp3/livechat中最新（最后）6个评论
        mDatabase.child("music/"+ currentMediaPlayer.getMediaName().replace(".mp3","")+"/livechat")
                .limitToLast(6).addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                        // A new comment has been added, add it to the displayed list
                        Map<String,Object> comment = (Map<String,Object>) snapshot.getValue();
                        items.add(new LiveComment(comment.get("userID").toString(),
                                Long.parseLong(comment.get("TimeStamp").toString()),
                                comment.get("Context").toString(),
                                comment.get("userName").toString()));
                        adapter.notifyItemRangeInserted(items.size()-1,1);
                        if (onButtom)
                            commentList.scrollToPosition(items.size() - 1);
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
        mAnimator = ObjectAnimator.ofFloat(iv,"rotation",0.0f,360.0f);
        mAnimator.setDuration(3000);//设定转一圈的时间
        mAnimator.setRepeatCount(Animation.INFINITE);//设定无限循环
        mAnimator.setRepeatMode(ObjectAnimator.RESTART);//循环模式
        mAnimator.setInterpolator(new LinearInterpolator());//匀速
        mAnimator.start();
        mAnimator.pause();
    }

    //filter the bad words and substitute with *
    public String Filter(String text) throws IOException {
        boolean flag = false;

        //list of bad words from https://github.com/coffee-and-fun/google-profanity-words
        InputStream inputStream = this.getResources().openRawResource(R.raw.wordlist);
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        String line;
        ArrayList<String> wordList = new ArrayList<>();
        while ((line = bufferedReader.readLine()) != null) {
            wordList.add(line);
        }
        bufferedReader.close();
        inputStreamReader.close();

        String textChanged = "";
        for (String textSeperated : text.split(" ")) {
            for (String w : wordList) {
                if (textSeperated.contains(w)) {
                    flag = true;
                    textSeperated = textSeperated.replace(w, "*");
                }

            }
            textChanged = textChanged + " " + textSeperated;
        }

        if (flag == false) {
            return text;
        } else {
            return textChanged.substring(1);
        }
    }

    // 转换格式
    private String convertFormat(int duration) {
        return String.format("%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(duration),
                TimeUnit.MILLISECONDS.toSeconds(duration) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(duration)));
    }

    // 打开音乐界面人数+1
    @Override
    protected void onStart() {
        super.onStart();
        Map<String, Object> updates = new HashMap<>();
        // livechatNum+1
        updates.put("music/"+ currentMediaPlayer.getMediaName().replace(".mp3","")+"/livechatNum",
                ServerValue.increment(1));
        mDatabase.updateChildren(updates);
    }

    // 打开音乐界面人数-1
    @Override
    protected void onStop() {
        super.onStop();
        Map<String, Object> updates = new HashMap<>();
        // livechatNum-1
        updates.put("music/"+ currentMediaPlayer.getMediaName().replace(".mp3","")+"/livechatNum",
                ServerValue.increment(-1));
        mDatabase.updateChildren(updates);
    }

    // Show message
    private void showSnackbar(String errorMessageRes) {
        Toast.makeText(getApplicationContext(), errorMessageRes, Toast.LENGTH_SHORT).show();
    }


}