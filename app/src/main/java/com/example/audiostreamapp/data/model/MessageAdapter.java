package com.example.audiostreamapp.data.model;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.audiostreamapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder>{

    private static final String TAG = "MessageAdapter";

    private ArrayList<Message> list;
    public MessageAdapter(ArrayList<Message> list){
        this.list = list;
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
        if(user.getUid() == message.getSender()) {
            Log.d(TAG, "Send a message"+message.getMessageTime());
            //如果是发出的消息，则显示右边的消息布局，将左边的消息布局隐藏
            holder.rightLayout.setVisibility(View.VISIBLE);
            holder.right_message.setText(message.getMessageTime() + "\n" + message.getContent());

            //注意此处隐藏左边的消息布局用的是 View.GONE
            holder.leftLayout.setVisibility(View.GONE);
        }
        else{
            Log.d(TAG, "Receive a message"+message.getMessageTime());
            //如果是收到的消息，则显示左边的消息布局，将右边的消息布局隐藏
            holder.leftLayout.setVisibility(View.VISIBLE);
            holder.left_message.setText(message.getMessageTime() + "\n" + message.getContent());

            //注意此处隐藏右面的消息布局用的是 View.GONE
            holder.rightLayout.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        LinearLayout leftLayout, rightLayout;
        TextView left_message, right_message;

        public ViewHolder(View view){
            super(view);
            leftLayout = view.findViewById(R.id.left_layout);
            left_message = view.findViewById(R.id.left_message);
            rightLayout = view.findViewById(R.id.right_layout);
            right_message = view.findViewById(R.id.right_message);
        }
    }
}
