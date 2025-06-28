package com.example.vetmate.data.model;

import com.google.firebase.Timestamp;

public class Appointment {

    private String appointmentId;
    private String userId;
    private String petId;
    private String petName;
    private Timestamp dateTime;
    private String status;       // "pending", "confirmed", "completed", "cancelled"
    private String notes;
    private Timestamp createdAt;

    // Required empty constructor for Firestore
    public Appointment() {
    }

    public Appointment(String appointmentId, String userId, String petId, String petName,
                       Timestamp dateTime, String status, String notes, Timestamp createdAt) {
        this.appointmentId = appointmentId;
        this.userId = userId;
        this.petId = petId;
        this.petName = petName;
        this.dateTime = dateTime;
        this.status = status;
        this.notes = notes;
        this.createdAt = createdAt;
    }

    // Getters and Setters
    public String getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(String appointmentId) {
        this.appointmentId = appointmentId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPetId() {
        return petId;
    }

    public void setPetId(String petId) {
        this.petId = petId;
    }

    public String getPetName() {
        return petName;
    }

    public void setPetName(String petName) {
        this.petName = petName;
    }

    public Timestamp getDateTime() {
        return dateTime;
    }

    public void setDateTime(Timestamp dateTime) {
        this.dateTime = dateTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }
}
