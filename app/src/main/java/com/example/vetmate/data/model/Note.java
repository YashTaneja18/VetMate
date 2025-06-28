package com.example.vetmate.data.model;

import com.google.firebase.Timestamp;

public class Note {

    private String noteId;         // Firestore document ID
    private String userId;         // User UID (for global collectionGroup query)
    private String petId;          // Pet ID (for linking and indexing)
    private String title;
    private String body;
    private Timestamp createdAt;
    private Timestamp lastModified;

    // Required empty constructor for Firestore
    public Note() {}

    public Note(String userId, String petId, String title, String body, Timestamp createdAt, Timestamp lastModified) {
        this.userId = userId;
        this.petId = petId;
        this.title = title;
        this.body = body;
        this.createdAt = createdAt;
        this.lastModified = lastModified;
    }

    // Getters & Setters
    public String getNoteId() {
        return noteId;
    }

    public void setNoteId(String noteId) {
        this.noteId = noteId;
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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public Timestamp getLastModified() {
        return lastModified;
    }

    public void setLastModified(Timestamp lastModified) {
        this.lastModified = lastModified;
    }
}
