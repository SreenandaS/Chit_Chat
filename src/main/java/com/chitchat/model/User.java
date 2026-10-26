package com.chitchat.model;

import java.sql.Timestamp;

public class User {
    private int id;
    private String username;
    private String email;
    private String password;
    private String fullName;
    private String bio;
    private String profilePic;
    private boolean admin;
    private Timestamp createdAt;

    // Computed / stats fields
    private int postCount;
    private int likeCount;

    public User() {}

    public User(int id, String username, String email, String password, String fullName, String bio, String profilePic, boolean admin, Timestamp createdAt) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.password = password;
        this.fullName = fullName;
        this.bio = bio;
        this.profilePic = profilePic;
        this.admin = admin;
        this.createdAt = createdAt;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }

    public String getProfilePic() {
        if (profilePic == null || profilePic.trim().isEmpty()) {
            return "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=150";
        }
        return profilePic;
    }
    public void setProfilePic(String profilePic) { this.profilePic = profilePic; }

    public boolean isAdmin() { return admin; }
    public void setAdmin(boolean admin) { this.admin = admin; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public int getPostCount() { return postCount; }
    public void setPostCount(int postCount) { this.postCount = postCount; }

    public int getLikeCount() { return likeCount; }
    public void setLikeCount(int likeCount) { this.likeCount = likeCount; }
}
