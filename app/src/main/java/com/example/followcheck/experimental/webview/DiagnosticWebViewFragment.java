package com.example.followcheck.experimental.webview;

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
import androidx.navigation.Navigation;

import com.example.followcheck.R;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.progressindicator.LinearProgressIndicator;

import java.util.HashSet;
import java.util.Set;

public class DiagnosticWebViewFragment extends Fragment {

    private WebView webView;
    private LinearProgressIndicator progressIndicator;
    private TextView textContext, textStats, textDomInfo;
    private MaterialButton btnRunDiagnostic;
    private WebViewDomDiagnostic diagnosticTool;
    
    private final Set<String> uniqueCandidates = new HashSet<>();
    private int lastCount = 0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_diagnostic_webview, container, false);
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        MaterialToolbar toolbar = view.findViewById(R.id.toolbar);
        progressIndicator = view.findViewById(R.id.progress_indicator);
        webView = view.findViewById(R.id.webview);
        textContext = view.findViewById(R.id.text_context);
        textStats = view.findViewById(R.id.text_stats);
        textDomInfo = view.findViewById(R.id.text_dom_info);
        btnRunDiagnostic = view.findViewById(R.id.btn_run_diagnostic);

        toolbar.setNavigationOnClickListener(v -> Navigation.findNavController(v).popBackStack());

        setupWebView();
        diagnosticTool = new WebViewDomDiagnostic(webView);

        btnRunDiagnostic.setOnClickListener(v -> runDiagnostic());

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
        webView.setWebViewClient(new WebViewClient());
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                progressIndicator.setProgress(newProgress);
                progressIndicator.setVisibility(newProgress < 100 ? View.VISIBLE : View.GONE);
            }
        });
    }

    private void runDiagnostic() {
        btnRunDiagnostic.setEnabled(false);
        diagnosticTool.runDiagnostic(result -> {
            if (isAdded()) {
                btnRunDiagnostic.setEnabled(true);
                updateUi(result);
            }
        });
    }

    private void updateUi(DomDiagnosticResult result) {
        if (!result.isSuccess()) {
            Toast.makeText(getContext(), "Diagnostic failed: " + result.getErrorMessage(), Toast.LENGTH_SHORT).show();
            return;
        }

        textContext.setText("Context: " + result.getDetectedContext().name());
        
        if (result.getCandidateUsernames() != null) {
            uniqueCandidates.addAll(result.getCandidateUsernames());
        }
        
        int newEntries = result.getCandidateCount() - lastCount;
        lastCount = result.getCandidateCount();

        String stats = String.format("Current: %d | Unique: %d | New: %d", 
                result.getCandidateCount(), uniqueCandidates.size(), newEntries);
        textStats.setText(stats);

        String dom = String.format("DOM: %d anchors | %d visible | %d scrolls", 
                result.getAnchorCount(), result.getVisibleAnchorCount(), result.getScrollableContainerCount());
        textDomInfo.setText(dom);
        
        if (result.isTruncated()) {
            Toast.makeText(getContext(), "Result truncated (found > 100)", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        webView.saveState(outState);
    }
}
