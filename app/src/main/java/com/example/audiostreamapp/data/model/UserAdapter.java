package com.example.audiostreamapp.data.model;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;

import com.example.audiostreamapp.DisplayProfileActivity;
import com.example.audiostreamapp.R;
import com.example.audiostreamapp.syncFunction.SyncRoomActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

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
        //圆角背景开始
        if (position ==0){
            holder.usercontainer.setBackgroundResource(R.drawable.round_color_top);
        }

        if(position == list.size()-1 ){
            holder.usercontainer.setBackgroundResource(R.drawable.round_corner_bottom);
            holder.userdivider.setVisibility(View.GONE);
        }
        //圆角背景结束

        User user = list.get(position);

        //display avatar
        FirebaseStorage storage = FirebaseStorage.getInstance();
        storageRef = storage.getReferenceFromUrl("gs://audiostreamapp-6a52b.appspot.com/userAvatar").child(user.getUserid()+".jpg");
        storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri.toString()).resize(100, 100).into(holder.useravatar);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                storageRef = storage.getReferenceFromUrl("gs://audiostreamapp-6a52b.appspot.com/userAvatar").child("Default.jpeg");
                storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Picasso.get().load(uri.toString()).resize(100, 100).into(holder.useravatar);
                    }
                });
            }
        });

        // Set item views based on your views and data model
        holder.username.setText(user.getUsername());
        if (user.getLatest_message().contains("$%Welcomes you to Sync Room%$:"))
            holder.latestmessage.setText("[Sync Room Button]");
        else
            holder.latestmessage.setText(user.getLatest_message());
        //holder.latestmessage.setTextColor(Color.RED);//Set unread message red
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

        public ImageView useravatar;
        public TextView username, latestmessage;
        //圆角背景
        RelativeLayout usercontainer;
        View userdivider;

        public ViewHolder(View View) {
            super(View);

            useravatar = (ImageView) View.findViewById(R.id.single_userAvatar_format);
            username = (TextView) View.findViewById(R.id.single_username_format);
            latestmessage = (TextView) View.findViewById(R.id.single_latest_message_format);
            usercontainer = itemView.findViewById(R.id.userContainer);
            userdivider = itemView.findViewById(R.id.userdivider);

        }

    }
}
