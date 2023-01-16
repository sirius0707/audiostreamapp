package com.example.audiostreamapp.ui.home;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toCollection;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.audiostreamapp.R;
import com.example.audiostreamapp.databinding.FragmentHomeBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    public static ArrayList<AudioFile> audioFiles;
    ArrayList<AudioFile> filteredAudioFiles;
    ArrayList<String> recommendedMusic;
    ArrayList<String> recommendedAudioBooks;
    String searchPara;
    private DatabaseReference mDatabase;
    public static RadioGroup contentMode;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentHomeBinding.inflate(inflater, container, false);

        return binding.getRoot();
    }


    @Override
    public void onStart() {
        super.onStart();
        EditText textSearch = getView().findViewById(R.id.textSearch);
        contentMode = getView().findViewById(R.id.contentModeBtn);
        RecyclerView albumList = (RecyclerView) this.getView().findViewById(R.id.album_list);
        RecyclerView albumRecList = (RecyclerView) this.getView().findViewById(R.id.album_recommend_list);
        mDatabase = FirebaseDatabase.getInstance("https://audiostreamapp-6a52b-default-rtdb.europe-west1.firebasedatabase.app/").getReference();
        Activity currentActivity=this.getActivity();

        //init audiofile list from Firebase Storage
        StorageReference storageRef = storage.getReference();
        Query musicQuery = mDatabase.child("music/").orderByChild("playedTimes").limitToLast(5);
        Query audioBooksQuery = mDatabase.child("audiobooks/").orderByChild("playedTimes").limitToLast(5);
        recommendedMusic = new ArrayList<>();
        recommendedAudioBooks = new ArrayList<>();

        // After opening fragment, invoke this
        int checkedButton = contentMode.getCheckedRadioButtonId();
        switch(checkedButton){
            case R.id.musicBtn:
                musicQuery.addValueEventListener(new ValueEventListener() {

                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                            recommendedMusic.add(postSnapshot.getKey());
                        }



                        storageRef.child("musicRepo").listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
                                    @Override
                                    public void onSuccess(ListResult listResult) {
                                        audioFiles=new ArrayList<>();
                                        for (StorageReference item : listResult.getItems()) {
                                            // All the items under listRef.
                                            for (String rec:recommendedMusic){
                                                if (rec.equals(item.getName().replace(".mp3","")))
                                                    audioFiles.add(new AudioFile(item.getName()));
                                            }
                                        }

                                        // Delete redundant data due to repeated name
                                        List<AudioFile> distinctAudioFiles = null;
                                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                                            distinctAudioFiles = audioFiles.stream().collect(
                                                    collectingAndThen(toCollection(() ->
                                                            new TreeSet<>(comparing(AudioFile::getName))), ArrayList::new));
                                        }

                                        AudioFileAdapter adapter = new AudioFileAdapter(distinctAudioFiles,getActivity());
                                        albumRecList.setAdapter(adapter);
                                        albumRecList.setLayoutManager(new LinearLayoutManager(currentActivity));
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
                    public void onCancelled(@NonNull DatabaseError error) {}
                });





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
                break;
            case R.id.audiobookBtn:
                System.out.println("R.id.audiobookBtn");
                break;
            default:
                break;
        }

        // When changing buttons, invoke this
        contentMode.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.musicBtn:
                        System.out.println("R.id.musicBtn");
                        musicQuery.addValueEventListener(new ValueEventListener() {

                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                                    recommendedMusic.add(postSnapshot.getKey());
                                }



                                storageRef.child("musicRepo").listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
                                            @Override
                                            public void onSuccess(ListResult listResult) {
                                                audioFiles = new ArrayList<>();
                                                for (StorageReference item : listResult.getItems()) {
                                                    // All the items under listRef.
                                                    for (String rec : recommendedMusic) {
                                                        if (rec.equals(item.getName().replace(".mp3", "")))
                                                            audioFiles.add(new AudioFile(item.getName()));
                                                    }
                                                }
                                                // Delete redundant data due to repeated name
                                                List<AudioFile> distinctAudioFiles = null;
                                                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                                                    distinctAudioFiles = audioFiles.stream().collect(
                                                            collectingAndThen(toCollection(() ->
                                                                    new TreeSet<>(comparing(AudioFile::getName))), ArrayList::new));
                                                }

                                                AudioFileAdapter adapter = new AudioFileAdapter(distinctAudioFiles, getActivity());
                                                albumRecList.setAdapter(adapter);
                                                albumRecList.setLayoutManager(new LinearLayoutManager(currentActivity));
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                // Uh-oh, an error occurred!
                                                Log.e("Exception", "Exception:"+e);
                                            }
                                        });

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {}
                        });





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

                        break;
                    case R.id.audiobookBtn:
                        System.out.println("R.id.audiobookBtn");
                        audioBooksQuery.addValueEventListener(new ValueEventListener() {

                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                                    recommendedAudioBooks.add(postSnapshot.getKey());
                                }



                                storageRef.child("audioBooks").listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
                                            @Override
                                            public void onSuccess(ListResult listResult) {
                                                audioFiles=new ArrayList<>();
                                                for (StorageReference item : listResult.getItems()) {
                                                    // All the items under listRef.
                                                    for (String rec:recommendedAudioBooks){
                                                        if (rec.equals(item.getName().replace(".mp3","")))
                                                            audioFiles.add(new AudioFile(item.getName()));
                                                    }
                                                }
                                                // Delete redundant data due to repeated name
                                                List<AudioFile> distinctAudioFiles = null;
                                                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                                                    distinctAudioFiles = audioFiles.stream().collect(
                                                            collectingAndThen(toCollection(() ->
                                                                    new TreeSet<>(comparing(AudioFile::getName))), ArrayList::new));
                                                }

                                                AudioFileAdapter adapter = new AudioFileAdapter(distinctAudioFiles,getActivity());
                                                albumRecList.setAdapter(adapter);
                                                albumRecList.setLayoutManager(new LinearLayoutManager(currentActivity));
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
                            public void onCancelled(@NonNull DatabaseError error) {}
                        });

                        storageRef.child("audioBooks").listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
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
                        break;
                    default:
                        break;

                }
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
