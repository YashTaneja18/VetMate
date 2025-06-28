package com.example.vetmate.data.model;

import com.google.firebase.Timestamp;

public class Reminder {
    private String reminderId;
    private String petId;
    private String userId;
    private String title;
    private String description;
    private boolean isCompleted;
    private Timestamp reminderTime;
    private String type; // e.g., "Deworming", "Appointment", "Medication"
    private transient String petName;  // UI-only, not stored in Firestore

    public String getPetName() {
        return petName;
    }

    public void setPetName(String petName) {
        this.petName = petName;
    }


    public Reminder() {
        // Required for Firebase
    }

    public Reminder(String reminderId, String petId, String userId, String title,
                    String description, boolean isCompleted, Timestamp reminderTime, String type ,String petName) {
        this.reminderId = reminderId;
        this.petId = petId;
        this.userId = userId;
        this.title = title;
        this.description = description;
        this.isCompleted = isCompleted;
        this.reminderTime = reminderTime;
        this.type = type;
        this.petName=petName;
    }

    // Getters and Setters

    public String getReminderId() {
        return reminderId;
    }

    public void setReminderId(String reminderId) {
        this.reminderId = reminderId;
    }

    public String getPetId() {
        return petId;
    }

    public void setPetId(String petId) {
        this.petId = petId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }

    public Timestamp getReminderTime() {
        return reminderTime;
    }

    public void setReminderTime(Timestamp reminderTime) {
        this.reminderTime = reminderTime;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
