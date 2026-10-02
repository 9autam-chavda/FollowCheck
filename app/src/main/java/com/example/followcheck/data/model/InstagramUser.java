package com.example.followcheck.data.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "instagram_users")
public class InstagramUser {
    @PrimaryKey
    @NonNull
    private String id;
    private String username;
    private String fullName;
    private String profilePicUrl;
    private boolean isPrivate;
    private boolean isVerified;

    public InstagramUser(@NonNull String id, String username, String fullName, String profilePicUrl, boolean isPrivate, boolean isVerified) {
        this.id = id;
        this.username = username;
        this.fullName = fullName;
        this.profilePicUrl = profilePicUrl;
        this.isPrivate = isPrivate;
        this.isVerified = isVerified;
    }

    @NonNull
    public String getId() {
        return id;
    }

    public void setId(@NonNull String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getProfilePicUrl() {
        return profilePicUrl;
    }

    public void setProfilePicUrl(String profilePicUrl) {
        this.profilePicUrl = profilePicUrl;
    }

    public boolean isPrivate() {
        return isPrivate;
    }

    public void setPrivate(boolean isPrivate) {
        this.isPrivate = isPrivate;
    }

    public boolean isVerified() {
        return isVerified;
    }

    public void setVerified(boolean isVerified) {
        this.isVerified = isVerified;
    }
}
