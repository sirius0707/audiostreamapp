package com.example.audiostreamapp.liveComment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
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
        DatabaseReference mDatabase = FirebaseDatabase.getInstance("https://audiostreamapp-6a52b-default-rtdb.europe-west1.firebasedatabase.app/").getReference();
        // check if user's Block status
        String isBlocked = "unblocked";
        mDatabase.child("reports/" + liveComment.userID + "/isBlocked").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot tasksSnapshot) {
                for (DataSnapshot snapshot: tasksSnapshot.getChildren()) {
                    Object isBlocked = snapshot.getValue().toString();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
           @Override
           public boolean onLongClick(View view){
               Dialog d = new AlertDialog.Builder(mContext)
                       .setIcon(android.R.drawable.ic_dialog_info)
                       .setTitle("Report")
                       .setMessage("Are you sure you want to report this user?")
                       .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                           @Override
                           public void onClick(DialogInterface dialogInterface, int i) {
                               FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                               mDatabase.child("reports/" + liveComment.userID + "/livecomments").setValue(liveComment.commentText);
                               if(isBlocked.equals("blocked")) {
                                   Toast.makeText(mContext, "This user is already blocked!", Toast.LENGTH_SHORT).show();
                               }
                               else {
                                   mDatabase.child("reports/" + liveComment.userID + "/isBlocked").setValue("reported");
                                   Toast.makeText(mContext, "Reported! We will check this user's words.", Toast.LENGTH_SHORT).show();
                               }
                           }
                       })
                       .setNegativeButton("Cancel", null)
                       .show();
               return true;
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
