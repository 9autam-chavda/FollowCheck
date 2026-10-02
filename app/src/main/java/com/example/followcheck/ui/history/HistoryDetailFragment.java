package com.example.followcheck.ui.history;

import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.followcheck.R;
import com.example.followcheck.comparison.FollowComparisonEngine;
import com.example.followcheck.data.model.FollowSnapshot;
import com.example.followcheck.data.model.InstagramUser;
import com.example.followcheck.data.repository.FollowRepository;
import com.example.followcheck.ui.results.FollowUserAdapter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class HistoryDetailFragment extends Fragment {

    private long snapshotId;
    private FollowRepository repository;
    private TextView textDate, valNewFollowers, valLostFollowers, valNewFollowing, valRemovedFollowing;
    private RecyclerView recyclerView;
    private FollowUserAdapter detailAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            snapshotId = getArguments().getLong("snapshotId");
        }
        repository = new FollowRepository(requireActivity().getApplication());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history_detail, container, false);

        textDate = view.findViewById(R.id.text_detail_date);
        valNewFollowers = view.findViewById(R.id.val_new_followers);
        valLostFollowers = view.findViewById(R.id.val_lost_followers);
        valNewFollowing = view.findViewById(R.id.val_new_following);
        valRemovedFollowing = view.findViewById(R.id.val_removed_following);
        recyclerView = view.findViewById(R.id.recycler_details);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        detailAdapter = new FollowUserAdapter("Changed relationship");
        recyclerView.setAdapter(detailAdapter);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        loadData();
    }

    private void loadData() {
        repository.getAllSnapshots().observe(getViewLifecycleOwner(), snapshots -> {
            if (snapshots == null) return;
            
            FollowSnapshot current = null;
            FollowSnapshot previous = null;
            
            for (int i = 0; i < snapshots.size(); i++) {
                if (snapshots.get(i).getId() == snapshotId) {
                    current = snapshots.get(i);
                    if (i + 1 < snapshots.size()) {
                        previous = snapshots.get(i + 1);
                    }
                    break;
                }
            }

            if (current != null) {
                textDate.setText(DateFormat.format("MMMM d, yyyy h:mm a", new Date(current.getTimestamp())));
                
                if (previous != null) {
                    compareSnapshots(current, previous);
                }
            }
        });
    }

    private void compareSnapshots(FollowSnapshot current, FollowSnapshot previous) {
        repository.getSnapshotDetails(current.getId(), (currFollowers, currFollowing) -> {
            repository.getSnapshotDetails(previous.getId(), (prevFollowers, prevFollowing) -> {
                List<InstagramUser> newFollowers = FollowComparisonEngine.getNewFollowers(prevFollowers, currFollowers);
                List<InstagramUser> lostFollowers = FollowComparisonEngine.getLostFollowers(prevFollowers, currFollowers);
                List<InstagramUser> newFollowing = FollowComparisonEngine.getNewFollowing(prevFollowing, currFollowing);
                List<InstagramUser> removedFollowing = FollowComparisonEngine.getRemovedFollowing(prevFollowing, currFollowing);

                requireActivity().runOnUiThread(() -> {
                    valNewFollowers.setText(String.valueOf(newFollowers.size()));
                    valLostFollowers.setText(String.valueOf(lostFollowers.size()));
                    valNewFollowing.setText(String.valueOf(newFollowing.size()));
                    valRemovedFollowing.setText(String.valueOf(removedFollowing.size()));

                    List<InstagramUser> allChanges = new ArrayList<>();
                    allChanges.addAll(newFollowers);
                    allChanges.addAll(lostFollowers);
                    detailAdapter.submitList(allChanges);
                });
            });
        });
    }
}
