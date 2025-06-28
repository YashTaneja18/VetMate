package com.example.vetmate.notifications;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

public class NotificationHelper {

    public static final String CHANNEL_ID = "vetmate_reminders_channel";
    private static final String CHANNEL_NAME = "VetMate Reminders";
    private static final String CHANNEL_DESCRIPTION = "Notification channel for pet reminders";

    public static void createNotificationChannel(Application application) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription(CHANNEL_DESCRIPTION);

            NotificationManager manager = application.getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }
}
