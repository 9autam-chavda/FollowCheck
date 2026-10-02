package com.example.followcheck.ui.instagram;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.followcheck.R;
import com.example.followcheck.scanner.LoginState;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.progressindicator.LinearProgressIndicator;

public class InstagramWebViewFragment extends Fragment {

    private InstagramWebViewViewModel viewModel;
    private WebView webView;
    private LinearProgressIndicator progressIndicator;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_instagram_webview, container, false);
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(InstagramWebViewViewModel.class);

        MaterialToolbar toolbar = view.findViewById(R.id.toolbar);
        progressIndicator = view.findViewById(R.id.progress_indicator);
        webView = view.findViewById(R.id.webview);

        toolbar.setNavigationOnClickListener(v -> Navigation.findNavController(v).popBackStack());

        setupWebView();

        viewModel.getProgress().observe(getViewLifecycleOwner(), progress -> {
            progressIndicator.setProgress(progress);
        });

        viewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            progressIndicator.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        });

        if (savedInstanceState == null) {
            webView.loadUrl("https://www.instagram.com/accounts/login/");
        } else {
            webView.restoreState(savedInstanceState);
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void setupWebView() {
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setSupportZoom(true);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setDisplayZoomControls(false);

        // Security settings
        webView.getSettings().setAllowFileAccess(false);
        webView.getSettings().setAllowContentAccess(false);

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                viewModel.setProgress(0);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                viewModel.setProgress(100);
                
                // Basic observable signals for login state
                if (url.contains("instagram.com/accounts/login")) {
                    viewModel.updateLoginState(LoginState.LOGGED_OUT);
                } else if (url.equals("https://www.instagram.com/") || url.contains("instagram.com/direct/inbox")) {
                    viewModel.updateLoginState(LoginState.LOGGED_IN);
                    // Navigation back to home after successful login can be triggered here or via user
                }
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                String url = request.getUrl().toString();
                if (url.contains("instagram.com")) {
                    return false; // Allow Instagram navigation
                }
                return true; // Block external navigation
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
                // Handle network errors
            }
        });

        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                viewModel.setProgress(newProgress);
            }
        });
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        webView.saveState(outState);
    }
}
