package com.example.vetmate.data.repository;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.vetmate.data.model.Reminder;
import com.example.vetmate.utils.ReminderNotificationHelper;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReminderRepository {

    private static final String TAG = "ReminderRepository";
    private static final String USERS = "users";
    private static final String PETS = "pets";
    private static final String REMINDERS = "reminders";

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final FirebaseAuth auth = FirebaseAuth.getInstance();

    public String getUserId() {
        return auth.getCurrentUser() != null ? auth.getCurrentUser().getUid() : null;
    }

    // Callback interfaces
    public interface ReminderListCallback {
        void onSuccess(List<Reminder> reminders);
        void onFailure(String errorMessage);
    }

    public interface ReminderCallback {
        void onSuccess();
        void onFailure(String errorMessage);
    }

    // Add a reminder
    @SuppressLint("ScheduleExactAlarm")
    public void addReminder(Reminder reminder, Context context, @NonNull ReminderCallback callback) {
        String userId = getUserId();
        if (userId == null) {
            callback.onFailure("User not logged in.");
            return;
        }

        db.collection(USERS)
                .document(userId)
                .collection(PETS)
                .document(reminder.getPetId())
                .collection(REMINDERS)
                .add(reminder)
                .addOnSuccessListener(documentReference -> {
                    reminder.setReminderId(documentReference.getId());
                    ReminderNotificationHelper.scheduleReminder(context, reminder);
                    callback.onSuccess();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Failed to add reminder", e);
                    callback.onFailure(e.getMessage());
                });
    }

    // Update a reminder

    public void updateReminder(Reminder reminder, @NonNull ReminderCallback callback) {
        String userId = getUserId();
        if (userId == null || reminder.getReminderId() == null) {
            callback.onFailure("Invalid input or user not logged in.");
            return;
        }

        db.collection(USERS)
                .document(userId)
                .collection(PETS)
                .document(reminder.getPetId())
                .collection(REMINDERS)
                .document(reminder.getReminderId())
                .set(reminder)
                .addOnSuccessListener(unused -> {

                    callback.onSuccess();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Failed to update reminder", e);
                    callback.onFailure(e.getMessage());
                });
    }

    // Delete a reminder
    public void deleteReminder(Reminder reminder, @NonNull ReminderCallback callback) {
        String userId = getUserId();
        if (userId == null || reminder.getReminderId() == null) {
            callback.onFailure("Invalid input or user not logged in.");
            return;
        }

        db.collection(USERS)
                .document(userId)
                .collection(PETS)
                .document(reminder.getPetId())
                .collection(REMINDERS)
                .document(reminder.getReminderId())
                .delete()
                .addOnSuccessListener(unused -> {
                    callback.onSuccess();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Failed to delete reminder", e);
                    callback.onFailure(e.getMessage());
                });
    }

    // Fetch all reminders for a user using collectionGroup
    public void fetchAllReminders(@NonNull ReminderListCallback callback) {
        String userId = getUserId();
        if (userId == null) {
            callback.onFailure("User not logged in.");
            return;
        }

        // Step 1: Fetch all pets of user (to get pet names)
        db.collection(USERS)
                .document(userId)
                .collection(PETS)
                .get()
                .addOnSuccessListener(petsSnapshot -> {
                    // Map of petId -> petName
                    Map<String, String> petNameMap = new HashMap<>();
                    for (QueryDocumentSnapshot petDoc : petsSnapshot) {
                        String petId = petDoc.getId();
                        String petName = petDoc.getString("name");
                        if (petId != null && petName != null) {
                            petNameMap.put(petId, petName);
                        }
                    }

                    // Step 2: Fetch all reminders
                    db.collectionGroup(REMINDERS)
                            .whereEqualTo("userId", userId)
                            .orderBy("reminderTime")
                            .get()
                            .addOnSuccessListener(querySnapshot -> {
                                List<Reminder> reminders = new ArrayList<>();
                                for (QueryDocumentSnapshot doc : querySnapshot) {
                                    Reminder reminder = doc.toObject(Reminder.class);
                                    reminder.setReminderId(doc.getId());

                                    // Set petName from map
                                    if (reminder.getPetId() != null && petNameMap.containsKey(reminder.getPetId())) {
                                        reminder.setPetName(petNameMap.get(reminder.getPetId()));
                                    }

                                    reminders.add(reminder);
                                }
                                callback.onSuccess(reminders);
                            })
                            .addOnFailureListener(e -> {
                                Log.e(TAG, "Failed to fetch reminders", e);
                                callback.onFailure(e.getMessage());
                            });
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Failed to fetch pets for reminder names", e);
                    callback.onFailure(e.getMessage());
                });
    }


    // Mark reminder as complete/incomplete
    public void setReminderCompleted(Reminder reminder, boolean isCompleted, @NonNull ReminderCallback callback) {
        reminder.setCompleted(isCompleted);
        updateReminder(reminder, callback);
    }
}
