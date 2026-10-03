package com.example.followcheck.scanner;

import android.webkit.WebView;

public interface FollowDataSource {
    /**
     * Start the scanning/collection process.
     */
    void scan(ScanCallback callback);
    
    /**
     * Set the WebView instance to be used for scanning.
     */
    void setWebView(WebView webView);
    
    /**
     * Stop the current collection and trigger completion with current data.
     */
    void stop();
    
    /**
     * Cancel the current process and discard data.
     */
    void cancel();
}
