package com.example.followcheck.scanner;

/**
 * Factory for creating FollowDataSource instances.
 */
public class FollowDataSourceFactory {

    public enum SourceType {
        REAL,
        MOCK
    }

    public static FollowDataSource createDataSource(SourceType type, int mockScanNum) {
        if (type == SourceType.MOCK) {
            return new MockFollowDataSource(mockScanNum);
        } else {
            return new InstagramScanner();
        }
    }

    /**
     * Helper to get the default data source.
     * Changed to REAL by default to allow technical validation of the WebView scanner.
     */
    public static FollowDataSource getDefaultDataSource(int mockScanNum) {
        // Return REAL to ensure actual DOM scraping is tested.
        // Change to SourceType.MOCK only for isolated UI testing without Instagram login.
        return createDataSource(SourceType.REAL, 0);
    }
}
