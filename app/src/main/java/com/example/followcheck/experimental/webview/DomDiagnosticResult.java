package com.example.followcheck.experimental.webview;

import java.util.List;

public class DomDiagnosticResult {
    private String pageTitle;
    private String url;
    private DomContext detectedContext;
    private int candidateCount;
    private List<String> candidateUsernames;
    private int bodyTextLength;
    private int anchorCount;
    private int visibleAnchorCount;
    private int scrollableContainerCount;
    private long timestamp;
    private boolean success;
    private String errorMessage;
    private boolean truncated;
    private String completeness = "UNKNOWN";

    public DomDiagnosticResult() {
        this.timestamp = System.currentTimeMillis();
    }

    // Getters and Setters
    public String getPageTitle() { return pageTitle; }
    public void setPageTitle(String pageTitle) { this.pageTitle = pageTitle; }

    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }

    public DomContext getDetectedContext() { return detectedContext; }
    public void setDetectedContext(DomContext detectedContext) { this.detectedContext = detectedContext; }

    public int getCandidateCount() { return candidateCount; }
    public void setCandidateCount(int candidateCount) { this.candidateCount = candidateCount; }

    public List<String> getCandidateUsernames() { return candidateUsernames; }
    public void setCandidateUsernames(List<String> candidateUsernames) { this.candidateUsernames = candidateUsernames; }

    public int getBodyTextLength() { return bodyTextLength; }
    public void setBodyTextLength(int bodyTextLength) { this.bodyTextLength = bodyTextLength; }

    public int getAnchorCount() { return anchorCount; }
    public void setAnchorCount(int anchorCount) { this.anchorCount = anchorCount; }

    public int getVisibleAnchorCount() { return visibleAnchorCount; }
    public void setVisibleAnchorCount(int visibleAnchorCount) { this.visibleAnchorCount = visibleAnchorCount; }

    public int getScrollableContainerCount() { return scrollableContainerCount; }
    public void setScrollableContainerCount(int scrollableContainerCount) { this.scrollableContainerCount = scrollableContainerCount; }

    public long getTimestamp() { return timestamp; }

    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }

    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }

    public boolean isTruncated() { return truncated; }
    public void setTruncated(boolean truncated) { this.truncated = truncated; }

    public String getCompleteness() { return completeness; }
    public void setCompleteness(String completeness) { this.completeness = completeness; }
}
