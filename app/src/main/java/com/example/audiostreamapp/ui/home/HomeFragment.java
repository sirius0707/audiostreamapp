package com.example.audiostreamapp.ui.home;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    ArrayList<AudioFile> audioFiles;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();


        return root;
    }


    @Override
    public void onStart() {
        super.onStart();
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
                        AudioFileAdapter adapter = new AudioFileAdapter(audioFiles,getActivity());
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
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
