package com.example.followcheck.scanner;

import com.example.followcheck.data.model.FollowRecord;
import com.example.followcheck.data.model.InstagramUser;

import java.util.List;

public interface ScanCallback {
    void onStateChanged(ScannerState state);
    void onProgressUpdate(int current, int total, String type);
    void onScanCompleted(List<InstagramUser> users, List<FollowRecord> records);
    void onError(String message);
}
