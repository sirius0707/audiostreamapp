package com.example.audiostreamapp.data.model;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;

public class User {
    String userid;
    String username;

    public User(String userID, String username){
        this.userid = userID;
        this.username = username;
    }

    public String getUserid() {return userid;}

    public String getUsername() {return username;}

}
