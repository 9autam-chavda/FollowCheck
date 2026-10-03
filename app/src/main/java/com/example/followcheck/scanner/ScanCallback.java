package com.example.followcheck.scanner;

import com.example.followcheck.data.model.FollowRecord;
import com.example.followcheck.data.model.InstagramUser;

import java.util.List;

public interface ScanCallback {
    void onStateChanged(ScannerState state);
    
    void onProgressUpdate(ScanProgress progress);
    
    void onScanFinished(List<InstagramUser> users, List<FollowRecord> records, 
                        ScanCompleteness followersComp, ScanCompleteness followingComp,
                        String diagnosticInfo);
    
    void onError(String message);
}
