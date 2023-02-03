package com.example.audiostreamapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.net.MalformedURLException;
import java.net.URL;

public class ModifyProfileActivity extends AppCompatActivity {
    ImageView avatarImageView;
    TextView nameTextView, emailTextView;
    TextInputLayout nameTextInput, emailTextInput;
    TextInputEditText nameEditText, emailEditText;
    Button modifyprofileButton, back2notificationButton;
    private DatabaseReference mDatabase;

    private static final int PICK_PDF_FILE = 2;

    private static final String TAG = "ModifyProfileActivity";

    //Open File and upload
    private void openFile(URL pickerInitialUri, ImageView IV) {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");

        // Optionally, specify a URI for the file that should appear in the
        // system file picker when it loads.
        intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, pickerInitialUri);

        IV.setImageURI(intent.getData());
        startActivityForResult(intent, PICK_PDF_FILE);
    }


    protected void onActivityResult(int requestCode, int resultCode,
                                 Intent resultData) {
        super.onActivityResult(requestCode, resultCode, resultData);
        if (requestCode == PICK_PDF_FILE
                && resultCode == Activity.RESULT_OK) {
            // The result data contains a URI for the document or directory that
            // the user selected.
            Uri file = null;
            if (resultData != null) {
                file = resultData.getData();
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                StorageReference storageRef = FirebaseStorage.getInstance().getReference().child("userAvatar/"+ user.getUid()+".jpg");
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
                        showSnackbar("Avatar Modified.");
                    }
                });
                // Perform operations on the document using its URI.

                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                        .setPhotoUri(Uri.parse("Not null"))
                        .build();
                user.updateProfile(profileUpdates);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_profile);


    }

    @Override
    protected void onStart() {
        super.onStart();
        avatarImageView = findViewById(R.id.ImageView_Avatar);
        nameTextView = findViewById(R.id.TextView_Name);
        nameTextInput = findViewById(R.id.TextInput_Name);
        nameEditText = findViewById(R.id.EditText_Name);
        emailTextView = findViewById(R.id.TextView_Email);
        emailTextInput = findViewById(R.id.TextInput_Email);
        emailEditText = findViewById(R.id.EditText_Email);
        modifyprofileButton = findViewById(R.id.Button_Modify_Profile);
        back2notificationButton = findViewById(R.id.Button_Back_To_Notification);

        //get and show users' profile
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            getUserProfile();
            // Name
            String current_name = user.getDisplayName();
            nameTextView.setText(current_name);
            // Email
            String current_email = user.getEmail();
            emailTextView.setText(current_email);
            // Avatar
            // Get a reference to the FirebaseStorage object
            FirebaseStorage storage = FirebaseStorage.getInstance();
            // Create a StorageReference to the project URL and the file to download
            StorageReference storageRef;
            if(user.getPhotoUrl() != null)
                storageRef = storage.getReferenceFromUrl("gs://audiostreamapp-6a52b.appspot.com/userAvatar").child(user.getUid()+".jpg");
            else
                storageRef = storage.getReferenceFromUrl("gs://audiostreamapp-6a52b.appspot.com/userAvatar").child("Default.jpeg");

            // Get uri and display pictures with Picasso
            storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Log.e("Tuts+", "uri: " + uri.toString());
                    //Handle whatever you're going to do with the URL here
                    Picasso.get().load(uri.toString()).resize(400, 400).into(avatarImageView);
                }
            });

        }

        // Button: Modify Avatar
        avatarImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getUserProfile();
                try {
                    URL defaultURL=Environment.getExternalStorageDirectory().toURI().toURL();
                    openFile(defaultURL, avatarImageView);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
            }
        });

        // Button: Modify Profile
        modifyprofileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = nameEditText.getText().toString();
                String new_email = emailEditText.getText().toString();

                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                if(name.length() != 0)
                {
                    updateProfile(name);
                    showSnackbar("Name has been modified.");
                }

                if(new_email.length() != 0){
                    Dialog d = new AlertDialog.Builder(ModifyProfileActivity.this)
                            .setIcon(android.R.drawable.ic_dialog_info)
                            .setTitle("Update E-mail Confirmation")
                            .setMessage("Are you sure you want to update E-mail address?")
                            .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    updateEmail(new_email);
                                    showSnackbar("E-mail address has been updated!");
                                }
                            })
                            .setNegativeButton("Cancel", null)
                            .show();
                }

                if((name.length()==0)&&(new_email.length()==0))
                    showSnackbar("Nothing to modify!");
            }
        });

        // Button: Back
        back2notificationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }

    // Show message
    private void showSnackbar(String errorMessageRes) {
        Toast.makeText(getApplicationContext(), errorMessageRes, Toast.LENGTH_SHORT).show();
    }

    // Get User Profile
    public void getUserProfile() {
        // [START get_user_profile]
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // Name, email address, and profile photo Url
            String name = user.getDisplayName();
            String email = user.getEmail();
            Uri photoUrl = user.getPhotoUrl();

            // Check if user's email is verified
            boolean emailVerified = user.isEmailVerified();

            // The user's ID, unique to the Firebase project. Do NOT use this value to
            // authenticate with your backend server, if you have one. Use
            // FirebaseUser.getIdToken() instead.
            String uid = user.getUid();

            Log.d(TAG, "Current profile:");
            Log.d(TAG, "    Name:"+name);
            Log.d(TAG, "    Email:"+email);
            Log.d(TAG, "    UID:"+uid);
            Log.d(TAG, "    Photo:"+photoUrl);

        }
        // [END get_user_profile]
    }

    // Update Profile with new name
    public void updateProfile(String name) {
        // [START update_profile]
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(name)
                .build();

        user.updateProfile(profileUpdates)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "User profile updated.");
                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            mDatabase = FirebaseDatabase.getInstance("https://audiostreamapp-6a52b-default-rtdb.europe-west1.firebasedatabase.app/").getReference();
                            String uid=user.getUid();
                            Log.e("uid",uid);
                            mDatabase.child("users/"+uid+"/username").setValue(user.getDisplayName());
                        }
                    }
                });

        // [END update_profile]
    }

    public void updateEmail(String new_email) {
        // [START update_email]
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        user.updateEmail(new_email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "User email address updated.");
                        }
                    }
                });
        // [END update_email]
    }

    // Send Email verification (abandoned)
    public void sendEmailVerification() {
        // [START send_email_verification]
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        user.sendEmailVerification()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "Email sent.");
                        }
                    }
                });
        // [END send_email_verification]
    }

}