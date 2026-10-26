package com.chitchat.model;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;

public class Message {
    private int id;
    private int senderId;
    private int receiverId;
    private String senderUsername;
    private String senderFullName;
    private String senderPic;
    private String receiverUsername;
    private String messageText;
    private boolean read;
    private Timestamp createdAt;

    public Message() {}

    public Message(int id, int senderId, int receiverId, String messageText, boolean read, Timestamp createdAt) {
        this.id = id;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.messageText = messageText;
        this.read = read;
        this.createdAt = createdAt;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getSenderId() { return senderId; }
    public void setSenderId(int senderId) { this.senderId = senderId; }

    public int getReceiverId() { return receiverId; }
    public void setReceiverId(int receiverId) { this.receiverId = receiverId; }

    public String getSenderUsername() { return senderUsername; }
    public void setSenderUsername(String senderUsername) { this.senderUsername = senderUsername; }

    public String getSenderFullName() { return senderFullName; }
    public void setSenderFullName(String senderFullName) { this.senderFullName = senderFullName; }

    public String getSenderPic() {
        if (senderPic == null || senderPic.trim().isEmpty()) {
            return "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=150";
        }
        return senderPic;
    }
    public void setSenderPic(String senderPic) { this.senderPic = senderPic; }

    public String getReceiverUsername() { return receiverUsername; }
    public void setReceiverUsername(String receiverUsername) { this.receiverUsername = receiverUsername; }

    public String getMessageText() { return messageText; }
    public void setMessageText(String messageText) { this.messageText = messageText; }

    public boolean isRead() { return read; }
    public void setRead(boolean read) { this.read = read; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public String getFormattedTime() {
        if (createdAt == null) return "";
        return new SimpleDateFormat("hh:mm a").format(createdAt);
    }
}
