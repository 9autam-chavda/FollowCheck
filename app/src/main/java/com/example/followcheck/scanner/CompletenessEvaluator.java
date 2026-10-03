package com.example.followcheck.scanner;

import com.example.followcheck.scanner.dom.DomExtractionResult;

/**
 * Evaluates multiple signals to determine the completeness of a relationship scan.
 */
public class CompletenessEvaluator {

    public static class Evidence {
        public boolean endOfListSignal;
        public boolean scrollReachedEnd;
        public boolean countStabilized;
        public boolean isLoading;
        public int consecutiveNoProgress;

        public Evidence(DomExtractionResult result, int consecutiveNoProgress) {
            this.endOfListSignal = result.isEndOfList();
            this.isLoading = result.isLoading();
            this.scrollReachedEnd = (result.getScrollTop() + result.getClientHeight() >= result.getScrollHeight() - 15);
            this.countStabilized = (consecutiveNoProgress > 8);
            this.consecutiveNoProgress = consecutiveNoProgress;
        }
    }

    /**
     * Evaluates the current evidence to determine completeness.
     */
    public static ScanCompleteness evaluate(Evidence evidence) {
        // If Instagram is explicitly loading, it is NOT complete.
        if (evidence.isLoading) {
            return ScanCompleteness.PARTIAL;
        }

        // Strong Evidence: Explicit DOM signal from Instagram "End of list"
        if (evidence.endOfListSignal) {
            return ScanCompleteness.COMPLETE;
        }

        // Strong Evidence: Physical scroll reached bottom and no loading spinner visible
        if (evidence.scrollReachedEnd && evidence.consecutiveNoProgress >= 3) {
            return ScanCompleteness.COMPLETE;
        }

        // Heuristic: No new data for a significant number of attempts and no spinner
        if (evidence.consecutiveNoProgress >= 12) {
            return ScanCompleteness.COMPLETE;
        }

        return ScanCompleteness.PARTIAL;
    }
}
