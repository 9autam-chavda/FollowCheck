package com.example.followcheck.scanner;

import com.example.followcheck.scanner.dom.DomContext;

public class ScanProgress {
    private final int totalCaptured;
    private final int newlyCaptured;
    private final int visibleCaptured;
    private final double scrollTop;
    private final String statusMessage;
    private final DomContext context;

    public ScanProgress(int totalCaptured, int newlyCaptured, int visibleCaptured, double scrollTop, String statusMessage, DomContext context) {
        this.totalCaptured = totalCaptured;
        this.newlyCaptured = newlyCaptured;
        this.visibleCaptured = visibleCaptured;
        this.scrollTop = scrollTop;
        this.statusMessage = statusMessage;
        this.context = context;
    }

    public int getTotalCaptured() { return totalCaptured; }
    public int getNewlyCaptured() { return newlyCaptured; }
    public int getVisibleCaptured() { return visibleCaptured; }
    public double getScrollTop() { return scrollTop; }
    public String getStatusMessage() { return statusMessage; }
    public DomContext getContext() { return context; }

    /**
     * Compatibility getter for UI components.
     */
    public int getCurrentCount() {
        return totalCaptured;
    }
}
