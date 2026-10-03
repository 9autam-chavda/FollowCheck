package com.example.followcheck.scanner;

import android.os.Handler;
import android.os.Looper;
import android.webkit.WebView;

import com.example.followcheck.scanner.dom.DomContext;
import com.example.followcheck.scanner.dom.DomExtractionResult;
import com.example.followcheck.scanner.dom.InstagramDomExtractor;
import com.example.followcheck.scanner.dom.DomCandidate;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.ArrayList;

/**
 * Orchestrates the collection of data from the WebView DOM.
 * Updated for Phase 10: Persistent session memory and hot-swappable callbacks.
 */
public class WebViewDataCollector {
    private InstagramDomExtractor extractor;
    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    
    private final Set<String> observedFollowers = new HashSet<>();
    private final Set<String> observedFollowing = new HashSet<>();
    
    private ScanCompleteness followersCompleteness = ScanCompleteness.UNKNOWN;
    private ScanCompleteness followingCompleteness = ScanCompleteness.UNKNOWN;
    
    private boolean isScanning = false;
    private final int scrollIncrement = 350; 
    private final int scanIntervalMs = 2500; 
    private int consecutiveNoProgress = 0;
    private long startTime = 0;
    private static final long MAX_SCAN_TIME = 15 * 60 * 1000; 

    private CollectionCallback activeCallback;

    public interface CollectionCallback {
        void onProgress(ScanProgress progress);
        void onFinished(FollowData data);
        void onError(String message);
    }

    public WebViewDataCollector(WebView webView) {
        this.extractor = new InstagramDomExtractor(webView);
    }

    public void updateWebView(WebView webView) {
        this.extractor = new InstagramDomExtractor(webView);
    }

    public boolean isScanning() {
        return isScanning;
    }

    public void startCollection(CollectionCallback callback) {
        this.activeCallback = callback;
        
        if (isScanning) {
            // If already scanning, we've updated the callback, so the next batch will use it.
            return;
        }
        
        isScanning = true;
        startTime = System.currentTimeMillis();
        consecutiveNoProgress = 0;
        
        // Initial capture
        extractor.extract(result -> {
            if (result.isSuccess()) {
                processBatch(result);
            }
            scheduleNextBatch();
        });
    }

    public void stopCollection() {
        isScanning = false;
    }

    public void clearResults() {
        observedFollowers.clear();
        observedFollowing.clear();
        followersCompleteness = ScanCompleteness.UNKNOWN;
        followingCompleteness = ScanCompleteness.UNKNOWN;
        consecutiveNoProgress = 0;
    }

    public FollowData getCapturedData() {
        return new FollowData(
                new ArrayList<>(observedFollowers),
                new ArrayList<>(observedFollowing),
                followersCompleteness,
                followingCompleteness,
                System.currentTimeMillis(),
                "Session state: " + observedFollowers.size() + " followers, " + observedFollowing.size() + " following."
        );
    }

    private void scheduleNextBatch() {
        if (!isScanning) return;

        if (System.currentTimeMillis() - startTime > MAX_SCAN_TIME) {
            if (activeCallback != null) activeCallback.onFinished(getCapturedData());
            isScanning = false;
            return;
        }

        mainHandler.postDelayed(() -> {
            if (!isScanning) return;
            
            extractor.scroll(scrollIncrement, result -> {
                if (!result.isSuccess()) {
                    if (activeCallback != null) activeCallback.onError(result.getErrorMessage());
                    isScanning = false;
                    return;
                }

                processBatch(result);
                
                if (isScanning) {
                    scheduleNextBatch();
                } else {
                    if (activeCallback != null) activeCallback.onFinished(getCapturedData());
                }
            });
        }, scanIntervalMs);
    }

    private void processBatch(DomExtractionResult result) {
        if (activeCallback == null) return;
        
        DomContext context = result.getDetectedContext();
        List<DomCandidate> candidates = result.getCandidates();
        int newCount = 0;

        if (context == DomContext.FOLLOWERS || context == DomContext.FOLLOWING) {
            Set<String> targetSet = (context == DomContext.FOLLOWERS) ? observedFollowers : observedFollowing;

            if (candidates != null) {
                for (DomCandidate candidate : candidates) {
                    String normalized = candidate.getNormalizedUsername();
                    if (!normalized.isEmpty() && targetSet.add(normalized)) {
                        newCount++;
                    }
                }
            }

            if (newCount == 0 && !result.isLoading()) {
                consecutiveNoProgress++;
            } else if (newCount > 0) {
                consecutiveNoProgress = 0;
            }

            CompletenessEvaluator.Evidence evidence = new CompletenessEvaluator.Evidence(result, consecutiveNoProgress);
            ScanCompleteness currentCompleteness = CompletenessEvaluator.evaluate(evidence);

            if (context == DomContext.FOLLOWERS) followersCompleteness = currentCompleteness;
            else followingCompleteness = currentCompleteness;
        }

        activeCallback.onProgress(new ScanProgress(
                observedFollowers.size() + observedFollowing.size(),
                newCount,
                candidates != null ? candidates.size() : 0,
                result.getScrollTop(),
                "Found " + (context == DomContext.FOLLOWERS ? observedFollowers.size() : observedFollowing.size()) + " users",
                context
        ));
    }
}
