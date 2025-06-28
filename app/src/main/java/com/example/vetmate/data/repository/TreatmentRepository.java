package com.example.vetmate.data.repository;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.vetmate.data.model.Treatment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class TreatmentRepository {

    private static final String TAG = "TreatmentRepository";
    private static final String COLLECTION_USERS = "users";
    private static final String COLLECTION_PETS = "pets";
    private static final String COLLECTION_TREATMENTS = "treatments";

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final FirebaseAuth auth = FirebaseAuth.getInstance();

    private final MutableLiveData<List<Treatment>> treatmentLiveData = new MutableLiveData<>();

    public LiveData<List<Treatment>> getTreatments() {
        return treatmentLiveData;
    }

    public void loadTreatments(String petId) {
        String userId = auth.getCurrentUser().getUid();

        db.collection(COLLECTION_USERS)
                .document(userId)
                .collection(COLLECTION_PETS)
                .document(petId)
                .collection(COLLECTION_TREATMENTS)
                .orderBy("date")
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Log.w(TAG, "Error fetching treatments", error);
                        return;
                    }

                    List<Treatment> treatments = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : value) {
                        Treatment treatment = doc.toObject(Treatment.class);
                        treatment.setId(doc.getId());
                        treatments.add(treatment);
                    }
                    treatmentLiveData.setValue(treatments);
                });
    }

    public void addTreatment(String petId, Treatment treatment, AddTreatmentCallback callback) {
        String userId = auth.getCurrentUser().getUid();

        db.collection(COLLECTION_USERS)
                .document(userId)
                .collection(COLLECTION_PETS)
                .document(petId)
                .collection(COLLECTION_TREATMENTS)
                .add(treatment)
                .addOnSuccessListener(docRef -> {
                    Log.d(TAG, "Treatment added");
                    callback.onSuccess();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error adding treatment", e);
                    callback.onError(e.getMessage());
                });
    }

    public interface AddTreatmentCallback {
        void onSuccess();
        void onError(String error);
    }
}
