package com.example.audiostreamapp.data.model;

import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Color;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.audiostreamapp.DisplayProfileActivity;
import com.example.audiostreamapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

public class User {
    String userid;
    String username;
    String latest_message;

    public User(String userID,  String latest_message){
        this.userid = userID;
        this.username = "username";
        this.latest_message = latest_message;
    }

    public String getUserid() {return userid;}

    public String getUsername() {return username;}

    public String getLatest_message() {return latest_message;}

    public void setUsername(String username) {this.username=username;}
}
