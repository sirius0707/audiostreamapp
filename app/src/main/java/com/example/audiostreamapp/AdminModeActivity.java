package com.example.audiostreamapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import com.example.audiostreamapp.adminMode.Report;
import com.example.audiostreamapp.adminMode.ReportAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class AdminModeActivity extends AppCompatActivity {
    boolean onBottom =true;
    private DatabaseReference mDatabase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_mode);

        mDatabase = FirebaseDatabase.getInstance("https://audiostreamapp-6a52b-default-rtdb.europe-west1.firebasedatabase.app/").getReference();

        RecyclerView requestList = this.findViewById(R.id.report_list);
        ArrayList<Report> items = new ArrayList<>();
        ReportAdapter adapter = new ReportAdapter(items,this);
        requestList.setAdapter(adapter);
        requestList.setLayoutManager(new LinearLayoutManager(this));

        //display requests/reports
        requestList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (!recyclerView.canScrollVertically(1)) {
                    onBottom =true;
                }
                else{
                    onBottom =false;
                }
            }
        });

        //init requests from Firebase realtime database
        mDatabase.child("reports/")
                .limitToLast(6).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // A new report has been added, add it to the reports
                        for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
                            Object uid = snapshot.getKey();
                            Object livecomment = snapshot.child("livecomments").getValue();
                            Object isBlocked = snapshot.child("isBlocked").getValue();
                            items.add(new Report(uid.toString(),
                                    livecomment.toString(),
                                    isBlocked.toString()
                                    ));
                        }
                        Log.d("items: ", String.valueOf(items.size()));
                        adapter.notifyItemRangeInserted(items.size()-1,1);
                        if (onBottom)
                            requestList.scrollToPosition(items.size() - 1);
                    }


                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
}