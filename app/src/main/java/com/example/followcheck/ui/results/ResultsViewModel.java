package com.example.followcheck.ui.results;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.followcheck.data.model.FollowSnapshot;
import com.example.followcheck.data.model.InstagramUser;
import com.example.followcheck.data.repository.FollowRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class ResultsViewModel extends AndroidViewModel {
    private final FollowRepository repository;
    private final MutableLiveData<FollowSnapshot> snapshot = new MutableLiveData<>();
    private final MutableLiveData<List<InstagramUser>> nonFollowers = new MutableLiveData<>();
    private final MutableLiveData<List<InstagramUser>> followersOnly = new MutableLiveData<>();
    private final MutableLiveData<List<InstagramUser>> mutuals = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    
    private List<InstagramUser> allNonFollowers = new ArrayList<>();
    private List<InstagramUser> allFollowersOnly = new ArrayList<>();
    private List<InstagramUser> allMutuals = new ArrayList<>();

    public ResultsViewModel(@NonNull Application application) {
        super(application);
        repository = new FollowRepository(application);
    }

    public LiveData<FollowSnapshot> getSnapshot() { return snapshot; }
    public LiveData<List<InstagramUser>> getNonFollowers() { return nonFollowers; }
    public LiveData<List<InstagramUser>> getFollowersOnly() { return followersOnly; }
    public LiveData<List<InstagramUser>> getMutuals() { return mutuals; }
    public LiveData<Boolean> getIsLoading() { return isLoading; }

    public void loadLatestResults() {
        isLoading.setValue(true);
        repository.getLatestSnapshot(snap -> {
            if (snap != null) {
                snapshot.postValue(snap);
                repository.getSnapshotDetails(snap.getId(), (followers, following) -> {
                    List<InstagramUser> nonFollowersList = com.example.followcheck.comparison.FollowComparisonEngine.getNonFollowers(followers, following);
                    List<InstagramUser> followersOnlyList = com.example.followcheck.comparison.FollowComparisonEngine.getFollowersOnly(followers, following);
                    List<InstagramUser> mutualsList = com.example.followcheck.comparison.FollowComparisonEngine.getMutuals(followers, following);

                    allNonFollowers = nonFollowersList;
                    allFollowersOnly = followersOnlyList;
                    allMutuals = mutualsList;

                    nonFollowers.postValue(nonFollowersList);
                    followersOnly.postValue(followersOnlyList);
                    mutuals.postValue(mutualsList);
                    isLoading.postValue(false);
                });
            } else {
                isLoading.postValue(false);
            }
        });
    }

    public void filter(String query) {
        String lowerQuery = query.toLowerCase();
        nonFollowers.setValue(filterList(allNonFollowers, lowerQuery));
        followersOnly.setValue(filterList(allFollowersOnly, lowerQuery));
        mutuals.setValue(filterList(allMutuals, lowerQuery));
    }

    private List<InstagramUser> filterList(List<InstagramUser> list, String query) {
        if (query.isEmpty()) return list;
        return list.stream()
                .filter(u -> (u.getUsername() != null && u.getUsername().toLowerCase().contains(query)) 
                        || (u.getFullName() != null && u.getFullName().toLowerCase().contains(query)))
                .collect(Collectors.toList());
    }

    public void sort(boolean ascending) {
        Comparator<InstagramUser> comparator = Comparator.comparing(u -> u.getUsername() != null ? u.getUsername().toLowerCase() : "");
        if (!ascending) comparator = comparator.reversed();

        sortAndSet(allNonFollowers, nonFollowers, comparator);
        sortAndSet(allFollowersOnly, followersOnly, comparator);
        sortAndSet(allMutuals, mutuals, comparator);
    }

    private void sortAndSet(List<InstagramUser> allList, MutableLiveData<List<InstagramUser>> liveData, Comparator<InstagramUser> comparator) {
        List<InstagramUser> current = liveData.getValue();
        if (current == null) return;
        List<InstagramUser> sorted = new ArrayList<>(current);
        Collections.sort(sorted, comparator);
        liveData.setValue(sorted);
    }
}
