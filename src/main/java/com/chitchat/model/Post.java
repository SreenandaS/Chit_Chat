package com.chitchat.model;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;

public class Post {
    private int id;
    private int userId;
    private String authorUsername;
    private String authorFullName;
    private String authorPic;
    private String content;
    private String imageUrl;
    private Timestamp createdAt;

    // Computed / feed fields
    private int likeCount;
    private boolean likedByCurrentUser;

    public Post() {}

    public Post(int id, int userId, String authorUsername, String authorFullName, String authorPic, String content, String imageUrl, Timestamp createdAt) {
        this.id = id;
        this.userId = userId;
        this.authorUsername = authorUsername;
        this.authorFullName = authorFullName;
        this.authorPic = authorPic;
        this.content = content;
        this.imageUrl = imageUrl;
        this.createdAt = createdAt;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getAuthorUsername() { return authorUsername; }
    public void setAuthorUsername(String authorUsername) { this.authorUsername = authorUsername; }

    public String getAuthorFullName() { return authorFullName; }
    public void setAuthorFullName(String authorFullName) { this.authorFullName = authorFullName; }

    public String getAuthorPic() {
        if (authorPic == null || authorPic.trim().isEmpty()) {
            return "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=150";
        }
        return authorPic;
    }
    public void setAuthorPic(String authorPic) { this.authorPic = authorPic; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

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

        return new SimpleDateFormat("MMM d, yyyy").format(createdAt);
    }

    public int getLikeCount() { return likeCount; }
    public void setLikeCount(int likeCount) { this.likeCount = likeCount; }

    public boolean isLikedByCurrentUser() { return likedByCurrentUser; }
    public void setLikedByCurrentUser(boolean likedByCurrentUser) { this.likedByCurrentUser = likedByCurrentUser; }
}
