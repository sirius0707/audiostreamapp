package com.example.audiostreamapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.audiostreamapp.data.model.Message;
import com.example.audiostreamapp.data.model.ActionWithFirebase;
import com.example.audiostreamapp.data.model.MessageAdapter;
import com.example.audiostreamapp.data.model.User;
import com.example.audiostreamapp.syncFunction.SyncRoomActivity;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DirectMessageActivity extends AppCompatActivity {

    private static final String TAG = "DirectMessageActivity";

    private DatabaseReference mDatabase= FirebaseDatabase.getInstance("https://audiostreamapp-6a52b-default-rtdb.europe-west1.firebasedatabase.app/").getReference();
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    String receiverID;
    private Activity currentActivity = this;

    private RecyclerView msgRecyclerView;
    private MessageAdapter adapter;

    private EditText inputText;
    private Button send;
    private LinearLayoutManager layoutManager;
    private Button syncButton;

    ArrayList<Message> items = new ArrayList<>();

    boolean onButtom=true;

    public static String admin_status = "";
    public static String user_status = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_direct_message);

        inputText = findViewById(R.id.input_text);
        send = findViewById(R.id.send);

        msgRecyclerView = findViewById(R.id.msg_recycler_view);
        layoutManager = new LinearLayoutManager(this);
        msgRecyclerView.setLayoutManager(layoutManager);
        adapter = new MessageAdapter(items,this);
        msgRecyclerView.setAdapter(adapter);

        receiverID = getIntent().getStringExtra("receiverID");

        syncButton=this.findViewById(R.id.newSyncRoomButton);

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


        // Show message
        mDatabase.child("message/" + user.getUid() + "/" + receiverID).limitToLast(20).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if(!snapshot.getKey().equals("latest message")){
                    // A new Message has been added, add it to the displayed list
                    Map<String,Object> message = (Map<String,Object>) snapshot.getValue();
                    items.add(new Message(message.get("Context").toString(),
                            Long.parseLong(message.get("TimeStamp").toString()),
                            message.get("Sender").toString(),
                            message.get("Receiver").toString()));
                    adapter.notifyItemRangeInserted(items.size()-1,1);
                    if (onButtom)
                        msgRecyclerView.scrollToPosition(items.size() - 1);
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

    @Override
    protected void onResume() {

        super.onResume();

        // Get admin status
        /* mDatabase.child("permissions/" + user.getUid() + "/admin status").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.getValue()==null){//Initialize Admin Status
                    mDatabase.child("permissions/" + user.getUid() + "/admin status").setValue("Normal");
                }
                else{
                    admin_status = snapshot.getValue().toString();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });  */
        // Get user status
        mDatabase.child("permissions/" + user.getUid() + "/" + receiverID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.getValue()==null){//Initialize Admin Status
                    mDatabase.child("permissions/" + user.getUid() + "/" + receiverID).setValue("Normal");
                }
                else{
                    user_status = snapshot.getValue().toString();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Admin status: " + admin_status);
                Log.d(TAG, "User status: " + user_status);
                try {
                    if (admin_status.equals("Banned"))
                        showSnackbar("You are banned!");
                    else if (user_status.equals("Blocked"))
                        showSnackbar("You are blocked by this user!");
                    else {
                        String content = inputText.getText().toString();
                        ActionWithFirebase.sendMessage(user.getUid(), receiverID, content, inputText);
                    }
                } catch (NullPointerException e) {
                    String content = inputText.getText().toString();
                    ActionWithFirebase.sendMessage(user.getUid(), receiverID, content, inputText);
                }
            }
        });
        syncButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "Admin status: " + admin_status);
                Log.d(TAG, "User status: " + user_status);
                try {
                    if (admin_status.equals("Banned"))
                        showSnackbar("You are banned!");
                    else if (user_status.equals("Blocked"))
                        showSnackbar("You are blocked by this user!");
                    else {

                        Intent intent = new Intent(currentActivity, SyncRoomActivity.class);
                        intent.putExtra("Role","Host");
                        intent.putExtra("VisiorID", receiverID);
                        startActivity(intent);
                    }
                } catch (NullPointerException e) {
                    Intent intent = new Intent(currentActivity, SyncRoomActivity.class);
                    intent.putExtra("Role","Host");
                    intent.putExtra("VisiorID", receiverID);
                    startActivity(intent);
                }
            }
        });
    }

    // Show message
    private void showSnackbar(String errorMessageRes) {
        Toast.makeText(getApplicationContext(), errorMessageRes, Toast.LENGTH_SHORT).show();
    }
}