package com.example.audiostreamapp.ui.home.notifications;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.audiostreamapp.MainActivity;
import com.example.audiostreamapp.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link UploadDialogFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UploadDialogFragment extends DialogFragment {


    // TODO: Rename and change types of parameters

    private String mParam2;

    public UploadDialogFragment() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static UploadDialogFragment newInstance(Uri file) {
        UploadDialogFragment frag = new UploadDialogFragment();
        Bundle args = new Bundle();
        args.putParcelable("uri",file);
        frag.setArguments(args);
        return frag;
    }
    Activity currentActivity;
    private void uploadFile(Uri file,String type){
        currentActivity=getActivity();
        StorageReference storageRef = FirebaseStorage.getInstance().getReference().child(type+new File(file.getPath()).getName());
        UploadTask uploadTask = storageRef.putFile(file);

// Register observers to listen for when the download is done or if it fails
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                ((MainActivity)currentActivity).showSnackbar("Uploading done!");
            }
        });
        // Perform operations on the document using its URI.
    }

    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder
                .setTitle(R.string.select_file_property)
                .setPositiveButton(R.string.upload_as_music, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        uploadFile(getArguments().getParcelable("uri"),"musicRepo/");
                    }
                })
                .setNegativeButton(R.string.upload_as_audiobook, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        uploadFile(getArguments().getParcelable("uri"),"audioBooks/");
                    }
                })
                .setNeutralButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                });
        return builder.create();
    }
}