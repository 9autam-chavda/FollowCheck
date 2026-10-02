package com.example.followcheck.ui.home;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.followcheck.data.model.FollowSnapshot;
import com.example.followcheck.data.repository.FollowRepository;
import com.example.followcheck.scanner.InstagramSessionManager;
import com.example.followcheck.scanner.LoginState;

import java.util.List;

public class HomeViewModel extends AndroidViewModel {
    private final FollowRepository repository;
    private final InstagramSessionManager sessionManager;
    private final LiveData<List<FollowSnapshot>> allSnapshots;
    private final MutableLiveData<FollowSnapshot> latestSnapshot = new MutableLiveData<>();
    private final MutableLiveData<Integer> nonFollowersCount = new MutableLiveData<>(0);
    private final MutableLiveData<Integer> youDontFollowBackCount = new MutableLiveData<>(0);
    private final MutableLiveData<Integer> newFollowersCount = new MutableLiveData<>(0);
    private final MutableLiveData<Integer> lostFollowersCount = new MutableLiveData<>(0);

    public HomeViewModel(@NonNull Application application) {
        super(application);
        repository = new FollowRepository(application);
        sessionManager = InstagramSessionManager.getInstance();
        allSnapshots = repository.getAllSnapshots();
        loadDashboardData();
    }

    public LiveData<List<FollowSnapshot>> getAllSnapshots() {
        return allSnapshots;
    }

    public LiveData<FollowSnapshot> getLatestSnapshot() {
        return latestSnapshot;
    }

    public LiveData<Integer> getNonFollowersCount() { return nonFollowersCount; }
    public LiveData<Integer> getYouDontFollowBackCount() { return youDontFollowBackCount; }
    public LiveData<Integer> getNewFollowersCount() { return newFollowersCount; }
    public LiveData<Integer> getLostFollowersCount() { return lostFollowersCount; }

    public LiveData<LoginState> getLoginState() {
        return sessionManager.getLoginState();
    }

    public void checkInstagramSession() {
        sessionManager.checkSession();
    }

    public void loadDashboardData() {
        repository.getDashboardData((latest, nonFollowers, youDontFollowBack, newFollowers, lostFollowers) -> {
            latestSnapshot.postValue(latest);
            nonFollowersCount.postValue(nonFollowers);
            youDontFollowBackCount.postValue(youDontFollowBack);
            newFollowersCount.postValue(newFollowers);
            lostFollowersCount.postValue(lostFollowers);
        });
    }
}
