package com.example.followcheck.scanner;

/**
 * States for the Instagram scanning process.
 */
public enum ScannerState {
    IDLE,
    INITIALIZING,
    PREPARING,
    CHECKING_WEBVIEW,
    VERIFYING_INSTAGRAM,
    VERIFYING_PROFILE,
    VERIFYING_FOLLOWERS_CONTEXT,
    VERIFYING_FOLLOWING_CONTEXT,
    COLLECTING,
    RUNNING,    // Added for backward compatibility
    PROCESSING,
    SAVING,
    COMPARING,
    COMPLETED,
    PARTIAL,
    FAILED,
    ERROR,      // Added for backward compatibility
    CANCELLED
}
