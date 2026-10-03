package com.example.followcheck.data.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import com.example.followcheck.scanner.ScanCompleteness;

@Entity(tableName = "follow_snapshots")
public class FollowSnapshot {
    @PrimaryKey(autoGenerate = true)
    private long id;
    private long timestamp;
    private int followersCount;
    private int followingCount;
    private ScanCompleteness followersCompleteness = ScanCompleteness.UNKNOWN;
    private ScanCompleteness followingCompleteness = ScanCompleteness.UNKNOWN;
    private String source = "MOCK";

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

    public ScanCompleteness getFollowersCompleteness() {
        return followersCompleteness;
    }

    public void setFollowersCompleteness(ScanCompleteness followersCompleteness) {
        this.followersCompleteness = followersCompleteness;
    }

    public ScanCompleteness getFollowingCompleteness() {
        return followingCompleteness;
    }

    public void setFollowingCompleteness(ScanCompleteness followingCompleteness) {
        this.followingCompleteness = followingCompleteness;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }
}
