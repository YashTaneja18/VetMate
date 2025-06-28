package com.example.vetmate.data.repository;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.vetmate.data.model.Pet;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class PetRepository {

    private static final String TAG = "PetRepository";
    private static final String COLLECTION_USERS = "users";
    private static final String COLLECTION_PETS = "pets";

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final FirebaseAuth auth = FirebaseAuth.getInstance();
    private final MutableLiveData<List<Pet>> petsLiveData = new MutableLiveData<>();

    public PetRepository() {
        loadPets(); // Attach snapshot listener on init
    }

    public LiveData<List<Pet>> getPets() {
        return petsLiveData;
    }

    // Load pets in real time
    private void loadPets() {
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser == null) {
            Log.w(TAG, "No authenticated user");
            petsLiveData.setValue(new ArrayList<>());
            return;
        }

        db.collection(COLLECTION_USERS)
                .document(currentUser.getUid())
                .collection(COLLECTION_PETS)
                .orderBy("name")
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Log.w(TAG, "Listen failed.", error);
                        return;
                    }

                    List<Pet> pets = new ArrayList<>();
                    if (value != null) {
                        for (QueryDocumentSnapshot doc : value) {
                            Pet pet = doc.toObject(Pet.class);
                            pet.setId(doc.getId());
                            pets.add(pet);
                        }
                    }
                    petsLiveData.setValue(pets);
                });
    }

    // Refresh pets manually
    public void refreshPets(RefreshCallback callback) {
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser == null) {
            callback.onError("No authenticated user found");
            return;
        }

        db.collection(COLLECTION_USERS)
                .document(currentUser.getUid())
                .collection(COLLECTION_PETS)
                .orderBy("name")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    List<Pet> pets = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : querySnapshot) {
                        Pet pet = doc.toObject(Pet.class);
                        pet.setId(doc.getId());
                        pets.add(pet);
                    }
                    petsLiveData.setValue(pets);
                    callback.onSuccess();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error refreshing pets", e);
                    callback.onError(e.getMessage());
                });
    }

    // Add new pet
    public void addPet(Pet pet, OnPetAddListener callback) {
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser == null) {
            callback.onFailure(new Exception("No authenticated user found"));
            return;
        }

        db.collection(COLLECTION_USERS)
                .document(currentUser.getUid())
                .collection(COLLECTION_PETS)
                .add(pet)
                .addOnSuccessListener(documentRef -> {
                    Log.d(TAG, "Pet added with ID: " + documentRef.getId());
                    callback.onSuccess();
                })
                .addOnFailureListener(callback::onFailure);
    }

    // Update existing pet
    public void updatePet(Pet pet, OnPetUpdateListener callback) {
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser == null) {
            callback.onFailure(new Exception("No authenticated user found"));
            return;
        }

        if (pet.getId() == null || pet.getId().isEmpty()) {
            callback.onFailure(new Exception("Pet ID is required for update"));
            return;
        }

        db.collection(COLLECTION_USERS)
                .document(currentUser.getUid())
                .collection(COLLECTION_PETS)
                .document(pet.getId())
                .set(pet)
                .addOnSuccessListener(unused -> {
                    Log.d(TAG, "Pet updated successfully");
                    callback.onSuccess();
                })
                .addOnFailureListener(callback::onFailure);
    }

    // Delete pet
    public void deletePet(String petId, OnPetDeleteListener callback) {
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser == null) {
            callback.onFailure(new Exception("No authenticated user found"));
            return;
        }

        if (petId == null || petId.isEmpty()) {
            callback.onFailure(new Exception("Pet ID is required for deletion"));
            return;
        }

        db.collection(COLLECTION_USERS)
                .document(currentUser.getUid())
                .collection(COLLECTION_PETS)
                .document(petId)
                .delete()
                .addOnSuccessListener(unused -> {
                    Log.d(TAG, "Pet deleted successfully");
                    callback.onSuccess();
                })
                .addOnFailureListener(callback::onFailure);
    }

    // Get pet by ID
    public void getPetById(String petId, GetPetCallback callback) {
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser == null) {
            callback.onError("No authenticated user found");
            return;
        }

        db.collection(COLLECTION_USERS)
                .document(currentUser.getUid())
                .collection(COLLECTION_PETS)
                .document(petId)
                .get()
                .addOnSuccessListener(snapshot -> {
                    if (snapshot.exists()) {
                        Pet pet = snapshot.toObject(Pet.class);
                        if (pet != null) pet.setId(snapshot.getId());
                        callback.onSuccess(pet);
                    } else {
                        callback.onError("Pet not found");
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error getting pet", e);
                    callback.onError(e.getMessage());
                });
    }

    // Callback Interfaces
    public interface RefreshCallback {
        void onSuccess();
        void onError(String error);
    }

    public interface GetPetCallback {
        void onSuccess(Pet pet);
        void onError(String error);
    }

    public interface OnPetAddListener {
        void onSuccess();
        void onFailure(Exception e);
    }

    public interface OnPetDeleteListener {
        void onSuccess();
        void onFailure(Exception e);
    }

    public interface OnPetUpdateListener {
        void onSuccess();
        void onFailure(Exception e);
    }
}
