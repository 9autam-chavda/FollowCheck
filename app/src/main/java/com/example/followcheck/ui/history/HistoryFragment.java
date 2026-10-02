package com.example.followcheck.ui.history;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.followcheck.R;
import com.example.followcheck.data.model.FollowSnapshot;

public class HistoryFragment extends Fragment {

    private HistoryViewModel viewModel;
    private RecyclerView recyclerView;
    private HistoryAdapter adapter;
    private View emptyState;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history, container, false);

        recyclerView = view.findViewById(R.id.recycler_history);
        emptyState = view.findViewById(R.id.text_empty);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new HistoryAdapter(snapshot -> {
            Bundle bundle = new Bundle();
            bundle.putLong("snapshotId", snapshot.getId());
            Navigation.findNavController(view).navigate(R.id.action_history_to_detail, bundle);
        });
        recyclerView.setAdapter(adapter);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(HistoryViewModel.class);

        viewModel.getAllSnapshots().observe(getViewLifecycleOwner(), snapshots -> {
            if (snapshots != null) {
                adapter.submitList(snapshots);
                if (emptyState != null) {
                    emptyState.setVisibility(snapshots.isEmpty() ? View.VISIBLE : View.GONE);
                }
            }
        });
    }
}
