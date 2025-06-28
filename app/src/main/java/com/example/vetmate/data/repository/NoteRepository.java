package com.example.vetmate.data.repository;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.vetmate.data.model.Note;
import com.example.vetmate.data.model.NoteWithPet;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class NoteRepository {

    private static final String TAG = "NoteRepository";
    private static final String USERS = "users";
    private static final String PETS = "pets";
    private static final String NOTES = "notes";

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final FirebaseAuth auth = FirebaseAuth.getInstance();

    public interface NoteCallback {
        void onSuccess();
        void onFailure(String error);
    }

    public interface SingleNoteCallback {
        void onSuccess(Note note);
        void onFailure(String error);
    }

    public interface NoteWithPetListCallback {
        void onSuccess(List<NoteWithPet> notes);
        void onFailure(String error);
    }

    private String getUserId() {
        if (auth.getCurrentUser() != null) {
            return auth.getCurrentUser().getUid();
        }
        return null;
    }

    public void addNote(@NonNull String petId, @NonNull Note note, @NonNull NoteCallback callback) {
        String userId = getUserId();
        if (userId == null) {
            callback.onFailure("User not logged in.");
            return;
        }

        note.setUserId(userId);
        note.setCreatedAt(Timestamp.now());
        note.setLastModified(Timestamp.now());

        db.collection(USERS)
                .document(userId)
                .collection(PETS)
                .document(petId)
                .collection(NOTES)
                .add(note)
                .addOnSuccessListener(ref -> callback.onSuccess())
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Failed to add note", e);
                    callback.onFailure(e.getMessage());
                });
    }

    public void updateNote(@NonNull String petId, @NonNull Note note, @NonNull NoteCallback callback) {
        String userId = getUserId();
        if (userId == null || note.getNoteId() == null) {
            callback.onFailure("User not logged in or note ID missing.");
            return;
        }

        note.setLastModified(Timestamp.now());

        db.collection(USERS)
                .document(userId)
                .collection(PETS)
                .document(petId)
                .collection(NOTES)
                .document(note.getNoteId())
                .set(note)
                .addOnSuccessListener(unused -> callback.onSuccess())
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Failed to update note", e);
                    callback.onFailure(e.getMessage());
                });
    }

    public void deleteNote(@NonNull String petId, @NonNull String noteId, @NonNull NoteCallback callback) {
        String userId = getUserId();
        if (userId == null) {
            callback.onFailure("User not logged in.");
            return;
        }

        db.collection(USERS)
                .document(userId)
                .collection(PETS)
                .document(petId)
                .collection(NOTES)
                .document(noteId)
                .delete()
                .addOnSuccessListener(unused -> callback.onSuccess())
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Failed to delete note", e);
                    callback.onFailure(e.getMessage());
                });
    }

    public void getNoteById(@NonNull String petId, @NonNull String noteId, @NonNull SingleNoteCallback callback) {
        String userId = getUserId();
        if (userId == null) {
            callback.onFailure("User not logged in.");
            return;
        }

        DocumentReference noteRef = db.collection(USERS)
                .document(userId)
                .collection(PETS)
                .document(petId)
                .collection(NOTES)
                .document(noteId);

        noteRef.get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        Note note = doc.toObject(Note.class);
                        if (note != null) note.setNoteId(doc.getId());
                        callback.onSuccess(note);
                    } else {
                        callback.onFailure("Note not found.");
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Failed to get note by ID", e);
                    callback.onFailure(e.getMessage());
                });
    }

    public void fetchAllNotesWithPetNames(@NonNull NoteWithPetListCallback callback) {
        String userId = getUserId();
        if (userId == null) {
            callback.onFailure("User not logged in.");
            return;
        }

        db.collectionGroup(NOTES)
                .whereEqualTo("userId", userId)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    List<NoteWithPet> result = new ArrayList<>();
                    if (querySnapshot.isEmpty()) {
                        callback.onSuccess(result);
                        return;
                    }

                    final int total = querySnapshot.size();
                    final int[] count = {0};

                    for (QueryDocumentSnapshot doc : querySnapshot) {
                        Note note = doc.toObject(Note.class);
                        note.setNoteId(doc.getId());

                        // Extract petId from Firestore path
                        String path = doc.getReference().getPath();
                        String[] segments = path.split("/");
                        String petId = "";
                        for (int i = 0; i < segments.length - 1; i++) {
                            if (segments[i].equals("pets")) {
                                petId = segments[i + 1];
                                break;
                            }
                        }
                        note.setPetId(petId);

                        db.collection(USERS)
                                .document(userId)
                                .collection(PETS)
                                .document(petId)
                                .get()
                                .addOnSuccessListener(petDoc -> {
                                    String petName = petDoc.exists() ? petDoc.getString("name") : "Unknown Pet";
                                    NoteWithPet noteWithPet = new NoteWithPet(
                                            note.getNoteId(),
                                            note.getPetId(),
                                            note.getTitle(),
                                            note.getBody(),
                                            note.getCreatedAt(),
                                            note.getLastModified(),
                                            petName
                                    );
                                    result.add(noteWithPet);
                                    count[0]++;
                                    if (count[0] == total) {
                                        callback.onSuccess(result);
                                    }
                                })
                                .addOnFailureListener(e -> {
                                    Log.e(TAG, "Failed to get pet name", e);
                                    count[0]++;
                                    if (count[0] == total) {
                                        callback.onSuccess(result);
                                    }
                                });
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Failed to fetch notes with pet names", e);
                    callback.onFailure(e.getMessage());
                });
    }
}
