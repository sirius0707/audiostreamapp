package com.example.audiostreamapp.data.model;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.audiostreamapp.DisplayProfileActivity;
import com.example.audiostreamapp.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder>{

    private static final String TAG = "UserAdapter";

    private ArrayList<User> list;
    private Activity mContext;

    public UserAdapter(ArrayList<User> list, Activity context){
        this.list = list;
        this.mContext = context;
    }
    StorageReference storageRef, storageRef_message;

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user, parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User user = list.get(position);

        //display avatar
        FirebaseStorage storage = FirebaseStorage.getInstance();
        // Create a StorageReference to the project URL and the file to download
        storageRef = storage.getReferenceFromUrl("gs://audiostreamapp-6a52b.appspot.com/userAvatar").child(user.getUserid()+".jpg");
        storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Log.e("Tuts+", "uri: " + uri.toString());
                //Handle whatever you're going to do with the URL here
                Picasso.get().load(uri.toString()).resize(100, 100).into(holder.useravatar);
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
                        Picasso.get().load(uri.toString()).resize(100, 100).into(holder.useravatar);
                    }
                });
            }
        });

        // Set item views based on your views and data model
        holder.username.setText(user.getUsername());
        holder.latestmessage.setText("latest");
        holder.latestmessage.setTextColor(Color.RED);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String uid = user.userid;
                Intent showProfile = new Intent(mContext, DisplayProfileActivity.class);
                showProfile.putExtra("USERID",uid);
                mContext.startActivity(showProfile);
            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        public ImageView useravatar;
        public TextView username, latestmessage;

        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public ViewHolder(View View) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(View);

            useravatar = (ImageView) View.findViewById(R.id.single_userAvatar_format);
            username = (TextView) View.findViewById(R.id.single_username_format);
            latestmessage = (TextView) View.findViewById(R.id.single_latest_message_format);

        }

    }
}
