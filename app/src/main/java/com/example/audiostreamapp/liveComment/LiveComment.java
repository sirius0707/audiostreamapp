package com.example.audiostreamapp.liveComment;

import java.text.SimpleDateFormat;
import java.util.Date;

public class LiveComment {
    String userID;
    String commentTime;
    String commentText;

    public LiveComment(String userID,long miliTime,String commentText){
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        commentTime=dateFormat.format(new Date(miliTime*1000));
        this.userID = userID;
        this.commentText = commentText;

    }

    public String getUserName() {
        return userID;
    }

    public String getCommentText() {
        return commentText;
    }

    public String getCommentTime() {

        return commentTime;
    }

}
