package com.example.audiostreamapp.ui.home;

import static com.example.audiostreamapp.MainActivity.favList;
import static com.example.audiostreamapp.data.model.currentMediaPlayer.getMediaName;
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
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.audiostreamapp.MainActivity;
//import com.example.audiostreamapp.PlaySongActivity;
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

        // Set item views based on your views and data model
        TextView textView = holder.nameTextView;
        textView.setText(audioFile.getName());
//        Button button = holder.messageButton;
//        button.setText("Play");
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
//        public Button messageButton;
        public ImageButton imageButton;



        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public ViewHolder(View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);

            nameTextView = (TextView) itemView.findViewById(R.id.audio_name);
//            messageButton = (Button) itemView.findViewById(R.id.live_play_button);
            imageButton = (ImageButton) itemView.findViewById(R.id.imageButton) ;

//             messageButton.setOnClickListener(new View.OnClickListener() {
//                public void onClick(View v) {
//                    currentMediaPlayer.changeMedia((String) nameTextView.getText());
//                    nameTextView.getText();
//                }
//            });
            imageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//                    currentMediaPlayer.getMediaName();
                    //boolean a =currentMediaPlayer.changeToMedia((String) nameTextView.getText());
                    String SongName = nameTextView.getText().toString();
//                    String SongName = view.getContentDescription().toString();
                    for (AudioFile af : audioFiles) {
                        boolean matcher = SongName.equals(af.getName());
                        if (matcher) {

                            if(favList.contains(SongName)){
                                break;
                            }
                            else{
                                MainActivity.favList.add(new AudioFile(af.getName()));
//                                //用gson，将文件转成String列表文件
//                                Gson gson = new Gson();
//                                String json = gson.toJson(favList);
//                                Log.d("gson", json);
                            }
                        }
                    }
                }
            });
        }
    }

}
