package com.example.followcheck.data.local;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.followcheck.data.model.FollowSnapshot;

import java.util.List;

@Dao
public interface SnapshotDao {
    @Insert
    long insert(FollowSnapshot snapshot);

    @Query("SELECT * FROM follow_snapshots ORDER BY timestamp DESC")
    LiveData<List<FollowSnapshot>> getAllSnapshots();

    @Query("SELECT * FROM follow_snapshots ORDER BY timestamp DESC")
    List<FollowSnapshot> getAllSnapshotsList();

    @Query("SELECT * FROM follow_snapshots ORDER BY timestamp DESC LIMIT 1")
    FollowSnapshot getLatestSnapshot();

    @Query("SELECT * FROM follow_snapshots WHERE id = :id")
    FollowSnapshot getSnapshotById(long id);

    @Delete
    void delete(FollowSnapshot snapshot);
    
    @Query("DELETE FROM follow_snapshots")
    void deleteAll();
}
