package com.example.followcheck.data.local;

import androidx.room.TypeConverter;
import com.example.followcheck.scanner.ScanCompleteness;

public class Converters {
    @TypeConverter
    public static String fromScanCompleteness(ScanCompleteness completeness) {
        return completeness == null ? null : completeness.name();
    }

    @TypeConverter
    public static ScanCompleteness toScanCompleteness(String name) {
        return name == null ? null : ScanCompleteness.valueOf(name);
    }
}
