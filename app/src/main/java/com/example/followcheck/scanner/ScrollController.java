package com.example.followcheck.scanner;

import com.example.followcheck.scanner.dom.DomContext;
import com.example.followcheck.scanner.dom.DomExtractionResult;
import com.example.followcheck.scanner.dom.InstagramDomExtractor;

/**
 * Controls the incremental scrolling of the Instagram relationship list.
 * Updated for Phase 10: Precision metrics, context-switch awareness, and adaptive thresholds.
 */
public class ScrollController {
    private final InstagramDomExtractor extractor;
    private final int scrollIncrement = 300; 
    
    private double lastScrollTop = -1.0;
    private DomContext lastContext = DomContext.UNKNOWN;
    private int consecutiveNoMovement = 0;
    private static final int MAX_NO_MOVEMENT = 10; 

    public ScrollController(InstagramDomExtractor extractor) {
        this.extractor = extractor;
    }

    public void advance(InstagramDomExtractor.ExtractionCallback callback) {
        extractor.scroll(scrollIncrement, callback);
    }

    public boolean hasMoved(DomExtractionResult result) {
        double currentTop = result.getScrollTop();
        DomContext currentContext = result.getDetectedContext();

        // Fix: Detect if the user manually switched tabs in the WebView.
        // If the context changed, we reset the movement state to prevent a "Stalled" stop.
        if (currentContext != lastContext && currentContext != DomContext.UNKNOWN) {
            lastContext = currentContext;
            lastScrollTop = currentTop;
            consecutiveNoMovement = 0;
            return true; // Context switch is significant progress
        }

        // Sub-pixel comparison with epsilon to filter out browser rendering noise
        if (currentTop > lastScrollTop + 0.1) {
            lastScrollTop = currentTop;
            consecutiveNoMovement = 0;
            return true;
        } else {
            // Only count as 'stalled' if Instagram is NOT explicitly showing a loading spinner
            if (!result.isLoading()) {
                consecutiveNoMovement++;
            }
            return false;
        }
    }

    public boolean isStuck() {
        return consecutiveNoMovement >= MAX_NO_MOVEMENT;
    }
    
    public void reset() {
        lastScrollTop = -1.0;
        lastContext = DomContext.UNKNOWN;
        consecutiveNoMovement = 0;
    }
}
