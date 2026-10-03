package com.example.followcheck.scanner;

import android.webkit.WebView;
import com.example.followcheck.data.model.FollowRecord;
import com.example.followcheck.data.model.InstagramUser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Production implementation of FollowDataSource for Instagram.
 * Updated for Phase 10: Robust finish logic and persistent session state.
 */
public class InstagramScanner implements FollowDataSource {

    private WebView webView;
    private WebViewDataCollector collector;
    private ScanCallback activeCallback;
    private final AtomicBoolean isProcessing = new AtomicBoolean(false);

    @Override
    public void setWebView(WebView webView) {
        this.webView = webView;
        if (webView != null) {
            if (this.collector == null) {
                this.collector = new WebViewDataCollector(webView);
            } else {
                this.collector.updateWebView(webView);
            }
        }
    }

    @Override
    public void scan(ScanCallback callback) {
        if (webView == null || collector == null) {
            callback.onError("WebView not initialized. Please ensure Instagram is open.");
            callback.onStateChanged(ScannerState.FAILED);
            return;
        }

        this.activeCallback = callback;
        // Force reset processing guard for any new scan attempt
        isProcessing.set(false);
        
        if (collector.isScanning()) {
            collector.stopCollection();
        }
        
        callback.onStateChanged(ScannerState.COLLECTING);

        collector.startCollection(new WebViewDataCollector.CollectionCallback() {
            @Override
            public void onProgress(ScanProgress progress) {
                callback.onProgressUpdate(progress);
            }

            @Override
            public void onFinished(FollowData data) {
                processCapturedData(data, callback);
            }

            @Override
            public void onError(String message) {
                callback.onError(message);
                callback.onStateChanged(ScannerState.FAILED);
            }
        });
    }

    private synchronized void processCapturedData(FollowData data, ScanCallback callback) {
        // Guard against multiple calls (e.g. manual stop vs automatic timeout)
        if (isProcessing.getAndSet(true)) return;
        
        if (callback == null) {
            isProcessing.set(false);
            return;
        }
        
        callback.onStateChanged(ScannerState.PROCESSING);
        
        List<InstagramUser> users = new ArrayList<>();
        List<FollowRecord> records = new ArrayList<>();

        Map<String, InstagramUser> userMap = new HashMap<>();
        Map<String, boolean[]> relationshipFlags = new HashMap<>();

        if (data.getFollowers() != null) {
            for (String username : data.getFollowers()) {
                String id = username.toLowerCase().trim();
                userMap.put(id, new InstagramUser(id, username, username, null, false, false));
                relationshipFlags.put(id, new boolean[]{true, false});
            }
        }

        if (data.getFollowing() != null) {
            for (String username : data.getFollowing()) {
                String id = username.toLowerCase().trim();
                if (!userMap.containsKey(id)) {
                    userMap.put(id, new InstagramUser(id, username, username, null, false, false));
                    relationshipFlags.put(id, new boolean[]{false, true});
                } else {
                    relationshipFlags.get(id)[1] = true;
                }
            }
        }

        if (userMap.isEmpty()) {
            callback.onError("No candidates captured. Ensure the list is open and try scrolling.");
            callback.onStateChanged(ScannerState.FAILED);
            isProcessing.set(false); // CRITICAL: Reset guard on failure
            return;
        }

        for (String id : userMap.keySet()) {
            users.add(userMap.get(id));
            boolean[] flags = relationshipFlags.get(id);
            records.add(new FollowRecord(0, id, flags[0], flags[1]));
        }

        // Pass separate completeness assessments for followers and following to match the ScanCallback interface
        callback.onScanFinished(
                users, 
                records, 
                data.getFollowersCompleteness(), 
                data.getFollowingCompleteness(), 
                data.getDiagnosticMessage()
        );
        callback.onStateChanged(ScannerState.COMPLETED);
        
        // Reset flag after processing to allow subsequent scans in the same session
        isProcessing.set(false);
    }

    @Override
    public void stop() {
        if (collector != null) {
            collector.stopCollection();
            // Immediate final extraction and processing
            processCapturedData(collector.getCapturedData(), activeCallback);
        }
    }

    @Override
    public void cancel() {
        if (collector != null) {
            collector.stopCollection();
        }
        if (activeCallback != null) {
            activeCallback.onStateChanged(ScannerState.CANCELLED);
        }
        isProcessing.set(false); // Reset guard on cancel
    }
}
