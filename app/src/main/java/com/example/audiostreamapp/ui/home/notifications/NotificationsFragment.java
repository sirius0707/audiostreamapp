package com.example.audiostreamapp.ui.home.notifications;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.audiostreamapp.LoginActivity;
import com.example.audiostreamapp.MainActivity;
import com.example.audiostreamapp.R;
import com.example.audiostreamapp.databinding.FragmentNotificationsBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

public class NotificationsFragment extends Fragment {

    private FragmentNotificationsBinding binding;
//    private FirebaseAuth mAuth;


    private static final int PICK_PDF_FILE = 2;

    private void openFile(URL pickerInitialUri) {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("audio/*");

        // Optionally, specify a URI for the file that should appear in the
        // system file picker when it loads.
        intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, pickerInitialUri);

        startActivityForResult(intent, PICK_PDF_FILE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode,
                                 Intent resultData) {
        if (requestCode == PICK_PDF_FILE
                && resultCode == Activity.RESULT_OK) {
            // The result data contains a URI for the document or directory that
            // the user selected.
            Uri file = null;
            if (resultData != null) {
                file = resultData.getData();
                StorageReference storageRef = FirebaseStorage.getInstance().getReference().child("musicRepo/"+new File(file.getPath()).getName());
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
                        // ...
                    }
                });
                // Perform operations on the document using its URI.
            }
        }
    }
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentNotificationsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();


        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Button fileButton = (Button) view.findViewById(R.id.FileToUploadButton);
        Button signOutButton = (Button) view.findViewById(R.id.sign_out_Button);
//        mAuth = FirebaseAuth.getInstance();
        fileButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try {
                    URL defaultURL=Environment.getExternalStorageDirectory().toURI().toURL();
                    openFile(defaultURL);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }

            }
        });

        signOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().finish();
            }
        });

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}