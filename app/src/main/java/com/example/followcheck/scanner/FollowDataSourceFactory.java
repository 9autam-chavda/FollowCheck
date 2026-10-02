package com.example.followcheck.scanner;

import com.example.followcheck.BuildConfig;

/**
 * Factory for creating FollowDataSource instances.
 * In a real application, this might be handled by Dependency Injection.
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
     * Helper to get the default data source based on build type.
     */
    public static FollowDataSource getDefaultDataSource(int mockScanNum) {
        if (BuildConfig.DEBUG) {
            return createDataSource(SourceType.MOCK, mockScanNum);
        } else {
            return createDataSource(SourceType.REAL, 0);
        }
    }
}
