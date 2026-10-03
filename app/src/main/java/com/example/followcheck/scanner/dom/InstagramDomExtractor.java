package com.example.followcheck.scanner.dom;

import android.webkit.WebView;
import com.google.gson.Gson;

/**
 * Executes JavaScript in the WebView to extract follower/following candidates.
 * Updated for Phase 10: Fixed JS errors (bodyText, scroller syntax) and perfected context switching.
 */
public class InstagramDomExtractor {
    private final WebView webView;
    private final Gson gson;

    public InstagramDomExtractor(WebView webView) {
        this.webView = webView;
        this.gson = new Gson();
    }

    public interface ExtractionCallback {
        void onResult(DomExtractionResult result);
    }

    public void extract(ExtractionCallback callback) {
        if (webView == null) {
            callback.onResult(new DomExtractionResult("WebView is null"));
            return;
        }

        String script = "(function() {" +
                "  var result = {" +
                "    pageTitle: document.title," +
                "    url: window.location.href," +
                "    candidates: []," +
                "    detectedContext: 'UNKNOWN'," +
                "    isEndOfList: false," +
                "    isLoading: false," +
                "    scrollTop: 0," +
                "    scrollHeight: 0," +
                "    clientHeight: 0," +
                "    success: true," +
                "    errorMessage: ''" +
                "  };" +
                "  " +
                "  try {" +
                "    var url = result.url.toLowerCase();" +
                "    " +
                "    /* 1. Precise Context Detection - Rank signals by reliability */" +
                "    var getContext = function() {" +
                "        /* Rank 1: URL segments (Most definitive) */" +
                "        if (url.indexOf('/followers') !== -1) return 'FOLLOWERS';" +
                "        if (url.indexOf('/following') !== -1) return 'FOLLOWING';" +
                "" +
                "        /* Rank 2: Check Active Tab in current modal (ARIA state) */" +
                "        var activeTab = document.querySelector('[role=\"tab\"][aria-selected=\"true\"], [role=\"link\"][aria-selected=\"true\"], a._afxw');" +
                "        var tabText = activeTab ? (activeTab.innerText || '').toLowerCase() : '';" +
                "        if (tabText.indexOf('follower') !== -1) return 'FOLLOWERS';" +
                "        if (tabText.indexOf('following') !== -1) return 'FOLLOWING';" +
                "" +
                "        /* Rank 3: Dialog Header text */" +
                "        var header = document.querySelector('div[role=\"dialog\"] h1, div[role=\"dialog\"] h2, header h1, h1, h2');" +
                "        var headerText = header ? (header.innerText || '').toLowerCase() : '';" +
                "        if (headerText.indexOf('follower') !== -1) return 'FOLLOWERS';" +
                "        if (headerText.indexOf('following') !== -1) return 'FOLLOWING';" +
                "" +
                "        /* Rank 4: Relationship Action Button types */" +
                "        var buttons = Array.from(document.querySelectorAll('button')).map(b => (b.innerText || '').toLowerCase());" +
                "        if (buttons.some(t => t.includes('remove'))) return 'FOLLOWERS';" +
                "        if (buttons.some(t => t === 'following')) return 'FOLLOWING';" +
                "" +
                "        return 'UNKNOWN';" +
                "    };" +
                "" +
                "    result.detectedContext = getContext();" +
                "" +
                "    /* 2. Identify Scroller (Visible dialog priority) */" +
                "    var findScroller = function() {" +
                "        var dialog = document.querySelector('div[role=\"dialog\"]');" +
                "        if (dialog) {" +
                "            var s = dialog.querySelector('div[style*=\"overflow-y: auto\"], div._aano');" +
                "            if (s) return s;" +
                "            return dialog;" +
                "        }" +
                "        var known = document.querySelector('div._aano');" +
                "        if (known) return known;" +
                "        return document.scrollingElement || document.documentElement || document.body;" +
                "    };" +
                "" +
                "    var scroller = findScroller();" +
                "    result.scrollTop = scroller.scrollTop || window.pageYOffset || 0;" +
                "    result.scrollHeight = scroller.scrollHeight || document.documentElement.scrollHeight;" +
                "    result.clientHeight = scroller.clientHeight || window.innerHeight;" +
                "" +
                "    /* 3. Loading detection */" +
                "    var spinner = document.querySelector('svg[aria-label=\"Loading...\"], [role=\"progressbar\"], .spinner');" +
                "    result.isLoading = !!spinner;" +
                "" +
                "    /* 4. Extraction with row validation */" +
                "    var anchors = document.getElementsByTagName('a');" +
                "    var seen = new Set();" +
                "    var blacklist = ['explore', 'reels', 'direct', 'accounts', 'emails', 'legal', 'directory', 'p', 'stories', 'about', 'login', 'terms', 'privacy', 'help'];" +
                "" +
                "    for (var i = 0; i < anchors.length; i++) {" +
                "      var a = anchors[i];" +
                "      var href = a.getAttribute('href');" +
                "      if (!href || !href.startsWith('/') || href.startsWith('//')) continue;" +
                "      var parts = href.split('/').filter(Boolean);" +
                "      if (parts.length === 1) {" +
                "        var username = parts[0].split('?')[0].split('#')[0];" +
                "        if (!blacklist.includes(username.toLowerCase())) {" +
                "          var hasImage = a.querySelector('img') !== null;" +
                "          var textMatches = (a.innerText || '').trim().toLowerCase() === username.toLowerCase();" +
                "          var isInListItem = !!a.closest('li, [role=\"listitem\"], div[role=\"button\"], div._ab87, div._ab8v');" +
                "          if (hasImage || textMatches || isInListItem) {" +
                "            if (!seen.has(username.toLowerCase())) {" +
                "               seen.add(username.toLowerCase());" +
                "               result.candidates.push({ username: username, href: href, visible: true });" +
                "            }" +
                "          }" +
                "        }" +
                "      }" +
                "    }" +
                "    result.candidateCount = result.candidates.length;" +
                "    " +
                "    /* 5. End Detection (Corrected bodyText definition) */" +
                "    var bodyText = document.body.innerText || '';" +
                "    if (bodyText.includes('End of list') || bodyText.includes('No more followers')) {" +
                "        result.isEndOfList = true;" +
                "    }" +
                "" +
                "  } catch (err) {" +
                "    result.success = false;" +
                "    result.errorMessage = 'JS Error: ' + err.message;" +
                "  }" +
                "" +
                "  return JSON.stringify(result);" +
                "})();";

        webView.evaluateJavascript(script, value -> {
            try {
                String json = value;
                if (json != null && json.startsWith("\"") && json.endsWith("\"")) {
                    try { json = gson.fromJson(json, String.class); } catch (Exception ignored) {}
                }
                DomExtractionResult res = gson.fromJson(json, DomExtractionResult.class);
                callback.onResult(res != null ? res : new DomExtractionResult("Empty result"));
            } catch (Exception e) {
                callback.onResult(new DomExtractionResult("Parse Error: " + e.getMessage()));
            }
        });
    }

    public void scroll(int pixels, ExtractionCallback callback) {
        String scrollScript = "(function(p) {" +
                "  try {" +
                "    var findScroller = function() {" +
                "        var dialog = document.querySelector('div[role=\"dialog\"]');" +
                "        if (dialog) return dialog.querySelector('div[style*=\"overflow-y: auto\"], div._aano') || dialog;" +
                "        var known = document.querySelector('div._aano');" +
                "        if (known) return known;" +
                "        return document.scrollingElement || document.documentElement || document.body;" +
                "    };" +
                "    var scroller = findScroller();" +
                "    if (scroller) {" +
                "        if (scroller === document.documentElement || scroller === document.body || scroller === document.scrollingElement) {" +
                "            window.scrollBy(0, p);" +
                "        } else {" +
                "            scroller.scrollTop += p;" +
                "        }" +
                "        scroller.dispatchEvent(new Event('scroll', { bubbles: true }));" +
                "        return 1;" +
                "    }" +
                "    return 0;" +
                "  } catch (e) { return -1; }" +
                "})(" + pixels + ");";
        webView.evaluateJavascript(scrollScript, value -> extract(callback));
    }
}
