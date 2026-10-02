package com.example.followcheck.ui.scan;

import android.app.Application;

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
import com.example.followcheck.scanner.InstagramScanner;
import com.example.followcheck.scanner.ScanCallback;
import com.example.followcheck.scanner.ScannerState;

import java.util.List;

public class ScanViewModel extends AndroidViewModel {
    private final FollowRepository repository;
    private final MutableLiveData<ScannerState> state = new MutableLiveData<>(ScannerState.IDLE);
    private final MutableLiveData<Integer> progress = new MutableLiveData<>(0);
    private final MutableLiveData<Integer> total = new MutableLiveData<>(0);
    private final MutableLiveData<String> scanType = new MutableLiveData<>("");
    private final MutableLiveData<ScanResult> scanResult = new MutableLiveData<>();
    private final MutableLiveData<String> error = new MutableLiveData<>();
    
    private FollowDataSource activeSource;

    public ScanViewModel(@NonNull Application application) {
        super(application);
        repository = new FollowRepository(application);
    }

    public LiveData<ScannerState> getState() { return state; }
    public LiveData<Integer> getProgress() { return progress; }
    public LiveData<Integer> getTotal() { return total; }
    public LiveData<String> getScanType() { return scanType; }
    public LiveData<ScanResult> getScanResult() { return scanResult; }
    public LiveData<String> getError() { return error; }

    public void startScan() {
        if (state.getValue() == ScannerState.RUNNING || state.getValue() == ScannerState.PREPARING) return;

        ScanCallback callback = new ScanCallback() {
            @Override
            public void onStateChanged(ScannerState newState) {
                state.postValue(newState);
            }

            @Override
            public void onProgressUpdate(int current, int totalCount, String type) {
                progress.postValue(current);
                total.postValue(totalCount);
                scanType.postValue(type);
            }

            @Override
            public void onScanCompleted(List<InstagramUser> users, List<FollowRecord> records) {
                saveResults(users, records);
            }

            @Override
            public void onError(String message) {
                error.postValue(message);
            }
        };

        repository.getLatestSnapshot(latest -> {
            int scanNum = (latest == null) ? 1 : 2;
            // Use the factory to get the appropriate source (Mock in debug, Real in release)
            activeSource = FollowDataSourceFactory.getDefaultDataSource(scanNum);
            activeSource.scan(callback);
        });
    }

    private void saveResults(List<InstagramUser> users, List<FollowRecord> records) {
        state.postValue(ScannerState.SAVING);
        repository.saveScanResult(users, records, result -> {
            scanResult.postValue(result);
            state.postValue(ScannerState.COMPLETED);
        });
    }

    public void cancelScan() {
        if (activeSource instanceof InstagramScanner) {
            ((InstagramScanner) activeSource).cancel();
        }
        state.setValue(ScannerState.CANCELLED);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        if (activeSource instanceof InstagramScanner) {
            ((InstagramScanner) activeSource).cancel();
        }
    }
}
