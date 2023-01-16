package com.example.audiostreamapp.data.model;

import android.widget.EditText;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class ActionWithFirebase {
    private static DatabaseReference mDatabase = FirebaseDatabase.getInstance("https://audiostreamapp-6a52b-default-rtdb.europe-west1.firebasedatabase.app/").getReference();
    public static void sendMessage(String senderId, String receiverId, String content, EditText inputText){
        long timeStamp = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis());
        Map<String, Object> MessageAttribute = new HashMap<>();
        MessageAttribute.put("TimeStamp", timeStamp);
        MessageAttribute.put("Context", content);
        MessageAttribute.put("Sender", senderId);
        MessageAttribute.put("Receiver", receiverId);
        if(content!=null && content.length() != 0){
            mDatabase.child("message/" +senderId + "/" + receiverId + "/" + timeStamp)
                    .setValue(MessageAttribute).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            inputText.setText("");
                        };
                    });
            mDatabase.child("message/" + senderId + "/" + receiverId + "/" + "latest message")
                    .setValue(MessageAttribute).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            inputText.setText("");
                        };
                    });
            mDatabase.child("message/"+ receiverId + "/" + senderId + "/" + timeStamp)
                    .setValue(MessageAttribute).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            inputText.setText("");
                        };
                    });
            mDatabase.child("message/" + receiverId + "/" + senderId + "/" + "latest message")
                    .setValue(MessageAttribute).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            inputText.setText("");
                        };
                    });
        }

    }
}
