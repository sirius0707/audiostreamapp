package com.example.audiostreamapp.liveComment;

import java.text.SimpleDateFormat;
import java.util.Date;

public class LiveComment {
    String userID;
    String commentTime;
    String commentText;
    String userName;

    public LiveComment(String userID, long miliTime, String commentText, String userName){
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        commentTime=dateFormat.format(new Date(miliTime*1000));
        this.userID = userID;
        this.commentText = commentText;
        this.userName = userName;
    }


    public String getUserID() {
        return userID;
    }

    public String getUserName() {
        return userName;
    }

    public String getCommentText() {
        return commentText;
    }

    public String getCommentTime() {return commentTime;}

}
