package com.example.followcheck.data.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "follow_snapshots")
public class FollowSnapshot {
    @PrimaryKey(autoGenerate = true)
    private long id;
    private long timestamp;
    private int followersCount;
    private int followingCount;

    public FollowSnapshot(long timestamp, int followersCount, int followingCount) {
        this.timestamp = timestamp;
        this.followersCount = followersCount;
        this.followingCount = followingCount;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public int getFollowersCount() {
        return followersCount;
    }

    public void setFollowersCount(int followersCount) {
        this.followersCount = followersCount;
    }

    public int getFollowingCount() {
        return followingCount;
    }

    public void setFollowingCount(int followingCount) {
        this.followingCount = followingCount;
    }
}
