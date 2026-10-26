package com.chitchat.model;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;

public class Notification {
    private int id;
    private int userId;
    private int actorId;
    private String actorUsername;
    private String actorFullName;
    private String actorPic;
    private String type; // 'LIKE', 'MESSAGE'
    private Integer postId;
    private String message;
    private boolean read;
    private Timestamp createdAt;

    public Notification() {}

    public Notification(int id, int userId, int actorId, String type, Integer postId, String message, boolean read, Timestamp createdAt) {
        this.id = id;
        this.userId = userId;
        this.actorId = actorId;
        this.type = type;
        this.postId = postId;
        this.message = message;
        this.read = read;
        this.createdAt = createdAt;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public int getActorId() { return actorId; }
    public void setActorId(int actorId) { this.actorId = actorId; }

    public String getActorUsername() { return actorUsername; }
    public void setActorUsername(String actorUsername) { this.actorUsername = actorUsername; }

    public String getActorFullName() { return actorFullName; }
    public void setActorFullName(String actorFullName) { this.actorFullName = actorFullName; }

    public String getActorPic() {
        if (actorPic == null || actorPic.trim().isEmpty()) {
            return "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=150";
        }
        return actorPic;
    }
    public void setActorPic(String actorPic) { this.actorPic = actorPic; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public Integer getPostId() { return postId; }
    public void setPostId(Integer postId) { this.postId = postId; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public boolean isRead() { return read; }
    public void setRead(boolean read) { this.read = read; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public String getFormattedDate() {
        if (createdAt == null) return "";
        long diff = System.currentTimeMillis() - createdAt.getTime();
        long minutes = diff / (60 * 1000);
        long hours = diff / (60 * 60 * 1000);
        long days = diff / (24 * 60 * 60 * 1000);

        if (minutes < 1) return "Just now";
        if (minutes < 60) return minutes + "m ago";
        if (hours < 24) return hours + "h ago";
        if (days < 7) return days + "d ago";

        return new SimpleDateFormat("MMM d").format(createdAt);
    }
}
