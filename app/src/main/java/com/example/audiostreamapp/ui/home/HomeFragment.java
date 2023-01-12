package com.example.audiostreamapp.ui.home;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.audiostreamapp.R;
import com.example.audiostreamapp.databinding.FragmentHomeBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    public static ArrayList<AudioFile> audioFiles;
    ArrayList<AudioFile> filteredAudioFiles;
    String searchPara;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentHomeBinding.inflate(inflater, container, false);

        return binding.getRoot();
    }


    @Override
    public void onStart() {
        super.onStart();
        EditText textSearch = getView().findViewById(R.id.textSearch);
        RecyclerView albumList = (RecyclerView) this.getView().findViewById(R.id.album_list);
        Activity currentActivity=this.getActivity();
        //init audiofile list from Firebase Storage
        StorageReference storageRef = storage.getReference();
        storageRef.child("musicRepo").listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
                    @Override
                    public void onSuccess(ListResult listResult) {
                        audioFiles=new ArrayList<>();
                        for (StorageReference item : listResult.getItems()) {
                            // All the items under listRef.
                            audioFiles.add(new AudioFile(item.getName()));

                        }

                        filteredAudioFiles = new ArrayList<>();
                        searchPara = new String();
                        // Listen if search area is changed
                        textSearch.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

                            @Override
                            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                                searchPara = charSequence.toString();
                                // Fuzzy query
                                Pattern pattern = Pattern.compile(searchPara, Pattern.CASE_INSENSITIVE);
                                filteredAudioFiles.clear();
                                for (AudioFile af : audioFiles) {
                                    Matcher matcher = pattern.matcher(af.getName());

                                    if (matcher.find()) {
                                        filteredAudioFiles.add(new AudioFile(af.getName()));
                                    }
                                }


                                AudioFileAdapter adapter = new AudioFileAdapter(filteredAudioFiles,getActivity());
                                albumList.setAdapter(adapter);
                                albumList.setLayoutManager(new LinearLayoutManager(currentActivity));
                            }

                            @Override
                            public void afterTextChanged(Editable editable) {}
                        });


                        // Fuzzy query
                        Pattern pattern = Pattern.compile(searchPara, Pattern.CASE_INSENSITIVE);

                        for (AudioFile af : audioFiles) {
                            Matcher matcher = pattern.matcher(af.getName());

                            if (matcher.find()) {
                                filteredAudioFiles.add(new AudioFile(af.getName()));
                            }
                        }


                        AudioFileAdapter adapter = new AudioFileAdapter(filteredAudioFiles,getActivity());
                        albumList.setAdapter(adapter);
                        albumList.setLayoutManager(new LinearLayoutManager(currentActivity));
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Uh-oh, an error occurred!
                    }
                });


    }


    @Override
    public void onResume() {
        super.onResume();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
