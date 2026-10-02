package com.example.followcheck.ui.results;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.followcheck.R;
import com.google.android.material.tabs.TabLayout;

public class ResultsFragment extends Fragment {

    private ResultsViewModel viewModel;
    private RecyclerView recyclerView;
    private FollowUserAdapter nonFollowersAdapter;
    private FollowUserAdapter followersOnlyAdapter;
    private FollowUserAdapter mutualsAdapter;
    private ProgressBar progressBar;
    private TextView textEmpty;
    private TextView countNonFollowers, countFollowersOnly, countMutuals;
    private TabLayout tabLayout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_results, container, false);

        recyclerView = view.findViewById(R.id.recycler_results);
        progressBar = view.findViewById(R.id.progress_loading);
        textEmpty = view.findViewById(R.id.text_empty);
        tabLayout = view.findViewById(R.id.tab_layout);
        EditText editSearch = view.findViewById(R.id.edit_search);
        
        countNonFollowers = view.findViewById(R.id.text_count_non_followers);
        countFollowersOnly = view.findViewById(R.id.text_count_followers_only);
        countMutuals = view.findViewById(R.id.text_count_mutuals);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        
        nonFollowersAdapter = new FollowUserAdapter(getString(R.string.status_non_follower));
        followersOnlyAdapter = new FollowUserAdapter(getString(R.string.status_follower_only));
        mutualsAdapter = new FollowUserAdapter(getString(R.string.status_mutual));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                updateAdapter(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });

        editSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                viewModel.filter(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(ResultsViewModel.class);

        setupMenu();

        viewModel.getIsLoading().observe(getViewLifecycleOwner(), loading -> {
            progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
            recyclerView.setVisibility(loading ? View.GONE : View.VISIBLE);
        });

        viewModel.getNonFollowers().observe(getViewLifecycleOwner(), users -> {
            nonFollowersAdapter.submitList(users);
            countNonFollowers.setText(String.valueOf(users.size()));
            if (tabLayout.getSelectedTabPosition() == 0) checkEmpty(users.isEmpty());
        });

        viewModel.getFollowersOnly().observe(getViewLifecycleOwner(), users -> {
            followersOnlyAdapter.submitList(users);
            countFollowersOnly.setText(String.valueOf(users.size()));
            if (tabLayout.getSelectedTabPosition() == 1) checkEmpty(users.isEmpty());
        });

        viewModel.getMutuals().observe(getViewLifecycleOwner(), users -> {
            mutualsAdapter.submitList(users);
            countMutuals.setText(String.valueOf(users.size()));
            if (tabLayout.getSelectedTabPosition() == 2) checkEmpty(users.isEmpty());
        });

        viewModel.loadLatestResults();
        updateAdapter(tabLayout.getSelectedTabPosition());
    }

    private void setupMenu() {
        requireActivity().addMenuProvider(new MenuProvider() {
            @Override
            public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
                menuInflater.inflate(R.menu.results_menu, menu);
            }

            @Override
            public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
                if (menuItem.getItemId() == R.id.action_sort_az) {
                    viewModel.sort(true);
                    return true;
                } else if (menuItem.getItemId() == R.id.action_sort_za) {
                    viewModel.sort(false);
                    return true;
                }
                return false;
            }
        }, getViewLifecycleOwner(), Lifecycle.State.RESUMED);
    }

    private void updateAdapter(int position) {
        switch (position) {
            case 0:
                recyclerView.setAdapter(nonFollowersAdapter);
                checkEmpty(nonFollowersAdapter.getCurrentList().isEmpty());
                break;
            case 1:
                recyclerView.setAdapter(followersOnlyAdapter);
                checkEmpty(followersOnlyAdapter.getCurrentList().isEmpty());
                break;
            case 2:
                recyclerView.setAdapter(mutualsAdapter);
                checkEmpty(mutualsAdapter.getCurrentList().isEmpty());
                break;
        }
    }

    private void checkEmpty(boolean isEmpty) {
        textEmpty.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
    }
}
