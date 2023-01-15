package com.example.audiostreamapp.ui.home;

import static com.example.audiostreamapp.MainActivity.favList;
import static com.example.audiostreamapp.ui.home.HomeFragment.audioFiles;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.audiostreamapp.MainActivity;
import com.example.audiostreamapp.R;
import com.example.audiostreamapp.data.model.currentMediaPlayer;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
//import com.google.gson.Gson;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class AudioFileAdapter extends
        RecyclerView.Adapter<AudioFileAdapter.ViewHolder> {

    // Store a member variable for the contacts
    private List<AudioFile> mContacts;
    private Activity mContext;

    // Pass in the contact array into the constructor
    public AudioFileAdapter(List<AudioFile> contacts, Activity context) {
        mContacts = contacts;
        this.mContext = context;

    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View contactView = inflater.inflate(R.layout.item_album, parent, false);

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(contactView);
        return viewHolder;
    }

    @SuppressLint("ResourceType")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Get the data model based on position
        AudioFile audioFile = mContacts.get(position);

        TextView textView = holder.nameTextView;
        textView.setText(audioFile.getName());
        ImageButton imageView = holder.imageButton;
        imageView.getContext();

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentMediaPlayer.fromList=false;
                currentMediaPlayer.changeMedia((String) holder.nameTextView.getText());
                holder.nameTextView.getText();
            }
        });

    }


    @Override
    public int getItemCount() {
        return mContacts.size();
    }


    // Provide a direct reference to each of the views within a data item
    // Used to cache the views within the item layout for fast access
    public class ViewHolder extends RecyclerView.ViewHolder {
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        public TextView nameTextView;
        public ImageButton imageButton;



        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public ViewHolder(View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);

            nameTextView = (TextView) itemView.findViewById(R.id.audio_name);
            imageButton = (ImageButton) itemView.findViewById(R.id.imageButton) ;

            imageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String SongName = nameTextView.getText().toString();
                    for (AudioFile af : audioFiles) {
                        boolean matcher = SongName.equals(af.getName());
                        if (matcher) {
                            if(MainActivity.getAudioPos(SongName,favList)!=-1){
                                break;
                            }
                            else{
                                MainActivity.favList.add(new AudioFile(af.getName()));
                            }
                        }
                    }
                }
            });
        }
    }
}
