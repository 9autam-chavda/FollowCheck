package com.example.followcheck.scanner;

import android.webkit.WebView;

/**
 * Production coordinator for WebView-based relationship collection.
 * Coordinates between the high-level scanner and the low-level DOM collection logic.
 * Part of the Phase 10 unified scanner architecture.
 */
public class WebViewFollowDataSource {

    private WebViewDataCollector collector;

    /**
     * Associates the WebView with the collector.
     */
    public void setWebView(WebView webView) {
        if (webView != null) {
            this.collector = new WebViewDataCollector(webView);
        }
    }

    /**
     * Starts the automated collection process.
     */
    public void startCollection(WebViewDataCollector.CollectionCallback callback) {
        if (collector == null) {
            callback.onError("Collector not initialized. WebView is missing.");
            return;
        }
        collector.startCollection(callback);
    }

    /**
     * Stops the collection process but maintains captured data.
     */
    public void stopCollection() {
        if (collector != null) {
            collector.stopCollection();
        }
    }

    /**
     * Returns the data captured during the session.
     */
    public FollowData getCapturedData() {
        return collector != null ? collector.getCapturedData() : null;
    }

    /**
     * Clears all temporary observation data.
     */
    public void reset() {
        if (collector != null) {
            collector.clearResults();
        }
    }
}
