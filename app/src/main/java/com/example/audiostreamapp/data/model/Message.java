package com.example.audiostreamapp.data.model;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Message {
    private String content;
    private String sender;
    private String receiver;
    private String messageTime;
    private long messageTimeinDB;

    public Message(String content,long messageTime, String sender, String receiver){
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        this.messageTime=dateFormat.format(new Date(messageTime*1000));
        this.content = content;
        this.sender = sender;
        this.receiver = receiver;
        this.messageTimeinDB = messageTime;
    }

    public String getContent() {
        return content;
    }

    public String getSender() {
        return sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public String getMessageTime() {
        return messageTime;
    }

    public long getMessageTimeinDB() {return messageTimeinDB;}
}
