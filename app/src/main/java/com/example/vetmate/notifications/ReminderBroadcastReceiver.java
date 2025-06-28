package com.example.vetmate.notifications;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;

import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.example.vetmate.R;
import com.example.vetmate.ui.activity.ReminderActivity;

public class ReminderBroadcastReceiver extends BroadcastReceiver {

    public static final String EXTRA_TITLE = "title";
    public static final String EXTRA_NOTIFICATION_ID = "notification_id";
    public static final String EXTRA_PET_NAME="petName";
    public static final String EXTRA_TYPE="type";

    @Override
    public void onReceive(Context context, Intent intent) {
        String description = intent.getStringExtra(EXTRA_TITLE);
        String petName = intent.getStringExtra(EXTRA_PET_NAME);
        String type = intent.getStringExtra(EXTRA_TYPE);
        String title = (petName != null && type != null)
                ? petName + " - " + type
                : (petName != null ? petName : "Reminder");
        int notificationId = intent.getIntExtra(EXTRA_NOTIFICATION_ID, 0);

        Intent activityIntent = new Intent(context, ReminderActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(
                context,
                0,
                activityIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, NotificationHelper.CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification_bell) // use your app icon here
                .setContentTitle(title)
                .setContentText(description)
                .setAutoCancel(true)
                .setColor(ContextCompat.getColor(context, R.color.colorPrimary))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(contentIntent);

        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (manager != null) {
            manager.notify(notificationId, builder.build());
        }
    }
}
