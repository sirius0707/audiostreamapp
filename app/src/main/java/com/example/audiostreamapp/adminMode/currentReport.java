package com.example.audiostreamapp.adminMode;


import androidx.annotation.NonNull;

import com.example.audiostreamapp.AdminModeActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import java.util.concurrent.TimeUnit;

public class currentReport {
    private static String uid;
    private static String comment;
    private AdminModeActivity adminModeActivity;

    public static String getUid() {
        return uid;
    }

    public static void setUid(String uid) {
        currentReport.uid = uid;
    }

    public static String getComment() {
        return comment;
    }

    public static void setComment(String comment) {
        currentReport.comment = comment;
    }

    public AdminModeActivity getAdminModeActivity() {
        return adminModeActivity;
    }

    public void setAdminModeActivity(AdminModeActivity adminModeActivity) {
        this.adminModeActivity = adminModeActivity;
    }

    private static boolean isBlocked;
    public static boolean changeMedia(String newSongName) {
        DatabaseReference mDatabase = FirebaseDatabase.getInstance("https://audiostreamapp-6a52b-default-rtdb.europe-west1.firebasedatabase.app/").getReference();
        mDatabase.child("admin requests/" + uid + "admin status").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        mDatabase.child("permissions/" + uid + "admin status").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        isBlocked = false;


        try {
            TimeUnit.MILLISECONDS.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return isBlocked;
    }
}
