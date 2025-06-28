package com.example.vetmate.data.repository;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.vetmate.data.model.Appointment;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.*;

import java.util.ArrayList;
import java.util.List;

public class AppointmentRepository {

    private static final String TAG = "AppointmentRepository";
    private static final String COLLECTION_VETS = "vets";
    private static final String COLLECTION_APPOINTMENTS = "appointments";

    private final FirebaseFirestore db;
    private final FirebaseAuth auth;

    private final MutableLiveData<List<Appointment>> appointmentsLiveData = new MutableLiveData<>();

    public AppointmentRepository() {
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
    }

    public LiveData<List<Appointment>> getAppointmentsLiveData() {
        return appointmentsLiveData;
    }

    // Load appointments for a clinic (real-time listener)
    public void loadAppointments(String vetId) {
        db.collection(COLLECTION_VETS)
                .document(vetId)
                .collection(COLLECTION_APPOINTMENTS)
                .orderBy("dateTime", Query.Direction.ASCENDING)
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Log.e(TAG, "Failed to fetch appointments: ", error);
                        return;
                    }

                    List<Appointment> appointments = new ArrayList<>();
                    if (value != null) {
                        for (DocumentSnapshot snapshot : value.getDocuments()) {
                            Appointment appointment = snapshot.toObject(Appointment.class);
                            if (appointment != null) {
                                appointment.setAppointmentId(snapshot.getId());
                                appointments.add(appointment);
                            }
                        }
                    }
                    appointmentsLiveData.setValue(appointments);
                });
    }

    // Add appointment
    public void addAppointment(String vetId, Appointment appointment, Callback callback) {
        appointment.setCreatedAt(Timestamp.now());

        db.collection(COLLECTION_VETS)
                .document(vetId)
                .collection(COLLECTION_APPOINTMENTS)
                .add(appointment)
                .addOnSuccessListener(docRef -> {
                    Log.d(TAG, "Appointment added with ID: " + docRef.getId());
                    callback.onSuccess();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Failed to add appointment", e);
                    callback.onFailure(e.getMessage());
                });
    }

    // Update appointment
    public void updateAppointment(String vetId, Appointment appointment, Callback callback) {
        if (appointment.getAppointmentId() == null) {
            callback.onFailure("Appointment ID is missing");
            return;
        }

        db.collection(COLLECTION_VETS)
                .document(vetId)
                .collection(COLLECTION_APPOINTMENTS)
                .document(appointment.getAppointmentId())
                .set(appointment)
                .addOnSuccessListener(aVoid -> callback.onSuccess())
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Failed to update appointment", e);
                    callback.onFailure(e.getMessage());
                });
    }

    // Delete appointment
    public void deleteAppointment(String vetId, String appointmentId, Callback callback) {
        db.collection(COLLECTION_VETS)
                .document(vetId)
                .collection(COLLECTION_APPOINTMENTS)
                .document(appointmentId)
                .delete()
                .addOnSuccessListener(aVoid -> callback.onSuccess())
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Failed to delete appointment", e);
                    callback.onFailure(e.getMessage());
                });
    }

    // Interface for callbacks
    public interface Callback {
        void onSuccess();
        void onFailure(String error);
    }
}
