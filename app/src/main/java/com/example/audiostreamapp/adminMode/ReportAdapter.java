package com.example.audiostreamapp.adminMode;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.audiostreamapp.R;
import com.example.audiostreamapp.data.model.currentMediaPlayer;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import java.util.List;

public class ReportAdapter extends RecyclerView.Adapter<ReportAdapter.ViewHolder> {

    // Store a member variable for the requests
    private List<Report> reports;
    private Activity mContext;

    // Pass in the contact array into the constructor
    public ReportAdapter(List<Report> requests, Activity context) {
        reports = requests;
        this.mContext = context;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View contactView = inflater.inflate(R.layout.item_request, parent, false);

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(contactView);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ReportAdapter.ViewHolder holder, int position) {
        // Get the data model based on position
        Report report = reports.get(position);

        // Set item views based on your views and data model
        TextView textView = holder.reportTextView;
        textView.setText("UID: " + report.getUid() +", isBlocked: " + report.getIsBlocked()  + "\n"
                + "Live-comment: " + report.getLiveComment());
        Button button = holder.blockButton;

        DatabaseReference mDatabase = FirebaseDatabase.getInstance("https://audiostreamapp-6a52b-default-rtdb.europe-west1.firebasedatabase.app/").getReference();

        /*mDatabase.child("permissionsLivechat/" + report.getUid() + "/isBlocked").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot datasnapshot: snapshot.getChildren()) {
                    Log.d("if read the permissionslivechat", "+++++++++++ ");
                    isBlocked[0] = (Boolean) snapshot.getValue();
                    if (isBlocked[0]){
                        button.setText("UNBLOCK");
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });*/
        if (report.getIsBlocked().equals("unblocked") || report.getIsBlocked().equals("reported")){
            button.setText("BLOCK");
        }
        if (report.getIsBlocked().equals("blocked")){
            button.setText("UNBLOCK");
        }


        holder.blockButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reports.clear();
                if (report.getIsBlocked().equals("unblocked") || report.getIsBlocked().equals("reported")){
                    mDatabase.child("reports/" + report.getUid() + "/isBlocked").setValue("blocked");
                    button.setText("UNBLOCK");
                }
                if (report.getIsBlocked().equals("blocked")){
                    mDatabase.child("reports/" + report.getUid() + "/isBlocked").setValue("unblocked");
                    button.setText("BLOCK");
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return reports.size();
    }

    // Provide a direct reference to each of the views within a data item
    // Used to cache the views within the item layout for fast access
    public class ViewHolder extends RecyclerView.ViewHolder {
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        public TextView reportTextView;
        public Button blockButton;

        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public ViewHolder(View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);

            reportTextView = (TextView) itemView.findViewById(R.id.report);
            blockButton = (Button) itemView.findViewById(R.id.block_button);
            /*blockButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {

                    DatabaseReference mDatabase = FirebaseDatabase.getInstance("https://audiostreamapp-6a52b-default-rtdb.europe-west1.firebasedatabase.app/").getReference();

                    mDatabase.child("permissions/" + report.getUid() + "/livecomments").setValue();
                    requestTextView.getText();
                    Toast.makeText(mContext, "This user has been blocked!", Toast.LENGTH_SHORT).show();
                }
            }); */
        }
    }
}
