package com.example.audiostreamapp;

import static com.example.audiostreamapp.MainActivity.play_name;

import android.annotation.SuppressLint;
import android.content.Context;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.audiostreamapp.data.model.currentMediaPlayer;
import com.example.audiostreamapp.ui.home.AudioFile;
import com.example.audiostreamapp.ui.home.notifications.NotificationsFragment;

import java.util.List;

public class SongAdapter extends RecyclerView.Adapter<MyView> {
    List<AudioFile> songs;
    Context context;
    public SongAdapter(List<AudioFile> songs) {
        this.songs = songs;
    }

    @NonNull
    @Override
    public MyView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View songView = inflater.inflate(R.layout.fav_item_album,parent,false);
        MyView viewHolder = new MyView(songView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyView holder, @SuppressLint("RecyclerView") int position) {
        if (position ==0){
            holder.fav_container.setBackgroundResource(R.drawable.round_color_top);
        }

        if(position == songs.size()-1 ){
            holder.fav_container.setBackgroundResource(R.drawable.round_corner_bottom);
            holder.fav_divider.setVisibility(View.GONE);
        }
        AudioFile song = songs.get(position);


        TextView textView = holder.audio_name;
        textView.setText(song.getName());


        //点击歌曲名播放
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //currentMediaPlayer.listPosition=holder.getAdapterPosition();

                currentMediaPlayer.changeMedia("musicRepo",(String) holder.audio_name.getText());
                currentMediaPlayer.changeMedia("audioBooks",(String) holder.audio_name.getText());
                currentMediaPlayer.fromList=true;
                holder.audio_name.getText();
                play_name.setText(holder.audio_name.getText());
            }
        });


        ImageButton imageButton = holder.delete_fav;
        holder.delete_fav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.favList.remove(position);
                notifyDataSetChanged();

            }
        });
    }

    @Override
    public int getItemCount() {
        return songs.size();
    }
}
