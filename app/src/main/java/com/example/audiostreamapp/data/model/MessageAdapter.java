package com.example.audiostreamapp.data.model;

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

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder>{

    private static final String TAG = "MessageAdapter";

    private ArrayList<Message> list;
    public MessageAdapter(ArrayList<Message> list){
        this.list = list;
    }

    StorageReference storageRef;

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_direct_message, parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Message message = list.get(position);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        //display avatar
        FirebaseStorage storage = FirebaseStorage.getInstance();

        if(user.getUid().equals(message.getSender())) {
            // Display the message layout on the right and hide the message layout on the left
            holder.rightLayout.setVisibility(View.VISIBLE);
            // right_avatar
            storageRef = storage.getReferenceFromUrl("gs://audiostreamapp-6a52b.appspot.com/userAvatar").child(user.getUid()+".jpg");
            storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Picasso.get().load(uri.toString()).resize(40, 40).into(holder.right_avatar);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    storageRef = storage.getReferenceFromUrl("gs://audiostreamapp-6a52b.appspot.com/userAvatar").child("Default.jpeg");
                    storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Picasso.get().load(uri.toString()).resize(40, 40).into(holder.right_avatar);
                        }
                    });
                }
            });
            holder.right_message.setText(message.getMessageTime() + "\n" + message.getContent());

            holder.leftLayout.setVisibility(View.GONE);
        }
        else if(user.getUid().equals(message.getReceiver())){
            // Display the message layout on the left and hide the message layout on the right
            holder.leftLayout.setVisibility(View.VISIBLE);
            // left_avatar
            storageRef = storage.getReferenceFromUrl("gs://audiostreamapp-6a52b.appspot.com/userAvatar").child(message.getSender()+".jpg");
            storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Picasso.get().load(uri.toString()).resize(40, 40).into(holder.left_avatar);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    storageRef = storage.getReferenceFromUrl("gs://audiostreamapp-6a52b.appspot.com/userAvatar").child("Default.jpeg");
                    storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Picasso.get().load(uri.toString()).resize(40, 40).into(holder.left_avatar);
                        }
                    });
                }
            });
            holder.left_message.setText(message.getMessageTime() + "\n" + message.getContent());
            holder.rightLayout.setVisibility(View.GONE);
        }
        else
            Log.d(TAG, "Error");
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        LinearLayout leftLayout, rightLayout;
        TextView left_message, right_message;
        ImageView left_avatar, right_avatar;

        public ViewHolder(View view){
            super(view);
            leftLayout = view.findViewById(R.id.left_layout);
            left_avatar = view.findViewById(R.id.left_avatar);
            left_message = view.findViewById(R.id.left_message);
            rightLayout = view.findViewById(R.id.right_layout);
            right_message = view.findViewById(R.id.right_message);
            right_avatar = view.findViewById(R.id.right_avatar);
        }
    }
}
