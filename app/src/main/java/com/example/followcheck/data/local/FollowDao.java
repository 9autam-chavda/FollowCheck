package com.example.followcheck.data.local;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.followcheck.data.model.FollowRecord;
import com.example.followcheck.data.model.InstagramUser;

import java.util.List;

@Dao
public interface FollowDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertUsers(List<InstagramUser> users);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertRecords(List<FollowRecord> records);

    @Query("SELECT * FROM instagram_users WHERE id IN (SELECT userId FROM follow_records WHERE snapshotId = :snapshotId AND isFollowing = 1)")
    List<InstagramUser> getFollowing(long snapshotId);

    @Query("SELECT * FROM instagram_users WHERE id IN (SELECT userId FROM follow_records WHERE snapshotId = :snapshotId AND isFollower = 1)")
    List<InstagramUser> getFollowers(long snapshotId);

    @Query("SELECT * FROM follow_records WHERE snapshotId = :snapshotId")
    List<FollowRecord> getRecordsForSnapshot(long snapshotId);

    @Query("DELETE FROM instagram_users")
    void deleteAllUsers();
}
