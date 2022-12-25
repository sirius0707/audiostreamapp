package com.example.audiostreamapp.liveComment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.audiostreamapp.DisplayProfileActivity;
import com.example.audiostreamapp.LiveRoomActivity;
import com.example.audiostreamapp.MainActivity;
import com.example.audiostreamapp.R;
import com.example.audiostreamapp.data.model.currentMediaPlayer;
import com.example.audiostreamapp.liveComment.LiveComment;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;
import java.util.concurrent.TimeUnit;


public class LiveCommentAdapter extends
        RecyclerView.Adapter<com.example.audiostreamapp.liveComment.LiveCommentAdapter.ViewHolder> {

    // Store a member variable for the contacts
    private List<LiveComment> mComments;
    private Activity mContext;

    // Pass in the contact array into the constructor
    public LiveCommentAdapter(List<LiveComment> contacts, Activity context) {
        mComments = contacts;
        this.mContext = context;

    }
    @NonNull
    @Override
    public com.example.audiostreamapp.liveComment.LiveCommentAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View contactView = inflater.inflate(R.layout.item_live_comment, parent, false);

        // Return a new holder instance
        com.example.audiostreamapp.liveComment.LiveCommentAdapter.ViewHolder viewHolder = new com.example.audiostreamapp.liveComment.LiveCommentAdapter.ViewHolder(contactView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull com.example.audiostreamapp.liveComment.LiveCommentAdapter.ViewHolder holder, int position) {
        // Get the data model based on position
        LiveComment liveComment = mComments.get(position);

        // Set item views based on your views and data model
        TextView textView = holder.nameTextView;
        textView.setText(liveComment.getUserName()+"("+liveComment.getCommentTime()+")"+" : "+liveComment.getCommentText());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String uid = liveComment.userID;
                Intent showProfile = new Intent(mContext, DisplayProfileActivity.class);
                showProfile.putExtra("USERID",uid);
                mContext.startActivity(showProfile);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mComments.size();
    }

    // Provide a direct reference to each of the views within a data item
    // Used to cache the views within the item layout for fast access
    public class ViewHolder extends RecyclerView.ViewHolder {
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        public TextView nameTextView;

        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public ViewHolder(View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);

            nameTextView = (TextView) itemView.findViewById(R.id.single_comment_format);


        }
    }
}
