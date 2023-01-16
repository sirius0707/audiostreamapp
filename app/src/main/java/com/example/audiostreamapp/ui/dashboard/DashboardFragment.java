package com.example.audiostreamapp.ui.dashboard;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.example.audiostreamapp.R;
import com.example.audiostreamapp.data.model.User;
import com.example.audiostreamapp.data.model.UserAdapter;
import com.example.audiostreamapp.data.model.currentMediaPlayer;
import com.example.audiostreamapp.databinding.FragmentDashboardBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.io.Console;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class DashboardFragment extends Fragment {

    private FragmentDashboardBinding binding;

    private static final String TAG = "Chat";
    private Activity currentActivity;

    private RecyclerView userRecyclerView;
    private UserAdapter adapter;
    private LinearLayoutManager layoutManager;
    private DatabaseReference mDatabase;

    FirebaseUser currentuser = FirebaseAuth.getInstance().getCurrentUser();

    ArrayList<User> items = new ArrayList<>();

    boolean onButtom=true;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        currentActivity = getActivity();

        userRecyclerView = (RecyclerView) view.findViewById(R.id.user_recycler_view);
        layoutManager = new LinearLayoutManager(getContext());
        userRecyclerView.setLayoutManager(layoutManager);
        adapter = new UserAdapter(items, getActivity());
        userRecyclerView.setAdapter(adapter);


        mDatabase = FirebaseDatabase.getInstance("https://audiostreamapp-6a52b-default-rtdb.europe-west1.firebasedatabase.app/").getReference();

        // Display
        userRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (!recyclerView.canScrollVertically(1)) {
                    onButtom=true;
                }
                else{
                    onButtom=false;
                }
            }
        });

        // Get userlist
        mDatabase.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds:snapshot.getChildren()){
                    //snapshot.getChildrenCount用户数量 ds.getKey()用户id ds.child("username").getValue()用户名
                    items.add(new User(ds.getKey(), ds.child("username").getValue().toString()));

                    adapter.notifyItemRangeInserted(items.size()-1,1);
                    if (onButtom)
                        userRecyclerView.scrollToPosition(items.size() - 1);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}