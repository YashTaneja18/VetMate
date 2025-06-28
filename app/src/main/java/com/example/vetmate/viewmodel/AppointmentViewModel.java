package com.example.vetmate.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.vetmate.data.model.Appointment;
import com.example.vetmate.data.repository.AppointmentRepository;

import java.util.List;

public class AppointmentViewModel extends AndroidViewModel {

    private final AppointmentRepository appointmentRepository;
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();

    public AppointmentViewModel(@NonNull Application application) {
        super(application);
        appointmentRepository = new AppointmentRepository();
    }

    // Expose LiveData
    public LiveData<List<Appointment>> getAppointments() {
        return appointmentRepository.getAppointmentsLiveData();
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    private void setLoading(boolean loading) {
        isLoading.setValue(loading);
    }

    private void setErrorMessage(String message) {
        errorMessage.setValue(message);
    }

    private void clearErrorMessage() {
        errorMessage.setValue(null);
    }

    // Load all appointments for a clinic
    public void loadAppointments(String vetId) {
        setLoading(true);
        clearErrorMessage();
        appointmentRepository.loadAppointments(vetId);
        setLoading(false);
    }

    // Add appointment
    public void addAppointment(String vetId, Appointment appointment, AppointmentRepository.Callback callback) {
        setLoading(true);
        clearErrorMessage();

        appointmentRepository.addAppointment(vetId, appointment, new AppointmentRepository.Callback() {
            @Override
            public void onSuccess() {
                setLoading(false);
                callback.onSuccess();
            }

            @Override
            public void onFailure(String error) {
                setLoading(false);
                setErrorMessage(error);
                callback.onFailure(error);
            }
        });
    }

    // Update appointment
    public void updateAppointment(String vetId, Appointment appointment, AppointmentRepository.Callback callback) {
        setLoading(true);
        clearErrorMessage();

        appointmentRepository.updateAppointment(vetId, appointment, new AppointmentRepository.Callback() {
            @Override
            public void onSuccess() {
                setLoading(false);
                callback.onSuccess();
            }

            @Override
            public void onFailure(String error) {
                setLoading(false);
                setErrorMessage(error);
                callback.onFailure(error);
            }
        });
    }

    // Delete appointment
    public void deleteAppointment(String vetId, String appointmentId, AppointmentRepository.Callback callback) {
        setLoading(true);
        clearErrorMessage();

        appointmentRepository.deleteAppointment(vetId, appointmentId, new AppointmentRepository.Callback() {
            @Override
            public void onSuccess() {
                setLoading(false);
                callback.onSuccess();
            }

            @Override
            public void onFailure(String error) {
                setLoading(false);
                setErrorMessage(error);
                callback.onFailure(error);
            }
        });
    }
}
