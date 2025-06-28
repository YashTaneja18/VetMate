package com.example.vetmate.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.vetmate.data.model.Appointment;
import com.example.vetmate.data.model.Vet;
import com.example.vetmate.data.repository.VetRepository;

import java.util.List;

public class VetViewModel extends ViewModel {

    private final VetRepository vetRepository = new VetRepository();

    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>(null);
    private final MutableLiveData<Vet> vetProfile = new MutableLiveData<>();

    public LiveData<Boolean> getIsLoading() { return isLoading; }
    public LiveData<String> getErrorMessage() { return errorMessage; }
    public LiveData<Vet> getVetProfile() { return vetProfile; }

    public LiveData<List<Appointment>> getAppointments(String vetId) {
        return vetRepository.getAppointments(vetId);
    }

    public void loadVetProfile(String vetId) {
        isLoading.setValue(true);
        vetRepository.getVetProfile(vetId, new VetRepository.VetCallback() {
            @Override
            public void onSuccess(Vet vet) {
                vetProfile.setValue(vet);
                isLoading.setValue(false);
            }

            @Override
            public void onFailure(String error) {
                errorMessage.setValue(error);
                isLoading.setValue(false);
            }
        });
    }

    public void createAppointment(String vetId, Appointment appointment, VetRepository.Callback callback) {
        isLoading.setValue(true);
        vetRepository.createAppointment(vetId, appointment, new VetRepository.Callback() {
            @Override
            public void onSuccess() {
                isLoading.setValue(false);
                callback.onSuccess();
            }

            @Override
            public void onFailure(String error) {
                errorMessage.setValue(error);
                isLoading.setValue(false);
                callback.onFailure(error);
            }
        });
    }

    public void updateAppointmentStatus(String vetId, String appointmentId, String newStatus, VetRepository.Callback callback) {
        vetRepository.updateAppointmentStatus(vetId, appointmentId, newStatus, callback);
    }

    public void saveVetProfile(String vetId, Vet vet, VetRepository.Callback callback) {
        vetRepository.saveVetProfile(vetId, vet, callback);
    }
}
