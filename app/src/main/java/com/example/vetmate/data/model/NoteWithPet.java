package com.example.vetmate.data.model;

import com.google.firebase.Timestamp;

public class NoteWithPet {
    private String noteId;
    private String petId;
    private String title;
    private String body;
    private Timestamp createdAt;
    private Timestamp lastModified;
    private String petName;
    private String userId;

    public NoteWithPet() {
        // Required empty constructor
    }

    public Note toNote() {
        return new Note(userId, petId, title, body, createdAt, lastModified);
    }

    public NoteWithPet(String noteId, String petId, String title, String body, Timestamp createdAt, Timestamp lastModified, String petName) {
        this.noteId = noteId;
        this.petId = petId;
        this.title = title;
        this.body = body;
        this.createdAt = createdAt;
        this.lastModified = lastModified;
        this.petName = petName;
    }

    public String getNoteId() {
        return noteId;
    }

    public void setNoteId(String noteId) {
        this.noteId = noteId;
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

    public String getPetName() {
        return petName;
    }

    public void setPetName(String petName) {
        this.petName = petName;
    }
}
