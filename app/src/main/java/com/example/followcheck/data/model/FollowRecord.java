package com.example.followcheck.data.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;

@Entity(
    tableName = "follow_records",
    primaryKeys = {"snapshotId", "userId"},
    foreignKeys = {
        @ForeignKey(entity = FollowSnapshot.class,
                    parentColumns = "id",
                    childColumns = "snapshotId",
                    onDelete = ForeignKey.CASCADE),
        @ForeignKey(entity = InstagramUser.class,
                    parentColumns = "id",
                    childColumns = "userId",
                    onDelete = ForeignKey.CASCADE)
    },
    indices = {@Index("userId"), @Index("snapshotId")}
)
public class FollowRecord {
    private long snapshotId;
    @NonNull
    private String userId;
    private boolean isFollower;
    private boolean isFollowing;

    public FollowRecord(long snapshotId, @NonNull String userId, boolean isFollower, boolean isFollowing) {
        this.snapshotId = snapshotId;
        this.userId = userId;
        this.isFollower = isFollower;
        this.isFollowing = isFollowing;
    }

    public long getSnapshotId() {
        return snapshotId;
    }

    public void setSnapshotId(long snapshotId) {
        this.snapshotId = snapshotId;
    }

    @NonNull
    public String getUserId() {
        return userId;
    }

    public void setUserId(@NonNull String userId) {
        this.userId = userId;
    }

    public boolean isFollower() {
        return isFollower;
    }

    public void setFollower(boolean follower) {
        isFollower = follower;
    }

    public boolean isFollowing() {
        return isFollowing;
    }

    public void setFollowing(boolean following) {
        isFollowing = following;
    }
}
