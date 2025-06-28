package com.example.vetmate;

import android.app.Application;

import com.example.vetmate.notifications.NotificationHelper;

public class VetMateApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        NotificationHelper.createNotificationChannel(this);
    }
}
