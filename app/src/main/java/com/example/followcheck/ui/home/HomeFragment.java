package com.example.followcheck.ui.home;

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
import androidx.navigation.Navigation;

import com.example.followcheck.BuildConfig;
import com.example.followcheck.R;
import com.example.followcheck.scanner.LoginState;
import com.google.android.material.button.MaterialButton;

import java.util.Date;

public class HomeFragment extends Fragment {

    private HomeViewModel viewModel;
    private TextView valFollowers, valFollowing, valNotFollowingBack, valYouNotFollowingBack;
    private TextView textLastScan, valNewFollowers, valLostFollowers, textSinceLast;
    private TextView textConnectionStatus, textConnectionDetail;
    private MaterialButton btnConnect, btnScanNow;
    private View layoutChanges;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        valFollowers = view.findViewById(R.id.val_followers);
        valFollowing = view.findViewById(R.id.val_following);
        valNotFollowingBack = view.findViewById(R.id.val_not_following_back);
        valYouNotFollowingBack = view.findViewById(R.id.val_you_not_following_back);
        textLastScan = view.findViewById(R.id.text_last_scan);
        valNewFollowers = view.findViewById(R.id.val_new_followers);
        valLostFollowers = view.findViewById(R.id.val_lost_followers);
        textSinceLast = view.findViewById(R.id.text_since_last);
        layoutChanges = view.findViewById(R.id.layout_changes);
        
        textConnectionStatus = view.findViewById(R.id.text_connection_status);
        textConnectionDetail = view.findViewById(R.id.text_connection_detail);
        btnConnect = view.findViewById(R.id.btn_connect);
        btnScanNow = view.findViewById(R.id.btn_scan_now);

        btnConnect.setOnClickListener(v -> 
            Navigation.findNavController(v).navigate(R.id.action_home_to_connect)
        );

        btnScanNow.setOnClickListener(v -> {
            if (BuildConfig.DEBUG || viewModel.getLoginState().getValue() == LoginState.LOGGED_IN) {
                Navigation.findNavController(v).navigate(R.id.action_home_to_scan);
            } else {
                Navigation.findNavController(v).navigate(R.id.action_home_to_connect);
            }
        });

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        viewModel.getLatestSnapshot().observe(getViewLifecycleOwner(), snapshot -> {
            if (snapshot != null) {
                valFollowers.setText(String.valueOf(snapshot.getFollowersCount()));
                valFollowing.setText(String.valueOf(snapshot.getFollowingCount()));
                String dateStr = DateFormat.format("MMM d, h:mm a", new Date(snapshot.getTimestamp())).toString();
                textLastScan.setText(getString(R.string.last_scan_prefix, dateStr));
            } else {
                textLastScan.setText(R.string.no_scans_yet);
            }
        });

        viewModel.getNonFollowersCount().observe(getViewLifecycleOwner(), count -> 
            valNotFollowingBack.setText(String.valueOf(count))
        );

        viewModel.getYouDontFollowBackCount().observe(getViewLifecycleOwner(), count -> 
            valYouNotFollowingBack.setText(String.valueOf(count))
        );

        viewModel.getNewFollowersCount().observe(getViewLifecycleOwner(), count -> {
            valNewFollowers.setText("+" + count);
            checkChangesVisibility();
        });

        viewModel.getLostFollowersCount().observe(getViewLifecycleOwner(), count -> {
            valLostFollowers.setText("-" + count);
            checkChangesVisibility();
        });

        viewModel.getLoginState().observe(getViewLifecycleOwner(), state -> {
            if (state == LoginState.LOGGED_IN) {
                textConnectionStatus.setText("Instagram connected");
                textConnectionDetail.setText("Your web session is active");
                btnConnect.setText("Open");
            } else {
                textConnectionStatus.setText("Not connected");
                textConnectionDetail.setText("Connect your Instagram web session");
                btnConnect.setText("Connect");
            }
        });
        
        viewModel.checkInstagramSession();
        viewModel.loadDashboardData();
    }

    private void checkChangesVisibility() {
        Integer newCount = viewModel.getNewFollowersCount().getValue();
        Integer lostCount = viewModel.getLostFollowersCount().getValue();
        if ((newCount != null && newCount > 0) || (lostCount != null && lostCount > 0)) {
            textSinceLast.setVisibility(View.VISIBLE);
            layoutChanges.setVisibility(View.VISIBLE);
        }
    }
}
