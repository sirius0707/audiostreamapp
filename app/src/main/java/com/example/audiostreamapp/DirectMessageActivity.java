package com.example.audiostreamapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.audiostreamapp.data.model.Message;
import com.example.audiostreamapp.data.model.MessageAdapter;
import com.example.audiostreamapp.data.model.currentMediaPlayer;
import com.example.audiostreamapp.liveComment.LiveComment;
import com.example.audiostreamapp.liveComment.LiveCommentAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class DirectMessageActivity extends AppCompatActivity {

    private static final String TAG = "DirectMessageActivity";

    //private ArrayList<Message> messageList = new ArrayList<Message>();//unused

    private RecyclerView msgRecyclerView;
    private DatabaseReference mDatabase;
    private MessageAdapter adapter;

    private EditText inputText;
    private Button send;
    private LinearLayoutManager layoutManager;

    ArrayList<Message> items = new ArrayList<>();

    boolean onButtom=true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_direct_message);

        String receiverID = getIntent().getStringExtra("receiverID");

        inputText = findViewById(R.id.input_text);
        send = findViewById(R.id.send);

        msgRecyclerView = findViewById(R.id.msg_recycler_view);
        layoutManager = new LinearLayoutManager(this);
        msgRecyclerView.setLayoutManager(layoutManager);
        adapter = new MessageAdapter(items);
        msgRecyclerView.setAdapter(adapter);


        mDatabase = FirebaseDatabase.getInstance("https://audiostreamapp-6a52b-default-rtdb.europe-west1.firebasedatabase.app/").getReference();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

/*       我们还需要为button建立一个监听器，我们需要将编辑框的内容发送到 RecyclerView 上：
            ①获取内容，将需要发送的消息添加到 List 当中去。
            ②调用适配器的notifyItemInserted方法，通知有新的数据加入了，赶紧将这个数据加到 RecyclerView 上面去。
            ③调用RecyclerView的scrollToPosition方法，以保证一定可以看的到最后发出的一条消息。*/
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content = inputText.getText().toString();
                long timeStamp = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis());
                Map<String, Object> MessageAttribute = new HashMap<>();
                MessageAttribute.put("TimeStamp", timeStamp);
                MessageAttribute.put("Context", content);
                MessageAttribute.put("Sender", user.getUid());
                MessageAttribute.put("Receiver", receiverID);
                if(content!=null && content.length() != 0){
                    mDatabase.child("message/" + user.getUid() + "/" + receiverID + "/" + timeStamp)
                            .setValue(MessageAttribute).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    inputText.setText("");
                                };
                            });
                    mDatabase.child("message/"+ receiverID + "/" + user.getUid() + "/" + timeStamp)
                            .setValue(MessageAttribute).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    inputText.setText("");
                                };
                            });
                }
            }
        });

        // Display
        msgRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
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

        // Realtime Database/message/userID-key/
        mDatabase.child("message/" + user.getUid() + "/" + receiverID).limitToLast(20).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                // A new Message has been added, add it to the displayed list
                Map<    String,Object> message = (Map<String,Object>) snapshot.getValue();
                //snapshot.getKey()->String
                items.add(new Message(message.get("Context").toString(),
                        Long.parseLong(message.get("TimeStamp").toString()),
                        message.get("Sender").toString(),
                        message.get("Receiver").toString()));
                adapter.notifyItemRangeInserted(items.size()-1,1);
                if (onButtom)
                    msgRecyclerView.scrollToPosition(items.size() - 1);
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

    // Show message
    private void showSnackbar(String errorMessageRes) {
        Toast.makeText(getApplicationContext(), errorMessageRes, Toast.LENGTH_SHORT).show();
    }
}