package com.example.audiostreamapp.ui.home.notifications;

import static com.firebase.ui.auth.AuthUI.getApplicationContext;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.example.audiostreamapp.AdminModeActivity;
import com.example.audiostreamapp.ModifyProfileActivity;
import com.example.audiostreamapp.R;
import com.example.audiostreamapp.databinding.FragmentNotificationsBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

public class NotificationsFragment extends Fragment {

    private FragmentNotificationsBinding binding;

    private static final int PICK_PDF_FILE = 2;
    private static final String TAG = "NotificationsFragment";
    private Activity currentActivity;

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
                DialogFragment dialogEvent = UploadDialogFragment.newInstance(file);
                dialogEvent.show(getActivity().getSupportFragmentManager(), "uploadDialog");

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
        ImageView Image_Avatar = (ImageView) view.findViewById(R.id.Image_Avatar);
        Button modifyprofileButton = (Button) view.findViewById(R.id.Button_To_Modify_Profile);
        Button fileButton = (Button) view.findViewById(R.id.Button_Upload_File);
        Button resetpwdButton = (Button) view.findViewById(R.id.Button_To_Reset_PWD);
        Button signOutButton = (Button) view.findViewById(R.id.Button_Sign_Out);
        Button adminModeButton = view.findViewById(R.id.Button_To_Admin_Mode);
        currentActivity = getActivity();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        // Show Avatar
        if (user != null) {
            // Get a reference to the FirebaseStorage object
            FirebaseStorage storage = FirebaseStorage.getInstance();
            // Create a StorageReference to the project URL and the file to download
            StorageReference storageRef;
            if(user.getPhotoUrl() != null)
                storageRef = storage.getReferenceFromUrl("gs://audiostreamapp-6a52b.appspot.com/userAvatar").child(user.getUid()+".jpg");
            else
                storageRef = storage.getReferenceFromUrl("gs://audiostreamapp-6a52b.appspot.com/userAvatar").child("Default.jpeg");
            storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Log.e("Tuts+", "uri: " + uri.toString());
                    //Handle whatever you're going to do with the URL here
                    Picasso.get().load(uri.toString()).resize(400, 400).into(Image_Avatar);
                }
            });
        }

        // Button: Upload Video
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

        // Button: To Modify Profile
        modifyprofileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent modifyprofile_intent = new Intent(currentActivity, ModifyProfileActivity.class);
                startActivity(modifyprofile_intent);
            }
        });

        // Button: Reset Password
        resetpwdButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dialog d = new AlertDialog.Builder(currentActivity)
                        .setIcon(android.R.drawable.ic_dialog_info)
                        .setTitle("Reset Password")
                        .setMessage("Are you sure you want to reset password? You need to login again.")
                        .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                sendPasswordReset(user.getEmail());
                            }
                        })
                        .setNegativeButton("Cancel", null)
                        .show();
            }
        });

        // Button: Sign Out
        signOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dialog d = new AlertDialog.Builder(currentActivity)
                        .setIcon(android.R.drawable.ic_dialog_info)
                        .setTitle("Sign Out")
                        .setMessage("Are you sure you want to log out?")
                        .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                getActivity().finish();
                            }
                        })
                        .setNegativeButton("Cancel", null)
                        .show();
            }
        });

        // Button: Admin mode
        adminModeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dialog d = new AlertDialog.Builder(currentActivity)
                        .setIcon(android.R.drawable.ic_dialog_info)
                        .setTitle("Admin Mode")
                        .setMessage("Are you sure you want to enter admin mode?")
                        .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                Intent adminMode_intent = new Intent(currentActivity, AdminModeActivity.class);
                                if(user.getUid().equals("P3SVMDhUS7PPYu2CBKHUkLZ3Osn2")){
                                    startActivity(adminMode_intent);
                                } else{
                                    Context context = getApplicationContext();
                                    CharSequence text = "Sorry you don't have administrator access.";
                                    int duration = Toast.LENGTH_SHORT;
                                    Toast toast = Toast.makeText(context, text, duration);
                                    toast.show();
                                }
                            }
                        })
                        .setNegativeButton("Cancel", null)
                        .show();
            }
        });

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    // Show message
    private void showSnackbar(String errorMessageRes) {
        Toast.makeText(getApplicationContext(), errorMessageRes, Toast.LENGTH_SHORT).show();
    }

    // send email to reset pwd
    public void sendPasswordReset(String e_mail) {
        // [START send_password_reset]
        FirebaseAuth auth = FirebaseAuth.getInstance();

        auth.sendPasswordResetEmail(e_mail)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            showSnackbar("An Email for reseting password has been sent. Please log in.");
                            getActivity().finish();
                        }
                    }
                });
        // [END send_password_reset]
    }

}