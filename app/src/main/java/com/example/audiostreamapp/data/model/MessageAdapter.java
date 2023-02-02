package com.example.audiostreamapp.data.model;

import static com.firebase.ui.auth.AuthUI.getApplicationContext;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.audiostreamapp.R;
import com.example.audiostreamapp.syncFunction.SyncRoomActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder>{

    private static final String TAG = "MessageAdapter";
    private Activity currentActivity;

    private ArrayList<Message> list;
    public MessageAdapter(ArrayList<Message> list,Activity currentActivity){
        this.list = list;
        this.currentActivity=currentActivity;
    }

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

        SpannableString message_to_show = new SpannableString(message.getMessageTime() + "\n\n" + message.getContent());
        // 设置字体大小（相对值,单位：像素） 参数表示为默认字体大小的多少倍
        message_to_show.setSpan(new RelativeSizeSpan(0.7f), 0, 20, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        SpannableString send_sync_message_to_show = new SpannableString(message.getMessageTime() + "\n\n" + "You just sent an invitation for listening together!");
        send_sync_message_to_show.setSpan(new RelativeSizeSpan(0.7f), 0, 20, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        SpannableString receive_sync_message_to_show = new SpannableString(message.getMessageTime() + "\n\n" + "Come to listen with me!");
        receive_sync_message_to_show.setSpan(new RelativeSizeSpan(0.7f), 0, 20, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        if(user.getUid().equals(message.getSender())) {
            // Display the message layout on the right and hide the message layout on the left
            holder.rightLayout.setVisibility(View.VISIBLE);
            if (message.getContent().contains("$%Welcomes you to Sync Room%$:")){
                holder.right_message.setText(send_sync_message_to_show);
                holder.right_sync_button.setVisibility(View.VISIBLE);
                holder.right_sync_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(currentActivity, SyncRoomActivity.class);
                        intent.putExtra("Role","Visitor");
                        intent.putExtra("RoomID",message.getContent().replace("$%Welcomes you to Sync Room%$:",""));
                        currentActivity.startActivity(intent);
                    }
                });
            }
            else{
                holder.right_message.setText(message_to_show);
            }
            holder.right_message.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view){
                    Dialog d = new AlertDialog.Builder(currentActivity)
                            .setIcon(android.R.drawable.ic_dialog_info)
                            .setTitle("Recall")
                            .setMessage("Are you sure you want to recall this message?")
                            .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    DatabaseReference mDatabase = FirebaseDatabase.getInstance("https://audiostreamapp-6a52b-default-rtdb.europe-west1.firebasedatabase.app/").getReference();
                                    mDatabase.child("message/" + user.getUid() + "/" + message.getReceiver() + "/" + message.getMessageTimeinDB()).removeValue();
                                    mDatabase.child("message/" + message.getReceiver() + "/" + user.getUid() + "/" + message.getMessageTimeinDB()).removeValue();
                                }
                            })
                            .setNegativeButton("Cancel", null)
                            .show();
                    return true;
                }
            });
            holder.leftLayout.setVisibility(View.GONE);
        }
        else if(user.getUid().equals(message.getReceiver())){
            // Display the message layout on the left and hide the message layout on the right
            holder.leftLayout.setVisibility(View.VISIBLE);
            if (message.getContent().contains("$%Welcomes you to Sync Room%$:")){
                holder.left_message.setText(receive_sync_message_to_show);
                holder.left_sync_button.setVisibility(View.VISIBLE);
                holder.left_sync_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(currentActivity, SyncRoomActivity.class);
                        intent.putExtra("Role","Visitor");
                        intent.putExtra("RoomID",message.getContent().replace("$%Welcomes you to Sync Room%$:",""));
                        currentActivity.startActivity(intent);
                    }
                });
            }
            else{
                holder.left_message.setText(message_to_show);
            }
            holder.left_message.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view){
                    Dialog d = new AlertDialog.Builder(currentActivity)
                            .setIcon(android.R.drawable.ic_dialog_info)
                            .setTitle("Report")
                            .setMessage("Are you sure you want to report this user?")
                            .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    DatabaseReference mDatabase = FirebaseDatabase.getInstance("https://audiostreamapp-6a52b-default-rtdb.europe-west1.firebasedatabase.app/").getReference();
                                    mDatabase.child("admin requests/" + message.getSender() + "/messages").setValue(message.getContent());
                                    Toast.makeText(currentActivity, "Reported! We will check this user's words.", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .setNegativeButton("Cancel", null)
                            .show();
                    return true;
                }
            });
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
        Button left_sync_button, right_sync_button;

        public ViewHolder(View view){
            super(view);
            leftLayout = view.findViewById(R.id.left_layout);
            left_message = view.findViewById(R.id.left_message);
            left_sync_button = view.findViewById(R.id.button_sync_left);
            rightLayout = view.findViewById(R.id.right_layout);
            right_message = view.findViewById(R.id.right_message);
            right_sync_button = view.findViewById(R.id.button_sync_right);
        }
    }
}
