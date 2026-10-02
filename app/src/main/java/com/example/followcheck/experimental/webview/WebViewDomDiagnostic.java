package com.example.followcheck.experimental.webview;

import android.webkit.ValueCallback;
import android.webkit.WebView;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.util.ArrayList;
import java.util.List;

public class WebViewDomDiagnostic {

    public interface DiagnosticCallback {
        void onResult(DomDiagnosticResult result);
    }

    private final WebView webView;
    private final Gson gson;

    public WebViewDomDiagnostic(WebView webView) {
        this.webView = webView;
        this.gson = new Gson();
    }

    public void runDiagnostic(DiagnosticCallback callback) {
        if (webView == null) {
            DomDiagnosticResult error = new DomDiagnosticResult();
            error.setSuccess(false);
            error.setErrorMessage("WebView is null");
            callback.onResult(error);
            return;
        }

        String script = "(function() {" +
                "  var result = {" +
                "    pageTitle: document.title," +
                "    url: window.location.href," +
                "    bodyTextLength: document.body ? document.body.innerText.length : 0," +
                "    anchorCount: document.getElementsByTagName('a').length," +
                "    visibleAnchorCount: 0," +
                "    scrollableContainerCount: 0," +
                "    candidateUsernames: []," +
                "    detectedContext: 'UNKNOWN'," +
                "    success: true" +
                "  };" +
                "" +
                "  var anchors = document.getElementsByTagName('a');" +
                "  var candidates = new Set();" +
                "" +
                "  for (var i = 0; i < anchors.length; i++) {" +
                "    var a = anchors[i];" +
                "    var href = a.getAttribute('href');" +
                "    var isVisible = a.offsetWidth > 0 && a.offsetHeight > 0;" +
                "    if (isVisible) result.visibleAnchorCount++;" +
                "" +
                "    if (href && href.length > 1 && href.startsWith('/') && !href.includes('?') && !href.includes('.')) {" +
                "      var username = href.substring(1).replace(/\\/$/, '');" +
                "      if (username && !['explore', 'reels', 'direct', 'accounts', 'emails', 'legal', 'directory'].includes(username)) {" +
                "        candidates.add(username);" +
                "      }" +
                "    }" +
                "  }" +
                "" +
                "  result.candidateUsernames = Array.from(candidates).slice(0, 100);" +
                "  result.candidateCount = candidates.size;" +
                "  result.truncated = candidates.size > 100;" +
                "" +
                "  if (result.url.includes('/followers/')) result.detectedContext = 'FOLLOWERS';" +
                "  else if (result.url.includes('/following/')) result.detectedContext = 'FOLLOWING';" +
                "  else if (result.url.match(/instagram\\.com\\/[^\\/]+\\/?$/)) result.detectedContext = 'PROFILE';" +
                "  else if (result.url === 'https://www.instagram.com/') result.detectedContext = 'HOME';" +
                "" +
                "  var allElements = document.querySelectorAll('*');" +
                "  for (var j = 0; j < allElements.length; j++) {" +
                "    var style = window.getComputedStyle(allElements[j]);" +
                "    if (style.overflowY === 'auto' || style.overflowY === 'scroll') {" +
                "      result.scrollableContainerCount++;" +
                "    }" +
                "  }" +
                "" +
                "  return JSON.stringify(result);" +
                "})();";

        webView.evaluateJavascript(script, value -> {
            if (value == null || value.equals("null") || value.isEmpty()) {
                DomDiagnosticResult error = new DomDiagnosticResult();
                error.setSuccess(false);
                error.setErrorMessage("Empty result from JS");
                callback.onResult(error);
                return;
            }

            try {
                // Remove surrounding quotes if evaluateJavascript returned a quoted string
                String json = value;
                if (json.startsWith("\"") && json.endsWith("\"")) {
                    json = json.substring(1, json.length() - 1)
                            .replace("\\\"", "\"")
                            .replace("\\\\", "\\");
                }
                
                DomDiagnosticResult result = gson.fromJson(json, DomDiagnosticResult.class);
                callback.onResult(result);
            } catch (JsonSyntaxException e) {
                DomDiagnosticResult error = new DomDiagnosticResult();
                error.setSuccess(false);
                error.setErrorMessage("JSON Parse Error: " + e.getMessage());
                callback.onResult(error);
            }
        });
    }
}
