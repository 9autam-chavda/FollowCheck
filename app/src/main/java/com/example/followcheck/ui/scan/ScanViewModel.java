package com.example.followcheck.ui.scan;

import android.app.Application;
import android.webkit.WebView;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.followcheck.data.model.FollowRecord;
import com.example.followcheck.data.model.InstagramUser;
import com.example.followcheck.data.model.ScanResult;
import com.example.followcheck.data.repository.FollowRepository;
import com.example.followcheck.scanner.FollowDataSource;
import com.example.followcheck.scanner.FollowDataSourceFactory;
import com.example.followcheck.scanner.ScanCallback;
import com.example.followcheck.scanner.ScannerState;
import com.example.followcheck.scanner.ScanCompleteness;
import com.example.followcheck.scanner.ScanProgress;

import java.util.List;

public class ScanViewModel extends AndroidViewModel {
    private final FollowRepository repository;
    private final MutableLiveData<ScannerState> state = new MutableLiveData<>(ScannerState.IDLE);
    private final MutableLiveData<ScanProgress> progress = new MutableLiveData<>();
    private final MutableLiveData<ScanResult> scanResult = new MutableLiveData<>();
    private final MutableLiveData<String> error = new MutableLiveData<>();
    private final MutableLiveData<Boolean> navigateToResults = new MutableLiveData<>(false);
    
    private FollowDataSource activeSource;
    private WebView webView;

    public ScanViewModel(@NonNull Application application) {
        super(application);
        repository = new FollowRepository(application);
        // CRITICAL: Initialize activeSource ONCE here to ensure session persistence
        // (Followers and Following lists are remembered throughout the activity session).
        activeSource = FollowDataSourceFactory.createDataSource(FollowDataSourceFactory.SourceType.REAL, 0);
    }

    public LiveData<ScannerState> getState() { return state; }
    public LiveData<ScanProgress> getProgress() { return progress; }
    public LiveData<ScanResult> getScanResult() { return scanResult; }
    public LiveData<String> getError() { return error; }
    public LiveData<Boolean> getNavigateToResults() { return navigateToResults; }

    public void setWebView(WebView webView) {
        this.webView = webView;
        if (activeSource != null) {
            activeSource.setWebView(webView);
        }
    }

    public void startScan() {
        if (state.getValue() == ScannerState.RUNNING || state.getValue() == ScannerState.COLLECTING) return;
        
        error.setValue(null);

        ScanCallback callback = new ScanCallback() {
            @Override
            public void onStateChanged(ScannerState newState) {
                state.postValue(newState);
            }

            @Override
            public void onProgressUpdate(ScanProgress p) {
                progress.postValue(p);
            }

            @Override
            public void onScanFinished(List<InstagramUser> users, List<FollowRecord> records, 
                                     ScanCompleteness followersComp, ScanCompleteness followingComp,
                                     String diagnosticInfo) {
                saveResults(users, records, followersComp, followingComp);
            }

            @Override
            public void onError(String message) {
                error.postValue(message);
            }
        };

        if (webView != null) {
            // Use the persistent activeSource
            activeSource.scan(callback);
        } else {
            error.setValue("Scanner initialization failed: WebView is null");
            state.setValue(ScannerState.FAILED);
        }
    }

    private void saveResults(List<InstagramUser> users, List<FollowRecord> records, 
                             ScanCompleteness followersComp, ScanCompleteness followingComp) {
        state.postValue(ScannerState.SAVING);
        
        repository.saveScanResult(users, records, followersComp, followingComp, "WEBVIEW_DOM", result -> {
            scanResult.postValue(result);
            state.postValue(ScannerState.COMPLETED);
            navigateToResults.postValue(true);
        });
    }

    public void onNavigatedToResults() {
        navigateToResults.setValue(false);
    }

    public void finishScan() {
        if (activeSource != null) {
            activeSource.stop();
        }
    }

    public void cancelScan() {
        if (activeSource != null) {
            activeSource.cancel();
        }
        state.setValue(ScannerState.CANCELLED);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        if (activeSource != null) {
            cancelScan();
        }
    }
}
