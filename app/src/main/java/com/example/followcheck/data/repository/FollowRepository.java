package com.example.followcheck.data.repository;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.LiveData;

import com.example.followcheck.comparison.FollowComparisonEngine;
import com.example.followcheck.data.local.AppDatabase;
import com.example.followcheck.data.local.FollowDao;
import com.example.followcheck.data.local.SnapshotDao;
import com.example.followcheck.data.model.FollowRecord;
import com.example.followcheck.data.model.FollowSnapshot;
import com.example.followcheck.data.model.InstagramUser;
import com.example.followcheck.data.model.ScanResult;
import com.example.followcheck.scanner.ScanCompleteness;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;

public class FollowRepository {
    private static final String TAG = "FollowRepository";
    private final FollowDao followDao;
    private final SnapshotDao snapshotDao;
    private final ExecutorService executor;

    public FollowRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        followDao = db.followDao();
        snapshotDao = db.snapshotDao();
        executor = AppDatabase.databaseWriteExecutor;
    }

    public LiveData<List<FollowSnapshot>> getAllSnapshots() {
        return snapshotDao.getAllSnapshots();
    }

    public void saveScanResult(List<InstagramUser> users, List<FollowRecord> records, 
                               ScanCompleteness followersCompleteness, 
                               ScanCompleteness followingCompleteness,
                               String source,
                               SaveResultCallback callback) {
        executor.execute(() -> {
            try {
                FollowSnapshot latest = snapshotDao.getLatestSnapshot();
                long snapshotId;
                boolean isNewSnapshot = true;

                // Session Merge Logic: If latest scan was < 15 mins ago, merge into it
                if (latest != null && (System.currentTimeMillis() - latest.getTimestamp() < 15 * 60 * 1000)) {
                    snapshotId = latest.getId();
                    isNewSnapshot = false;
                    
                    if (followersCompleteness != ScanCompleteness.UNKNOWN) {
                        latest.setFollowersCompleteness(followersCompleteness);
                    }
                    if (followingCompleteness != ScanCompleteness.UNKNOWN) {
                        latest.setFollowingCompleteness(followingCompleteness);
                    }
                } else {
                    FollowSnapshot snapshot = new FollowSnapshot(System.currentTimeMillis(), 0, 0);
                    snapshot.setFollowersCompleteness(followersCompleteness);
                    snapshot.setFollowingCompleteness(followingCompleteness);
                    snapshot.setSource(source);
                    snapshotId = snapshotDao.insert(snapshot);
                    latest = snapshot;
                    latest.setId(snapshotId);
                }

                List<FollowRecord> existingRecords = followDao.getRecordsForSnapshot(snapshotId);
                Map<String, FollowRecord> recordMap = new HashMap<>();
                for (FollowRecord r : existingRecords) recordMap.put(r.getUserId(), r);

                for (FollowRecord newRecord : records) {
                    newRecord.setSnapshotId(snapshotId);
                    FollowRecord existing = recordMap.get(newRecord.getUserId());
                    if (existing != null) {
                        if (newRecord.isFollower()) existing.setFollower(true);
                        if (newRecord.isFollowing()) existing.setFollowing(true);
                    } else {
                        recordMap.put(newRecord.getUserId(), newRecord);
                    }
                }

                latest.setFollowersCount(0);
                latest.setFollowingCount(0);
                for (FollowRecord r : recordMap.values()) {
                    if (r.isFollower()) latest.setFollowersCount(latest.getFollowersCount() + 1);
                    if (r.isFollowing()) latest.setFollowingCount(latest.getFollowingCount() + 1);
                }
                
                snapshotDao.update(latest); 
                followDao.insertUsers(users);
                followDao.insertRecords(new java.util.ArrayList<>(recordMap.values()));

                List<InstagramUser> followers = followDao.getFollowers(snapshotId);
                List<InstagramUser> following = followDao.getFollowing(snapshotId);
                
                ScanResult result = new ScanResult(latest, followers, following);
                result.setNonFollowers(FollowComparisonEngine.getNonFollowers(followers, following));
                result.setFollowersOnly(FollowComparisonEngine.getFollowersOnly(followers, following));
                result.setMutuals(FollowComparisonEngine.getMutuals(followers, following));

                callback.onSaved(result);
            } catch (Exception e) {
                Log.e(TAG, "Database Error during save", e);
                // Important: Ensure UI completes even on error
                callback.onSaved(null);
            }
        });
    }

    public void getDashboardData(DashboardDataCallback callback) {
        executor.execute(() -> {
            try {
                FollowSnapshot latest = snapshotDao.getLatestSnapshot();
                List<FollowSnapshot> history = snapshotDao.getAllSnapshotsList();
                int nonFollowersCount = 0, youDontFollowBackCount = 0, newFollowersCount = 0, lostFollowersCount = 0;
                if (latest != null) {
                    List<InstagramUser> followers = followDao.getFollowers(latest.getId());
                    List<InstagramUser> following = followDao.getFollowing(latest.getId());
                    nonFollowersCount = FollowComparisonEngine.getNonFollowers(followers, following).size();
                    youDontFollowBackCount = FollowComparisonEngine.getFollowersOnly(followers, following).size();
                    if (history != null && history.size() > 1) {
                        FollowSnapshot previous = history.get(1);
                        if (previous.getFollowersCompleteness() == ScanCompleteness.COMPLETE && latest.getFollowersCompleteness() == ScanCompleteness.COMPLETE) {
                            List<InstagramUser> prevFollowers = followDao.getFollowers(previous.getId());
                            newFollowersCount = FollowComparisonEngine.getNewFollowers(prevFollowers, followers).size();
                            lostFollowersCount = FollowComparisonEngine.getLostFollowers(prevFollowers, followers).size();
                        }
                    }
                }
                callback.onDataLoaded(latest, nonFollowersCount, youDontFollowBackCount, newFollowersCount, lostFollowersCount);
            } catch (Exception e) { Log.e(TAG, "Dash Error", e); }
        });
    }

    public void getSnapshotDetails(long snapshotId, SnapshotDetailsCallback callback) {
        executor.execute(() -> {
            try {
                List<InstagramUser> followers = followDao.getFollowers(snapshotId);
                List<InstagramUser> following = followDao.getFollowing(snapshotId);
                callback.onDetailsLoaded(followers, following);
            } catch (Exception e) { Log.e(TAG, "Details Error", e); }
        });
    }

    public void getLatestSnapshot(LatestSnapshotCallback callback) {
        executor.execute(() -> {
            try { callback.onLoaded(snapshotDao.getLatestSnapshot()); }
            catch (Exception e) { Log.e(TAG, "Latest Error", e); }
        });
    }

    public void deleteAllData(Runnable onComplete) {
        executor.execute(() -> {
            try { snapshotDao.deleteAll(); followDao.deleteAllUsers(); }
            catch (Exception e) { Log.e(TAG, "Delete Error", e); }
            onComplete.run();
        });
    }

    public interface SaveResultCallback { void onSaved(ScanResult result); }
    public interface SnapshotDetailsCallback { void onDetailsLoaded(List<InstagramUser> followers, List<InstagramUser> following); }
    public interface LatestSnapshotCallback { void onLoaded(FollowSnapshot snapshot); }
    public interface DashboardDataCallback { void onDataLoaded(FollowSnapshot latest, int nonFollowers, int youDontFollowBack, int newFollowers, int lostFollowers); }
}
