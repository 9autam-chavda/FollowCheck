package com.example.followcheck.data.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.followcheck.comparison.FollowComparisonEngine;
import com.example.followcheck.data.local.AppDatabase;
import com.example.followcheck.data.local.FollowDao;
import com.example.followcheck.data.local.SnapshotDao;
import com.example.followcheck.data.model.FollowRecord;
import com.example.followcheck.data.model.FollowSnapshot;
import com.example.followcheck.data.model.InstagramUser;
import com.example.followcheck.data.model.ScanResult;

import java.util.List;
import java.util.concurrent.ExecutorService;

public class FollowRepository {
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

    /**
     * Persists a scan result to the database.
     */
    public void saveScanResult(List<InstagramUser> users, List<FollowRecord> records, SaveResultCallback callback) {
        executor.execute(() -> {
            int followersCount = 0;
            int followingCount = 0;
            for (FollowRecord r : records) {
                if (r.isFollower()) followersCount++;
                if (r.isFollowing()) followingCount++;
            }

            FollowSnapshot snapshot = new FollowSnapshot(System.currentTimeMillis(), followersCount, followingCount);
            long snapshotId = snapshotDao.insert(snapshot);
            snapshot.setId(snapshotId);

            for (FollowRecord record : records) {
                record.setSnapshotId(snapshotId);
            }

            followDao.insertUsers(users);
            followDao.insertRecords(records);

            // Fetch previous for comparison
            List<FollowSnapshot> history = snapshotDao.getAllSnapshotsList();
            FollowSnapshot previous = (history.size() > 1) ? history.get(1) : null;
            
            List<InstagramUser> followers = followDao.getFollowers(snapshotId);
            List<InstagramUser> following = followDao.getFollowing(snapshotId);
            
            ScanResult result = new ScanResult(snapshot, followers, following);
            result.setNonFollowers(FollowComparisonEngine.getNonFollowers(followers, following));
            result.setFollowersOnly(FollowComparisonEngine.getFollowersOnly(followers, following));
            result.setMutuals(FollowComparisonEngine.getMutuals(followers, following));

            if (previous != null) {
                List<InstagramUser> prevFollowers = followDao.getFollowers(previous.getId());
                List<InstagramUser> prevFollowing = followDao.getFollowing(previous.getId());
                result.setNewFollowers(FollowComparisonEngine.getNewFollowers(prevFollowers, followers));
                result.setLostFollowers(FollowComparisonEngine.getLostFollowers(prevFollowers, followers));
                result.setNewFollowing(FollowComparisonEngine.getNewFollowing(prevFollowing, following));
                result.setRemovedFollowing(FollowComparisonEngine.getRemovedFollowing(prevFollowing, following));
            }

            callback.onSaved(result);
        });
    }

    public void getDashboardData(DashboardDataCallback callback) {
        executor.execute(() -> {
            FollowSnapshot latest = snapshotDao.getLatestSnapshot();
            List<FollowSnapshot> history = snapshotDao.getAllSnapshotsList();
            
            int nonFollowersCount = 0;
            int youDontFollowBackCount = 0;
            int newFollowersCount = 0;
            int lostFollowersCount = 0;

            if (latest != null) {
                List<InstagramUser> followers = followDao.getFollowers(latest.getId());
                List<InstagramUser> following = followDao.getFollowing(latest.getId());
                nonFollowersCount = FollowComparisonEngine.getNonFollowers(followers, following).size();
                youDontFollowBackCount = FollowComparisonEngine.getFollowersOnly(followers, following).size();

                if (history != null && history.size() > 1) {
                    FollowSnapshot previous = history.get(1);
                    List<InstagramUser> prevFollowers = followDao.getFollowers(previous.getId());
                    newFollowersCount = FollowComparisonEngine.getNewFollowers(prevFollowers, followers).size();
                    lostFollowersCount = FollowComparisonEngine.getLostFollowers(prevFollowers, followers).size();
                }
            }
            callback.onDataLoaded(latest, nonFollowersCount, youDontFollowBackCount, newFollowersCount, lostFollowersCount);
        });
    }

    public void getSnapshotDetails(long snapshotId, SnapshotDetailsCallback callback) {
        executor.execute(() -> {
            List<InstagramUser> followers = followDao.getFollowers(snapshotId);
            List<InstagramUser> following = followDao.getFollowing(snapshotId);
            callback.onDetailsLoaded(followers, following);
        });
    }

    public void getLatestSnapshot(LatestSnapshotCallback callback) {
        executor.execute(() -> {
            FollowSnapshot snapshot = snapshotDao.getLatestSnapshot();
            callback.onLoaded(snapshot);
        });
    }

    public void deleteAllData(Runnable onComplete) {
        executor.execute(() -> {
            snapshotDao.deleteAll();
            followDao.deleteAllUsers();
            onComplete.run();
        });
    }

    public interface SaveResultCallback {
        void onSaved(ScanResult result);
    }

    public interface SnapshotDetailsCallback {
        void onDetailsLoaded(List<InstagramUser> followers, List<InstagramUser> following);
    }

    public interface LatestSnapshotCallback {
        void onLoaded(FollowSnapshot snapshot);
    }

    public interface DashboardDataCallback {
        void onDataLoaded(FollowSnapshot latest, int nonFollowers, int youDontFollowBack, int newFollowers, int lostFollowers);
    }
}
