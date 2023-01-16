package com.example.audiostreamapp.adminMode;

public class Report {
    private String uid;
    private String liveComment;
    private String isBlocked;

    public Report(String uid, String liveComment, String isBlocked) {
        this.uid = uid;
        this.liveComment = liveComment;
        this.isBlocked = isBlocked;
    }

    public String getIsBlocked() {
        return isBlocked;
    }

    public void setIsBlocked(String isBlocked) {
        this.isBlocked = isBlocked;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getLiveComment() {
        return liveComment;
    }

    public void setLiveComment(String liveComment) {
        this.liveComment = liveComment;
    }
}
