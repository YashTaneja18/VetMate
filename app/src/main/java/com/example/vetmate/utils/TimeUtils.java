// TimeUtils.java
package com.example.vetmate.utils;

import com.google.firebase.Timestamp;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class TimeUtils {
    public static String formatTimestamp(Timestamp timestamp) {
        if (timestamp == null) return "";
        return new SimpleDateFormat("dd MMM, h:mm a", Locale.getDefault())
                .format(timestamp.toDate());
    }
}
