package com.example.followcheck.scanner;

public enum ScannerState {
    IDLE,
    INITIALIZING,
    PREPARING,
    RUNNING,
    SAVING,
    COMPLETED,
    CANCELLED,
    SESSION_EXPIRED,
    ERROR
}
