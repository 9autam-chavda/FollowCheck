package com.example.followcheck.ui.scan;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.followcheck.R;
import com.example.followcheck.scanner.ScannerState;
import com.example.followcheck.scanner.ScanProgress;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.progressindicator.LinearProgressIndicator;

public class ScanFragment extends Fragment {

    private ScanViewModel viewModel;
    private WebView webView;
    private TextView textStatus, textProgressCount, textScanType;
    private MaterialButton btnStart, btnFinish;
    private LinearProgressIndicator progressHorizontal;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Important: Scope to Activity so data persists when navigating between fragments in one session
        viewModel = new ViewModelProvider(requireActivity()).get(ScanViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_scan, container, false);

        MaterialToolbar toolbar = view.findViewById(R.id.toolbar);
        webView = view.findViewById(R.id.webview_scan);
        textStatus = view.findViewById(R.id.text_scan_status);
        textProgressCount = view.findViewById(R.id.text_progress_count);
        textScanType = view.findViewById(R.id.text_scan_type);
        btnStart = view.findViewById(R.id.btn_start_scan);
        btnFinish = view.findViewById(R.id.btn_finish_scan);
        progressHorizontal = view.findViewById(R.id.progress_horizontal);

        toolbar.setNavigationOnClickListener(v -> {
            if (viewModel != null) {
                viewModel.cancelScan();
            }
            Navigation.findNavController(v).popBackStack();
        });

        btnStart.setOnClickListener(v -> {
            if (viewModel != null) viewModel.startScan();
        });
        
        btnFinish.setOnClickListener(v -> {
            if (viewModel != null) viewModel.finishScan();
        });

        return view;
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        setupWebView();
        if (viewModel != null) {
            viewModel.setWebView(webView);

            viewModel.getState().observe(getViewLifecycleOwner(), this::updateUiState);
            viewModel.getProgress().observe(getViewLifecycleOwner(), this::updateProgress);
            
            viewModel.getNavigateToResults().observe(getViewLifecycleOwner(), navigate -> {
                if (Boolean.TRUE.equals(navigate)) {
                    Navigation.findNavController(requireView()).navigate(R.id.action_scan_to_results);
                    viewModel.onNavigatedToResults();
                }
            });

            viewModel.getError().observe(getViewLifecycleOwner(), error -> {
                if (error != null) {
                    Toast.makeText(getContext(), error, Toast.LENGTH_LONG).show();
                }
            });
        }

        if (savedInstanceState == null) {
            webView.loadUrl("https://www.instagram.com/");
        } else {
            webView.restoreState(savedInstanceState);
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void setupWebView() {
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setUserAgentString("Mozilla/5.0 (Linux; Android 10; SM-G973F) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/88.0.4324.181 Mobile Safari/537.36");
        webView.setWebViewClient(new WebViewClient());
        webView.setWebChromeClient(new WebChromeClient());
    }

    private void updateUiState(ScannerState state) {
        if (state == null) return;
        switch (state) {
            case IDLE:
                textStatus.setText("Ready to Scan");
                btnStart.setEnabled(true);
                btnFinish.setEnabled(false);
                progressHorizontal.setVisibility(View.GONE);
                break;
            case RUNNING:
            case COLLECTING:
                textStatus.setText("Scanning...");
                btnStart.setEnabled(false);
                btnFinish.setEnabled(true);
                progressHorizontal.setVisibility(View.VISIBLE);
                break;
            case SAVING:
                textStatus.setText("Finalizing Data...");
                btnStart.setEnabled(false);
                btnFinish.setEnabled(false);
                break;
            case COMPLETED:
                textStatus.setText("Scan Complete");
                progressHorizontal.setVisibility(View.GONE);
                break;
            case FAILED:
                textStatus.setText("Scan Failed");
                btnStart.setEnabled(true);
                btnFinish.setEnabled(false);
                progressHorizontal.setVisibility(View.GONE);
                break;
            case CANCELLED:
                textStatus.setText("Scan Stopped");
                btnStart.setEnabled(true);
                btnFinish.setEnabled(false);
                progressHorizontal.setVisibility(View.GONE);
                break;
        }
    }

    private void updateProgress(ScanProgress progress) {
        if (progress == null) return;
        textProgressCount.setText("Users found: " + progress.getTotalCaptured());
        String contextName = (progress.getContext() != null) ? progress.getContext().name() : "Searching...";
        textScanType.setText("Context: " + contextName);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        webView.saveState(outState);
    }
}
