package com.example.audiostreamapp;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class MyView extends RecyclerView.ViewHolder{

    public TextView audio_name;
    public ImageButton delete_fav;


    public MyView(@NonNull View itemView) {
        super(itemView);

        audio_name = itemView.findViewById(R.id.audio_name);
        delete_fav = itemView.findViewById(R.id.delete_fav);
    }
}
