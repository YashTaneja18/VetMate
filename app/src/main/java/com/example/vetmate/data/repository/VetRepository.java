package com.example.vetmate.data.repository;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.vetmate.data.model.Appointment;
import com.example.vetmate.data.model.Vet;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class VetRepository {

    private static final String TAG = "VetRepository";
    private static final String COLLECTION_VETS = "vets";
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    // Save Vet Info
    public void saveVetProfile(String vetId, Vet vet, Callback callback) {
        db.collection(COLLECTION_VETS)
                .document(vetId)
                .set(vet)
                .addOnSuccessListener(aVoid -> callback.onSuccess())
                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }

    // Load Vet Profile
    public void getVetProfile(String vetId, VetCallback callback) {
        db.collection(COLLECTION_VETS)
                .document(vetId)
                .get()
                .addOnSuccessListener(snapshot -> {
                    if (snapshot.exists()) {
                        Vet vet = snapshot.toObject(Vet.class);
                        callback.onSuccess(vet);
                    } else {
                        callback.onFailure("Vet profile not found.");
                    }
                })
                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }

    // Create Appointment under /vets/{vetId}/appointments/
    public void createAppointment(String vetId, Appointment appointment, Callback callback) {
        CollectionReference appointmentRef = db.collection(COLLECTION_VETS)
                .document(vetId)
                .collection("appointments");

        appointmentRef.add(appointment)
                .addOnSuccessListener(documentRef -> callback.onSuccess())
                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }

    // Fetch Appointments for a Vet
    public LiveData<List<Appointment>> getAppointments(String vetId) {
        MutableLiveData<List<Appointment>> appointmentsLiveData = new MutableLiveData<>();

        db.collection(COLLECTION_VETS)
                .document(vetId)
                .collection("appointments")
                .addSnapshotListener((value, error) -> {
                    if (error != null || value == null) {
                        appointmentsLiveData.setValue(new ArrayList<>());
                        Log.e(TAG, "Error fetching appointments", error);
                        return;
                    }

                    List<Appointment> appointments = new ArrayList<>();
                    for (com.google.firebase.firestore.QueryDocumentSnapshot doc : value) {
                        Appointment a = doc.toObject(Appointment.class);
                        a.setAppointmentId(doc.getId());
                        appointments.add(a);
                    }

                    appointmentsLiveData.setValue(appointments);
                });

        return appointmentsLiveData;
    }

    // Update appointment status
    public void updateAppointmentStatus(String vetId, String appointmentId, String newStatus, Callback callback) {
        DocumentReference docRef = db.collection(COLLECTION_VETS)
                .document(vetId)
                .collection("appointments")
                .document(appointmentId);

        docRef.update("status", newStatus)
                .addOnSuccessListener(aVoid -> callback.onSuccess())
                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }

    // Interfaces
    public interface Callback {
        void onSuccess();
        void onFailure(String error);
    }

    public interface VetCallback {
        void onSuccess(Vet vet);
        void onFailure(String error);
    }
}
