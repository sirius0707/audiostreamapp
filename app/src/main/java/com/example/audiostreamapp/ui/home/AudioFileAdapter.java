package com.example.audiostreamapp.ui.home;


import static com.example.audiostreamapp.MainActivity.favList;
import static com.example.audiostreamapp.MainActivity.play_name;
import static com.example.audiostreamapp.ui.home.HomeFragment.audioFiles;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.audiostreamapp.MainActivity;
import com.example.audiostreamapp.R;
import com.example.audiostreamapp.data.model.currentMediaPlayer;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;

import com.google.firebase.storage.StorageReference;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AudioFileAdapter extends RecyclerView.Adapter<AudioFileAdapter.ViewHolder> {

    // Store a member variable for the contacts
    private List<AudioFile> mContacts;
    private Activity mContext;
    private DatabaseReference mDatabase;

    // Pass in the contact array into the constructor
    public AudioFileAdapter(List<AudioFile> contacts, Activity context) {
        mContacts = contacts;
        this.mContext = context;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        mDatabase = FirebaseDatabase.getInstance("https://audiostreamapp-6a52b-default-rtdb.europe-west1.firebasedatabase.app/").getReference();
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
        if (position ==0){
            holder.container.setBackgroundResource(R.drawable.round_color_top);
        }

        if(position == mContacts.size()-1 ){
            holder.container.setBackgroundResource(R.drawable.round_corner_bottom);
            holder.divider.setVisibility(View.GONE);
        }

        // Get the data model based on position
        AudioFile audioFile = mContacts.get(position);

        TextView textView = holder.nameTextView;
        textView.setText(audioFile.getName().replace(".mp3",""));
        ImageButton imageView = holder.imageButton;
        imageView.getContext();
        //


        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentMediaPlayer.fromList = false;
                Map<String, Object> updates = new HashMap<>();
                String type="musicRepo";
                if (HomeFragment.contentMode.getCheckedRadioButtonId() == R.id.musicBtn) {
                    updates.put("music/" + textView.getText()
                            + "/playedTimes", ServerValue.increment(1));
                    type="musicRepo";
                } else if (HomeFragment.contentMode.getCheckedRadioButtonId() == R.id.audiobookBtn) {
                    type="audioBooks";
                    updates.put("audiobooks/" + textView.getText()+ "/playedTimes", ServerValue.increment(1));

                } else {
                    Log.e("Storage error", "Specified storage is not found");
                }
                mDatabase.updateChildren(updates);
                currentMediaPlayer.changeMedia(type,textView.getText()+ ".mp3");
                play_name.setText(holder.nameTextView.getText());
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
        public Button messageButton;
        RelativeLayout container;
        View divider;


        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public ViewHolder(View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);

            container = itemView.findViewById(R.id.container);
            divider = itemView.findViewById(R.id.divider);

            nameTextView = (TextView) itemView.findViewById(R.id.audio_name);
            imageButton = (ImageButton) itemView.findViewById(R.id.imageButton);
            imageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String SongName = nameTextView.getText().toString()+ ".mp3";
                    for (AudioFile af : audioFiles) {
                        boolean matcher = SongName.equals(af.getName());
                        if (matcher) {
                            if (MainActivity.getAudioPos(SongName, favList) != -1) {
                                break;
                            } else {
                                favList.add(new AudioFile(af.getName()));
                            }
                        }
                    }
                }
            });
        }

    }
}



