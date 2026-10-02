package com.example.followcheck.ui.history;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import com.example.followcheck.data.model.FollowSnapshot;
import com.example.followcheck.data.repository.FollowRepository;
import java.util.List;

public class HistoryViewModel extends AndroidViewModel {
    private final FollowRepository repository;
    private final LiveData<List<FollowSnapshot>> allSnapshots;

    public HistoryViewModel(@NonNull Application application) {
        super(application);
        repository = new FollowRepository(application);
        allSnapshots = repository.getAllSnapshots();
    }

    public LiveData<List<FollowSnapshot>> getAllSnapshots() {
        return allSnapshots;
    }

    public void deleteHistory() {
        repository.deleteAllData(() -> {
            // Optional: Handle post-deletion UI logic if needed
        });
    }
}
