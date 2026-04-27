package com.example.task_perf1;

public class Message {
    private String text;
    private int senderID;
    private String timestamp;

    public Message(String text, int senderID, String timestamp) {
        this.text = text;
        this.senderID = senderID;
        this.timestamp = timestamp;
    }

    public String getText() { return text; }
    public int getSenderID() { return senderID; }
    public String getTimestamp() { return timestamp; }
}