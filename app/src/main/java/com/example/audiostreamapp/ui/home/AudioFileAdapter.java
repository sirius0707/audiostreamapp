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
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.audiostreamapp.MainActivity;
import com.example.audiostreamapp.R;
import com.example.audiostreamapp.data.model.currentMediaPlayer;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
//import com.google.gson.Gson;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

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
        // Get the data model based on position
        AudioFile audioFile = mContacts.get(position);

        TextView textView = holder.nameTextView;
        textView.setText(audioFile.getName());
        ImageButton imageView = holder.imageButton;
        imageView.getContext();

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentMediaPlayer.fromList = false;
                Map<String, Object> updates = new HashMap<>();
                if (HomeFragment.contentMode.getCheckedRadioButtonId() == R.id.musicBtn) {
                    updates.put("music/" + currentMediaPlayer.
                            getMediaName().
                            replace(".mp3", "") + "/playedTimes", ServerValue.increment(1));
                } else if (HomeFragment.contentMode.getCheckedRadioButtonId() == R.id.audiobookBtn) {
                    updates.put("audiobooks/" + currentMediaPlayer.
                            getMediaName().
                            replace(".mp3", "") + "/playedTimes", ServerValue.increment(1));
                } else {
                    Log.e("Storage error", "Specified storage is not found");
                }

                mDatabase.updateChildren(updates);
                StorageReference storageRef = null;
                if (HomeFragment.contentMode.getCheckedRadioButtonId() == R.id.musicBtn) {
                    storageRef = FirebaseStorage.getInstance().getReference().child("musicRepo/" + textView.getText());
                } else if (HomeFragment.contentMode.getCheckedRadioButtonId() == R.id.audiobookBtn) {
                    storageRef = FirebaseStorage.getInstance().getReference().child("audioBooks/" + textView.getText());
                } else {
                    Log.e("Storage error", "Specified storage is not found");
                }
                storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                // Download url of file
                                String url = uri.toString();
                                Log.e("URL", url);
                                currentMediaPlayer.setMediaPlayerURL(url, (String) textView.getText());
                                currentMediaPlayer.getMediaPlayer().setOnPreparedListener(new MediaPlayer.OnPreparedListener() {

                                    @Override
                                    public void onPrepared(MediaPlayer mp) {
                                        mp.start();
                                        try {
                                            TimeUnit.MILLISECONDS.sleep(10);
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }
                                        ((MainActivity) mContext).resetDurationOfAudioPlayer();

                                    }
                                });


                            }
                        })

                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.i("TAG", e.getMessage());
                            }
                        });
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
        public Button messageButton;


        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public ViewHolder(View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);

            nameTextView = (TextView) itemView.findViewById(R.id.audio_name);
            imageButton = (ImageButton) itemView.findViewById(R.id.imageButton);
            imageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String SongName = nameTextView.getText().toString();
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



