package com.example.audiostreamapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

public class DisplayProfileActivity extends AppCompatActivity {
    private ImageView avatarImage;
    private StorageReference storageRef;
    private TextView nameText;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_profile);

        mDatabase = FirebaseDatabase.getInstance("https://audiostreamapp-6a52b-default-rtdb.europe-west1.firebasedatabase.app/").getReference();
        avatarImage = findViewById(R.id.Image_Avatar);
        nameText = findViewById(R.id.chatter_name);
        String userID = getIntent().getStringExtra("USERID");

        //display name from realtime database
        mDatabase.child("users").child(userID).child("username").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                }
                else {
                     nameText.setText(String.valueOf(task.getResult().getValue()));
                }
            }
        });

        //display avatar
        FirebaseStorage storage = FirebaseStorage.getInstance();
        // Create a StorageReference to the project URL and the file to download
        storageRef = storage.getReferenceFromUrl("gs://audiostreamapp-6a52b.appspot.com/userAvatar").child(userID+".jpg");
        storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Log.e("Tuts+", "uri: " + uri.toString());
                //Handle whatever you're going to do with the URL here
                Picasso.get().load(uri.toString()).resize(400, 400).into(avatarImage);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                storageRef = storage.getReferenceFromUrl("gs://audiostreamapp-6a52b.appspot.com/userAvatar").child("Default.jpeg");
                storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Log.e("Tuts+", "uri: " + uri.toString());
                        //Handle whatever you're going to do with the URL here
                        Picasso.get().load(uri.toString()).resize(400, 400).into(avatarImage);
                    }
                });
            }
        });

    }
}