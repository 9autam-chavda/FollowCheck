package com.example.followcheck.scanner;

import android.os.Handler;
import android.os.Looper;

import com.example.followcheck.data.mock.MockInstagramDataProvider;
import com.example.followcheck.data.model.FollowRecord;
import com.example.followcheck.data.model.InstagramUser;

import java.util.ArrayList;
import java.util.List;

public class MockFollowDataSource implements FollowDataSource {

    private final Handler handler = new Handler(Looper.getMainLooper());
    private final int scanNum;

    public MockFollowDataSource(int scanNum) {
        this.scanNum = scanNum;
    }

    @Override
    public void scan(ScanCallback callback) {
        callback.onStateChanged(ScannerState.RUNNING);
        
        handler.postDelayed(() -> {
            List<InstagramUser> followers = scanNum == 1 ? 
                    MockInstagramDataProvider.getFollowersScan1() : 
                    MockInstagramDataProvider.getFollowersScan2();
                    
            List<InstagramUser> following = scanNum == 1 ? 
                    MockInstagramDataProvider.getFollowingScan1() : 
                    MockInstagramDataProvider.getFollowingScan2();
            
            callback.onProgressUpdate(followers.size(), followers.size() + following.size(), "Followers");
            
            List<InstagramUser> allUsers = new ArrayList<>(followers);
            for (InstagramUser f : following) {
                boolean exists = false;
                for (InstagramUser existing : allUsers) {
                    if (existing.getId().equalsIgnoreCase(f.getId())) {
                        exists = true;
                        break;
                    }
                }
                if (!exists) allUsers.add(f);
            }

            List<FollowRecord> records = new ArrayList<>();
            for (InstagramUser user : allUsers) {
                boolean isF = false;
                for (InstagramUser f : followers) {
                    if (f.getId().equalsIgnoreCase(user.getId())) {
                        isF = true;
                        break;
                    }
                }
                boolean isFollowing = false;
                for (InstagramUser f : following) {
                    if (f.getId().equalsIgnoreCase(user.getId())) {
                        isFollowing = true;
                        break;
                    }
                }
                records.add(new FollowRecord(0, user.getId(), isF, isFollowing));
            }

            callback.onScanCompleted(allUsers, records);
            callback.onStateChanged(ScannerState.COMPLETED);
        }, 1500);
    }
}
