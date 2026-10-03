package com.example.followcheck.scanner;

import android.os.Handler;
import android.os.Looper;
import android.webkit.WebView;

import com.example.followcheck.data.mock.MockInstagramDataProvider;
import com.example.followcheck.data.model.FollowRecord;
import com.example.followcheck.data.model.InstagramUser;
import com.example.followcheck.scanner.dom.DomContext;

import java.util.ArrayList;
import java.util.List;

/**
 * Mock implementation of FollowDataSource for testing.
 * Updated for Phase 10 ScanProgress compatibility.
 */
public class MockFollowDataSource implements FollowDataSource {

    private final Handler handler = new Handler(Looper.getMainLooper());
    private final int scanNum;
    private boolean isCancelled = false;

    public MockFollowDataSource(int scanNum) {
        this.scanNum = scanNum;
    }

    @Override
    public void setWebView(WebView webView) {
        // Mock does not need WebView
    }

    @Override
    public void scan(ScanCallback callback) {
        isCancelled = false;
        callback.onStateChanged(ScannerState.COLLECTING);
        
        handler.postDelayed(() -> {
            if (isCancelled) return;

            List<InstagramUser> followers = scanNum == 1 ? 
                    MockInstagramDataProvider.getFollowersScan1() : 
                    MockInstagramDataProvider.getFollowersScan2();
                    
            List<InstagramUser> following = scanNum == 1 ? 
                    MockInstagramDataProvider.getFollowingScan1() : 
                    MockInstagramDataProvider.getFollowingScan2();
            
            int total = followers.size() + following.size();
            callback.onProgressUpdate(new ScanProgress(
                    total, 
                    total, 
                    total, 
                    0.0,
                    "Mock Data Generated", 
                    DomContext.UNKNOWN
            ));
            
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

            // Fixed signature: pass both followers and following completeness
            callback.onScanFinished(allUsers, records, ScanCompleteness.COMPLETE, ScanCompleteness.COMPLETE, "Mock scan successful.");
            callback.onStateChanged(ScannerState.COMPLETED);
        }, 1500);
    }

    @Override
    public void stop() {
        // Simple stop implementation
    }

    @Override
    public void cancel() {
        isCancelled = true;
    }
}
