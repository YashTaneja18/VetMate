package com.example.vetmate.utils;

import android.Manifest;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;

import androidx.annotation.RequiresPermission;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.vetmate.R;
import com.example.vetmate.data.model.Reminder;
import com.example.vetmate.notifications.ReminderBroadcastReceiver;

import java.util.Date;

public class ReminderNotificationHelper {

    public static final String CHANNEL_ID = "reminder_channel";
    private static final String CHANNEL_NAME = "VetMate Reminders";
    private static final String CHANNEL_DESC = "Reminder notifications for pet care";

    private static boolean isChannelCreated = false;

    // Call this once before sending any notifications
    public static void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && !isChannelCreated) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription(CHANNEL_DESC);
            NotificationManager manager = context.getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
            isChannelCreated = true;
        }
    }

    // Call this to show a reminder notification
    public static void showReminderNotification(Context context, String title, String body, int notificationId) {
        createNotificationChannel(context);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification_bell) // Replace with your app icon
                .setContentTitle(title)
                .setContentText(body)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);

        NotificationManagerCompat manager = NotificationManagerCompat.from(context);
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        manager.notify(notificationId, builder.build());
    }
    @RequiresPermission(Manifest.permission.SCHEDULE_EXACT_ALARM)
    public static void scheduleReminder(Context context, Reminder reminder) {
        if (reminder.getReminderTime() == null || reminder.getReminderTime().toDate().before(new Date()))
            return;

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(context, ReminderBroadcastReceiver.class);
        intent.putExtra("reminderId", reminder.getReminderId());
        intent.putExtra("title", reminder.getTitle());
        intent.putExtra("type",reminder.getType());
        intent.putExtra("petName", reminder.getPetName());

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                reminder.getReminderId().hashCode(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        long triggerTime = reminder.getReminderTime().toDate().getTime();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (alarmManager.canScheduleExactAlarms()) {
                alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        triggerTime,
                        pendingIntent
                );
            } else {
                // Fallback: Inexact alarm (less precise, but still notifies)
                alarmManager.set(
                        AlarmManager.RTC_WAKEUP,
                        triggerTime,
                        pendingIntent
                );

                Log.w("Reminder", "Exact alarm permission not granted. Using fallback inexact alarm.");
            }
        } else {
            // Pre-Android 12: Just schedule the exact alarm
            alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    triggerTime,
                    pendingIntent
            );
        }
    }



}
