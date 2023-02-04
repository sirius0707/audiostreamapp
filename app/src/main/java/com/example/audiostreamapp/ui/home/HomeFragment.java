package com.example.audiostreamapp.ui.home;

import static com.example.audiostreamapp.data.model.currentMediaPlayer.getMediaName;
import static com.firebase.ui.auth.AuthUI.getApplicationContext;
import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toCollection;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.audiostreamapp.R;
import com.example.audiostreamapp.databinding.FragmentHomeBinding;
import com.example.audiostreamapp.ui.home.notifications.UploadDialogFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;

    private DatabaseReference mDatabase = FirebaseDatabase.getInstance("https://audiostreamapp-6a52b-default-rtdb.europe-west1.firebasedatabase.app/").getReference();
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReference();

    public static ArrayList<AudioFile> audioFiles;
    ArrayList<AudioFile> filteredAudioFiles;
    ArrayList<String> recommendedMusic;
    ArrayList<String> recommendedAudioBooks;
    String searchPara;
    public static ArrayList<AudioFile> Search;

    public static RadioGroup contentMode;
    int limit = 8;
    String pageToken = null;

    NestedScrollView nestedScrollView;
    EditText textSearch;
    RecyclerView albumList;
    RecyclerView albumRecList;

    Activity currentActivity=this.getActivity();

    public static HomeFragment newInstance() {
        HomeFragment frag = new HomeFragment();
        Bundle args = new Bundle();
        //args.putParcelable("uri",file);
        frag.setArguments(args);
        return frag;
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    private void changeType(Query newTypeQuery,Query BiRecommendQuery,String repoName){
        Log.e("here",newTypeQuery.toString());
        BiRecommendQuery.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                }
                else {
                    recommendedMusic.clear();
                    for (DataSnapshot postSnapshot: task.getResult().getChildren()) {
                        recommendedMusic.add(postSnapshot.getKey());
                    }
                    newTypeQuery.limitToLast(6-recommendedMusic.size()).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DataSnapshot> task) {
                            if (!task.isSuccessful()) {
                                Log.e("firebase", "Error getting data", task.getException());
                            }
                            else {
                                Log.e("here",task.getResult().getKey());
                                for (DataSnapshot postSnapshot: task.getResult().getChildren()) {
                                    recommendedMusic.add(postSnapshot.getKey());
                                }

                                storageRef.child(repoName).listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
                                            @Override
                                            public void onSuccess(ListResult listResult) {
                                                ArrayList<AudioFile> recAudioFiles=new ArrayList<>();
                                                for (String rec:recommendedMusic){
                                                    for (StorageReference item : listResult.getItems()) {
                                                        // All the items under listRef.
                                                        if (rec.equals(item.getName().replace(".mp3","")))
                                                            recAudioFiles.add(new AudioFile(item.getName()));
                                                    }
                                                }

                                                // Delete redundant data due to repeated name
                                                //Collections.reverse(recAudioFiles);
                                                AudioFileAdapter adapter = new AudioFileAdapter(recAudioFiles,getActivity());
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
                        }
                    });

                }
            }
        });


        // Pagination: Show first "limit" files
        getAudioFile(repoName, limit);
        // Scroll to get more files
        ScrollListener(repoName);
        // Search
        getSearchResult(repoName);

        AudioFileAdapter adapter = new AudioFileAdapter(filteredAudioFiles,getActivity());
        albumList.setAdapter(adapter);
        albumList.setLayoutManager(new LinearLayoutManager(currentActivity));
    }

    private void checkStatueAndRefresh(){
        Query musicQuery = mDatabase.child("music/").orderByChild("playedTimes");
        Query audioBooksQuery = mDatabase.child("audiobooks/").orderByChild("playedTimes");
        Query BiRecommendQuery = mDatabase.child("recommend/"+getMediaName().replace(".mp3","")).orderByValue().limitToLast(5);
        switch (contentMode.getCheckedRadioButtonId()){
            case R.id.musicBtn:
                changeType(musicQuery,BiRecommendQuery,"musicRepo");
                break;
            case R.id.audiobookBtn:
                changeType(audioBooksQuery,BiRecommendQuery,"audioBooks");
                break;
            default:
                break;
        }
    }


    @Override
    public void onStart() {
        super.onStart();
        textSearch = getView().findViewById(R.id.textSearch);
        contentMode = getView().findViewById(R.id.contentModeBtn);
        nestedScrollView = getView().findViewById(R.id.scroll_view);
        albumList = getView().findViewById(R.id.album_list);
        albumRecList = getView().findViewById(R.id.album_recommend_list);
        getView().findViewById(R.id.RecommendTextView).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkStatueAndRefresh();
            }
        });

        //init audiofile list from Firebase Storage

        pageToken = null;
        audioFiles = new ArrayList<>();
        filteredAudioFiles = new ArrayList<>();
        recommendedMusic = new ArrayList<>();
        recommendedAudioBooks = new ArrayList<>();

        Search = new ArrayList<>();

        checkStatueAndRefresh();

        // When changing buttons, invoke this
        contentMode.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                pageToken = null;
                audioFiles.clear();
                filteredAudioFiles.clear();
                checkStatueAndRefresh();
            }
        });

    }

    private void getAudioFile(String type, int limit) {
        // Fetch the next page of results, using the pageToken if we have one.

        Task<ListResult> listPageTask = pageToken != null
                ? storageRef.child(type).list(limit, pageToken)
                : storageRef.child(type).list(limit);

        listPageTask.addOnSuccessListener(new OnSuccessListener<ListResult>() {
                    @Override
                    public void onSuccess(ListResult listResult) {
                        pageToken = listResult.getPageToken();
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

    private void ScrollListener(String type){
        nestedScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(@NonNull NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if(scrollY == v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight()){
                    if (pageToken != null) {
                        showSnackbar("Loading...");
                        getAudioFile(type, limit);
                    }
                    else
                        showSnackbar("End");
                }
            }
        });
    }

    private void getSearchResult(String type) {
        Search.clear();
        storageRef.child(type).listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
            @Override
            public void onSuccess(ListResult listResult) {
                for (StorageReference item : listResult.getItems()) {
                    // All the items under listRef.
                    Search.add(new AudioFile(item.getName()));
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                showSnackbar("Error: Can't get the data from database!");
            }
        });
        // Listen if search area is changed
        searchPara = new String();
        textSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                searchPara = charSequence.toString();
                // Fuzzy query
                Pattern pattern = Pattern.compile(searchPara, Pattern.CASE_INSENSITIVE);
                filteredAudioFiles.clear();
                for (AudioFile af : Search) {
                    Matcher matcher = pattern.matcher(af.getName());

                    if (matcher.find()) {
                        filteredAudioFiles.add(new AudioFile(af.getName()));
                    }
                }

                AudioFileAdapter adapter = new AudioFileAdapter(filteredAudioFiles,getActivity());
                albumList.setAdapter(adapter);

            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });
    }

    // Show message
    private void showSnackbar(String errorMessageRes) {
        Toast.makeText(getActivity().getApplicationContext(), errorMessageRes, Toast.LENGTH_SHORT).show();
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
