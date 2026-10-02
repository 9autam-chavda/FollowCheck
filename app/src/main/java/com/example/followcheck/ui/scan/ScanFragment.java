package com.example.followcheck.ui.scan;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.followcheck.R;
import com.example.followcheck.scanner.ScannerState;
import com.google.android.material.progressindicator.CircularProgressIndicator;

public class ScanFragment extends Fragment {

    private ScanViewModel viewModel;
    private TextView textStatus, textProgressCount, textScanType;
    private CircularProgressIndicator progressIndicator;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_scan, container, false);

        textStatus = view.findViewById(R.id.text_scan_status);
        textProgressCount = view.findViewById(R.id.text_progress_count);
        textScanType = view.findViewById(R.id.text_scan_type);
        progressIndicator = view.findViewById(R.id.progress_indicator);

        view.findViewById(R.id.btn_cancel_scan).setOnClickListener(v -> 
            Navigation.findNavController(v).popBackStack()
        );

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(ScanViewModel.class);

        viewModel.getState().observe(getViewLifecycleOwner(), state -> {
            if (state == ScannerState.COMPLETED) {
                Navigation.findNavController(view).navigate(R.id.navigation_results);
            } else if (state == ScannerState.INITIALIZING) {
                textStatus.setText(R.string.scan_analyzing);
            }
        });

        viewModel.getProgress().observe(getViewLifecycleOwner(), p -> {
            progressIndicator.setProgress(p, true);
        });

        viewModel.getTotal().observe(getViewLifecycleOwner(), t -> {
            progressIndicator.setMax(t);
        });

        viewModel.getScanResult().observe(getViewLifecycleOwner(), result -> {
            if (result != null) {
                textProgressCount.setText(String.format("%d / %d", 
                        result.getFollowers().size(), result.getFollowing().size()));
            }
        });

        // Start the mock scan
        viewModel.startScan();
    }
}
