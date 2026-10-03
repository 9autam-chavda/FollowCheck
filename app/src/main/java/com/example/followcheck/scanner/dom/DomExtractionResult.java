package com.example.followcheck.scanner.dom;

import java.util.List;

public class DomExtractionResult {
    private String pageTitle;
    private String url;
    private DomContext detectedContext;
    private int candidateCount;
    private List<DomCandidate> candidates;
    private boolean isEndOfList;
    private boolean isLoading; // Detects IG loading spinner
    private boolean success;
    private String errorMessage;

    // Use double to handle subpixel values from modern WebView (fixes NumberFormatException)
    private double scrollTop;
    private double scrollHeight;
    private double clientHeight;

    public DomExtractionResult() {
        this.success = true;
    }

    public DomExtractionResult(String errorMessage) {
        this.success = false;
        this.errorMessage = errorMessage;
    }

    public String getPageTitle() { return pageTitle; }
    public String getUrl() { return url; }
    public DomContext getDetectedContext() { return detectedContext; }
    public int getCandidateCount() { return candidateCount; }
    public List<DomCandidate> getCandidates() { return candidates; }
    public boolean isEndOfList() { return isEndOfList; }
    public boolean isLoading() { return isLoading; }
    public boolean isSuccess() { return success; }
    public String getErrorMessage() { return errorMessage; }

    public double getScrollTop() { return scrollTop; }
    public double getScrollHeight() { return scrollHeight; }
    public double getClientHeight() { return clientHeight; }

    public void setScrollMetrics(double scrollTop, double scrollHeight, double clientHeight) {
        this.scrollTop = scrollTop;
        this.scrollHeight = scrollHeight;
        this.clientHeight = clientHeight;
    }
}
