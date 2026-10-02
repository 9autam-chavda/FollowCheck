package com.example.followcheck.scanner;

import android.os.Handler;
import android.os.Looper;

/**
 * Implementation of FollowDataSource for real Instagram data acquisition.
 * 
 * IMPORTANT: In this phase, real scraping is NOT implemented.
 * This class serves as the architectural foundation for future work.
 */
public class InstagramScanner implements FollowDataSource {

    private final Handler handler = new Handler(Looper.getMainLooper());
    private boolean isCancelled = false;

    @Override
    public void scan(ScanCallback callback) {
        isCancelled = false;
        callback.onStateChanged(ScannerState.PREPARING);
        
        // Simulate initial preparation delay
        handler.postDelayed(() -> {
            if (isCancelled) return;
            
            // Phase 6: Real follower/following detection is NOT implemented.
            // We explicitly report this state to the UI.
            callback.onError("Real Instagram scanning is not yet implemented.");
            callback.onStateChanged(ScannerState.ERROR);
        }, 2000);
    }

    public void cancel() {
        isCancelled = true;
    }
}
